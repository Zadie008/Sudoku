import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SudokuGenerator generator = new SudokuGenerator();

        System.out.println("Press enter to generate the board");

        scanner.nextLine();

        try {
            SudokuGrid board = generator.generate();

            if (board != null) {
                System.out.println("\nGenerated Sudoku Board:");
                board.displayGrid();

                int invalidCount = board.countInvalidBoxes();
                System.out.println("Invalid boxes: " + invalidCount);
                System.out.println(invalidCount == 0 ? "Status: VALID" : "Status: INVALID");
            } else {
                System.out.println("failed to generate a valid board :(");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}