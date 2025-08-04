import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Necessario informar a dimensao do tabuleiro.");
        }

        Integer dimension = Integer.parseInt(args[0]);

        Integer sqrt = (int) Math.sqrt(dimension);

        if (sqrt * sqrt != dimension) {
            throw new IllegalArgumentException("Dimensao do tabuleiro deve ser um quadrado perfeito.");
        }

        List<List<Integer>> board = new ArrayList<>();

        for (Integer linha = 0; linha < dimension; linha++) {
            board.add(new ArrayList<>());

            printLinha(linha % sqrt == 0, dimension);

            for (Integer coluna = 0; coluna < dimension; coluna++) {
                board.get(linha).add(coluna, 0);

                String pipe = coluna % sqrt == 0 ? "\033[34m|\033[m " : "| ";

                System.out.print(pipe + board.get(linha).get(coluna) + " ");
            }

            System.out.println("\033[34m|\033[m");
        }
        printLinha(true, dimension);
    }

    public static void printLinha(Boolean blue, Integer colunas) {
        System.out.print(' ');
        String dash = blue ? "\033[34m-" : "-";
        String linha = dash.repeat(colunas * 4);
        System.out.println(linha.substring(0, linha.length() - 1));
    }
}