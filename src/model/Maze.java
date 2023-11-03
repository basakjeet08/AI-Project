package model;

import algo.generation.PassageTree;
import algo.solving.Fugitive;
import java.util.function.Consumer;
import static model.Cell.Type.PASSAGE;
import static model.Cell.Type.WALL;

/**
 * This class encapsulates the internal representation of the maze and provides
 * methods for creating, managing and extracting information about it.
 */
public class Maze {

    /**
     * The height and width of the maze in cells.
     */
    private final int height;

    private final int width;

    /**
     * Two-dimensional array of cells representing maze.
     */
    private final Cell[][] grid;

    /**
     * Indicates if a method for solving the maze has already
     * been called. It is used to prevent recalculation.
     */
    private boolean isSolved = false;

    /**
     * Generates a new maze of given height and width.
     *
     * @param height height of a maze
     * @param width  width of a maze
     */
    public Maze(int height, int width) {
        if (height < 3 || width < 3) {
            throw new IllegalArgumentException(
                    "Both the height and the width " +
                            "of the maze must be at least 3");
        }
        this.height = height;
        this.width = width;
        grid = new Cell[height][width];
        fillGrid();
    }

    /**
     * Generates a new square maze of a given size.
     */
    public Maze(int size) {
        this(size, size);
    }

    /**
     * Fills the cells with the new maze such that the maze becomes
     * simply connected, i.e. containing no loops and no detached walls.
     */
    private void fillGrid() {
        fillAlternately();
        fillGaps();
        makeEntranceAndExit();
        generatePassages();
    }

    /**
     * Creates a new cell with given coordinates and a type in the grid.
     */
    private void putCell(int row, int column, Cell.Type type) {
        grid[row][column] = new Cell(row, column, type);
    }

    /**
     * Fills every second cell with a passage and the others with a wall.
     * After this method, a maze looks like this:
     * 
     * <pre>
     * ██████████
     * ██  ██  ██
     * ██████████
     * ██  ██  ██
     * ██████████
     * </pre>
     */
    private void fillAlternately() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((i & 1) == 0 || (j & 1) == 0) {
                    putCell(i, j, WALL);
                } else {
                    putCell(i, j, PASSAGE);
                }
            }
        }
    }

    /**
     * If the maze has an even height or width it is needed to fill the
     * last row or column of the grid with a wall (or, otherwise, it will
     * contain passages at the outer border).
     * 
     * <pre>
     * ████████████
     * ██  ██  ██
     * ████████████
     * ██  ██  ██
     * ████████████
     * ██  ██  ██
     * </pre>
     * 
     * becomes
     * 
     * <pre>
     * ████████████
     * ██  ██  ████
     * ████████████
     * ██  ██  ████
     * ████████████
     * ████████████
     * </pre>
     */
    private void fillGaps() {
        if (height % 2 == 0)
            wallLastRow();
        if (width % 2 == 0)
            wallLastColumn();
    }

    /**
     * Fills the last column in the grid with a wall.
     */
    private void wallLastColumn() {
        for (int i = 0; i < height; i++)
            putCell(i, width - 1, WALL);
    }

    /**
     * Fills the last row in the grid with a wall.
     */
    private void wallLastRow() {
        for (int i = 0; i < width; i++)
            putCell(height - 1, i, WALL);
    }

    /**
     * Calculates the index of the passage in the last row. For a maze
     * with an odd (1) and even (2) width its indices differ:
     * 
     * <pre>
     * (1) ██████  ██
     * (2) ██████  ████
     * </pre>
     *
     * @return the index of the passage in the last row
     */
    private int getExitColumn() {
        return width - 3 + width % 2;
    }

    /**
     * Puts entrance and exit passages to upper left and lower right
     * corners. For example:
     * 
     * <pre>
     * ████████████
     * ██  ██  ████
     * ████████████
     * ██  ██  ████
     * ████████████
     * ████████████
     * </pre>
     * 
     * becomes
     * 
     * <pre>
     * ██  ████████
     * ██  ██  ████
     * ████████████
     * ██  ██  ████
     * ██████  ████
     * ██████  ████
     * </pre>
     */
    private void makeEntranceAndExit() {
        putCell(0, 1, PASSAGE);
        putCell(height - 1, getExitColumn(), PASSAGE);
        if (height % 2 == 0)
            putCell(height - 2, getExitColumn(), PASSAGE);
    }

    /**
     * Creates random passages between isolated passage cells such
     * that every cell is connected to the other in one way and
     * has no cycles. For example:
     * 
     * <pre>
     * ██  ██████
     * ██  ██  ██
     * ██████████
     * ██  ██  ██
     * ██████  ██
     * </pre>
     * 
     * becomes
     * 
     * <pre>
     * ██  ██████
     * ██      ██
     * ██████  ██
     * ██      ██
     * ██████  ██
     * </pre>
     */
    private void generatePassages() {
        new PassageTree(height, width)
                .generate()
                .forEach(putCell());
    }

    /**
     * Puts a cell in the corresponding place in grid.
     */
    private Consumer<Cell> putCell() {
        return cell -> grid[cell.getRow()][cell.getColumn()] = cell;
    }

    /**
     * Finds a path in the maze from its entrance to its exit.
     * For example:
     * 
     * <pre>
     * ██░░██████████
     * ██░░░░░░██  ██
     * ██████░░██  ██
     * ██    ░░    ██
     * ██████░░██████
     * ██    ░░░░░░██
     * ██████████░░██
     * </pre>
     *
     * If this method is called several times, the path is not
     * recalculated. It is stored in the grid so it is returned
     * immediately.
     * 
     */
    public String findEscape() {
        if (!isSolved) {
            new Fugitive(grid, getEntrance(), getExit())
                    .findEscape()
                    .forEach(putCell());
            isSolved = true;
        }
        return toString(true);
    }

    /**
     * Return the entrance cell.
     */
    private Cell getEntrance() {
        return grid[0][1];
    }

    /**
     * Return the exit cell.
     */
    private Cell getExit() {
        return grid[height - 1][getExitColumn()];
    }

    /**
     * Return the string representation of the grid. The path
     * from the entrance to the exit can be displayed if it
     * is already found and {showEscape -> true}.
     * The path is found on demand.
     *
     * <p>
     * For example:
     * 
     * {showEscape -> true}
     * 
     * <pre>
     * ██░░██████████
     * ██░░░░░░██  ██
     * ██████░░██  ██
     * ██    ░░    ██
     * ██████░░██████
     * ██    ░░░░░░██
     * ██████████░░██
     * </pre>
     * 
     * showEscape -> false}
     * 
     * <pre>
     * ██  ██████████
     * ██      ██  ██
     * ██████  ██  ██
     * ██          ██
     * ██████  ██████
     * ██          ██
     * ██████████  ██
     * </pre>
     */
    private String toString(boolean showEscape) {
        var sb = new StringBuilder();
        for (var row : grid) {
            for (var cell : row) {
                if (cell.isWall()) {
                    sb.append("██");
                } else if (showEscape && cell.isEscape()) {
                    sb.append("▓▓");
                } else {
                    sb.append("  ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Return the string representation of the grid.
     * The path is never displayed.
     */
    @Override
    public String toString() {
        return toString(false);
    }
}