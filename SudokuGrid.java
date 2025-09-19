import java.util.Set;
import java.util.EnumSet;
import java.util.Arrays;

public class SudokuGrid {
    private final int SIZE = 9;  //sudoku size
    private static final int BLOCK_SIZE = 3; //size of the 3x3 block
    private int[][] grid = new int[SIZE][SIZE];

    public enum ConflictType{
        ROW_CONFLICT, COLUMN_CONFLICT, BLOCK_CONFLICT
    }

    public SudokuGrid() {
        clearGrid();
    }

    public void clearGrid() {
        for(int row=0; row<SIZE; row++) {
            Arrays.fill(grid[row], 0);
        }
    }

    public void setValue(int row, int col, int value) {
        this.grid[row][col] = value;
    }
    public int getValue(int row, int col) {
        return this.grid[row][col];
    }

    public Set<ConflictType> getConflicts(int row, int col, int value) {
        Set<ConflictType> conflicts = EnumSet.noneOf(ConflictType.class);
        if(isInRow(row,col,value)){
            conflicts.add(ConflictType.ROW_CONFLICT);
        }
        if(isInColumn(row,col, value))
        {
            conflicts.add(ConflictType.COLUMN_CONFLICT);
        }
        if(isInBlock(row,col,value))
        {
            conflicts.add(ConflictType.BLOCK_CONFLICT);
        }
        return conflicts;
    }

    public boolean isValidPlacement(int row, int col, int value)
    {
        return getConflicts(row,col,value).isEmpty();
    }

    private boolean isInRow(int row, int col, int value) {
        for(int c= 0; c<SIZE; c++) {
            if(c!= col && grid[row][c] == value)
            {
                return true;//duplicates found in row
            }
        }
        return false;
    }

    private boolean isInColumn(int row, int col, int value) {
        for (int r = 0; r < SIZE; r++) {
            if (r != row && grid[r][col] == value) {
                return true; //duplicates found in column
            }
        }
        return false;
    }


    private boolean isInBlock(int row, int col, int value) {
        int blockRowStart = (row / BLOCK_SIZE) * BLOCK_SIZE;
        int blockColStart = (col / BLOCK_SIZE) * BLOCK_SIZE;

        for (int r = blockRowStart; r < blockRowStart + BLOCK_SIZE; r++) {
            for (int c = blockColStart; c < blockColStart + BLOCK_SIZE; c++) {
                if ((r != row || c != col) && grid[r][c] == value) {
                    return true;//duplicates found in the block
                }
            }
        }
        return false;
    }

    public void displayGrid() {
        System.out.println("-------------------------");
        for (int row = 0; row < SIZE; row++) {
            System.out.print("| ");
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];
                System.out.print(value == 0 ? "." : value);
                System.out.print(" ");
                if ((col + 1) % BLOCK_SIZE == 0 && col != SIZE - 1) {
                    System.out.print("| ");
                }
            }
            System.out.println("|");

            if ((row + 1) % BLOCK_SIZE == 0 && row != SIZE - 1) {
                System.out.println("-------------------------");
            }
        }
        System.out.println("-------------------------");
    }


    public int countInvalidBoxes()
    {
        int invalidCount = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];
                if (value != 0 && !isValidPlacement(row, col, value)) {
                    invalidCount++;
                }
            }
        }
        return invalidCount;

    }
}
