import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudokuGenerator {
    private final int SIZE = 9;
    private final int BLOCK_SIZE = 3;
    private final int TOTAL_CELLS = SIZE * SIZE;
    private Random random = new Random();
    private List<Set<Integer>> possibilities;
    private SudokuGrid grid;

    public SudokuGenerator() {
        grid = new SudokuGrid();
        initializePossibilities();
    }

    private void initializePossibilities() {
        possibilities = new ArrayList<>(TOTAL_CELLS);
        Set<Integer> allValues = IntStream.rangeClosed(1, SIZE)
                .boxed()
                .collect(Collectors.toSet());

        for (int i = 0; i < TOTAL_CELLS; i++) {
            possibilities.add(new HashSet<>(allValues));
        }
    }

    public SudokuGrid generate() {
        int maxAttempts = 10;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                grid.clearGrid();
                initializePossibilities();
                if (collapseWaveFunction()) {
                    return grid;
                }
            } catch (IllegalStateException e) {
                //collapse failed
                System.out.println("Attempt " + (attempt + 1) + " failed: " + e.getMessage());
            }
        }
        System.out.println("Failed to generate valid Sudoku after " + maxAttempts + " attempts");
        return null;
    }

    private boolean collapseWaveFunction() {
        while (true) {
            Optional<Cell> minCell = findMinimumEntropyCell();//finding the cell with the min amount of possibilities

            if (!minCell.isPresent()) {
                return true;//all cells collapsed
            }

            Cell cell = minCell.get();
            Set<Integer> cellPossibilities = possibilities.get(cell.index);

            if (cellPossibilities.isEmpty()) {
                throw new IllegalStateException("Contradiction found at cell " + cell.index);
            }


            List<Integer> possibleList = new ArrayList<>(cellPossibilities);
            int chosenValue = possibleList.get(random.nextInt(possibleList.size()));//randomly choose a value

            collapseCell(cell.row, cell.col, chosenValue); //collapse cell to chosen value

            if (!applyConstraints(cell.row, cell.col, chosenValue)) {
                return false;
            }
        }
    }

    private Optional<Cell> findMinimumEntropyCell() {
        return IntStream.range(0, TOTAL_CELLS)
                .filter(i -> possibilities.get(i).size() > 1) // Only consider uncollapsed cells
                .boxed()
                .min(Comparator.comparingInt(i -> possibilities.get(i).size()))
                .map(i -> new Cell(i / SIZE, i % SIZE, i));
    }

    private void collapseCell(int row, int col, int value) {
        int index = row * SIZE + col;
        possibilities.get(index).clear();
        possibilities.get(index).add(value);
        grid.setValue(row, col, value);
    }

    private boolean applyConstraints(int row, int col, int value) {
        for (int c = 0; c < SIZE; c++) { //remove value from same row
            if (c != col && !removePossibility(row, c, value)) {
                return false;
            }
        }

        for (int r = 0; r < SIZE; r++) {//remove value from same column
            if (r != row && !removePossibility(r, col, value)) {
                return false;
            }
        }

        int blockRow = (row / BLOCK_SIZE) * BLOCK_SIZE;//remove values from same block
        int blockCol = (col / BLOCK_SIZE) * BLOCK_SIZE;//remove values from same block

        for (int r = blockRow; r < blockRow + BLOCK_SIZE; r++) {
            for (int c = blockCol; c < blockCol + BLOCK_SIZE; c++) {
                if ((r != row || c != col) && !removePossibility(r, c, value)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean removePossibility(int row, int col, int value) {
        int index = row * SIZE + col;
        Set<Integer> cellPossibilities = possibilities.get(index);

        if (cellPossibilities.size() == 1) {
            return cellPossibilities.contains(value) ? false : true;
        }

        boolean removed = cellPossibilities.remove(value); //remove possibility

        if (removed && cellPossibilities.size() == 1) {//if only one value remains finalize it
            int remainingValue = cellPossibilities.iterator().next();
            collapseCell(row, col, remainingValue);
            return applyConstraints(row, col, remainingValue);
        }

        return true;
    }

    private static class Cell {
        int row, col, index;

        Cell(int row, int col, int index) {
            this.row = row;
            this.col = col;
            this.index = index;
        }
    }
}
