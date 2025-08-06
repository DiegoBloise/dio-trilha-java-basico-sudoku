import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Necessário informar a dimensão do tabuleiro.");
        }

        int dimension = Integer.parseInt(args[0]);
        int sqrt = (int) Math.sqrt(dimension);

        if (sqrt * sqrt != dimension) {
            throw new IllegalArgumentException("Dimensão do tabuleiro deve ser um quadrado perfeito.");
        }

        int[][] solution = new int[dimension][dimension];
        int[][] board = new int[dimension][dimension];
        boolean[][] fixed = new boolean[dimension][dimension];

        fillBoard(solution, dimension, sqrt);
        copyWithHiddenCells(solution, board, fixed, sqrt);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printBoard(board, fixed, dimension, sqrt);

            if (isBoardComplete(board)) {
                if (isSolutionCorrect(board, solution)) {
                    System.out.println("\n\033[1;32mParabéns! Você completou corretamente o Sudoku!\033[m");
                    break;
                } else {
                    System.out.println("\n\033[1;31mExistem erros no tabuleiro! Corrija para vencer.\033[m");
                }
            }

            System.out.print("\nInforme jogada (valor coluna linha) ou '0 0 0' para reiniciar: ");
            String input = scanner.nextLine().trim();

            String[] parts = input.split("\\s+");
            if (parts.length != 3) {
                System.out.println("Entrada inválida. Use: coluna linha valor");
                continue;
            }

            try {
                int value = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]) - 1;
                int row = Integer.parseInt(parts[2]) - 1;

                if (value == 0 && col == -1 && row == -1) {
                    resetBoard(board, fixed);
                    System.out
                            .println("\n\033[33mTabuleiro reiniciado. Todas as células editáveis foram limpas.\033[m");
                    continue;
                }

                if (row < 0 || row >= dimension || col < 0 || col >= dimension || value < 1 || value > dimension) {
                    System.out.println("Valores fora dos limites.");
                    continue;
                }

                if (fixed[row][col]) {
                    System.out.println("Essa célula não pode ser alterada.");
                    continue;
                }

                board[row][col] = value;

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Use apenas números.");
            }
        }
        scanner.close();
    }

    public static void printBoard(int[][] board, boolean[][] fixed, int dimension, int sqrt) {
        System.out.print("    ");
        for (int c = 0; c < dimension; c++) {
            System.out.printf("%-3d", c + 1);
        }
        System.out.println();

        for (int row = 0; row < dimension; row++) {
            if (row % sqrt == 0)
                printLinha(true, dimension);

            System.out.printf("%-3d", row + 1);
            for (int col = 0; col < dimension; col++) {
                String pipe = col % sqrt == 0 ? "\033[34m|\033[m " : "| ";
                String value = board[row][col] == 0 ? " " : Integer.toString(board[row][col]);

                if (fixed[row][col]) {
                    System.out.print(pipe + "\033[1m" + value + "\033[m ");
                } else {
                    System.out.print(pipe + value + " ");
                }
            }
            System.out.println("\033[34m|\033[m");
        }
        printLinha(true, dimension);
    }

    public static void printLinha(boolean blue, int colunas) {
        System.out.print("    ");
        String dash = blue ? "\033[34m-" : "-";
        String linha = dash.repeat(colunas * 4);
        System.out.println(linha.substring(0, linha.length() - 1));
    }

    public static boolean fillBoard(int[][] board, int dimension, int sqrt) {
        return solve(board, 0, 0, dimension, sqrt);
    }

    public static boolean solve(int[][] board, int row, int col, int dimension, int sqrt) {
        if (row == dimension)
            return true;
        if (col == dimension)
            return solve(board, row + 1, 0, dimension, sqrt);

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= dimension; i++)
            numbers.add(i);
        Collections.shuffle(numbers, new Random());

        for (int num : numbers) {
            if (isSafe(board, row, col, num, dimension, sqrt)) {
                board[row][col] = num;
                if (solve(board, row, col + 1, dimension, sqrt))
                    return true;
                board[row][col] = 0;
            }
        }

        return false;
    }

    public static boolean isSafe(int[][] board, int row, int col, int num, int dimension, int sqrt) {
        for (int i = 0; i < dimension; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false;
        }

        int startRow = row - row % sqrt;
        int startCol = col - col % sqrt;

        for (int i = 0; i < sqrt; i++) {
            for (int j = 0; j < sqrt; j++) {
                if (board[startRow + i][startCol + j] == num)
                    return false;
            }
        }

        return true;
    }

    public static void copyWithHiddenCells(int[][] source, int[][] target, boolean[][] fixed, int sqrt) {
        int dimension = source.length;
        Random rand = new Random();

        for (int blockRow = 0; blockRow < sqrt; blockRow++) {
            for (int blockCol = 0; blockCol < sqrt; blockCol++) {
                List<int[]> cells = new ArrayList<>();
                for (int i = 0; i < sqrt; i++) {
                    for (int j = 0; j < sqrt; j++) {
                        cells.add(new int[] { blockRow * sqrt + i, blockCol * sqrt + j });
                    }
                }

                Collections.shuffle(cells);
                int hideCount = sqrt * sqrt / 2;
                Set<String> hidden = new HashSet<>();

                for (int i = 0; i < cells.size(); i++) {
                    int[] cell = cells.get(i);
                    int r = cell[0], c = cell[1];

                    if (i < hideCount) {
                        target[r][c] = 0;
                        fixed[r][c] = false;
                    } else {
                        target[r][c] = source[r][c];
                        fixed[r][c] = true;
                    }
                }
            }
        }
    }

    public static boolean isBoardComplete(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0)
                    return false;
            }
        }
        return true;
    }

    public static boolean isSolutionCorrect(int[][] board, int[][] solution) {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != solution[i][j])
                    return false;
            }
        }
        return true;
    }

    public static void resetBoard(int[][] board, boolean[][] fixed) {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!fixed[i][j]) {
                    board[i][j] = 0;
                }
            }
        }
    }
}
