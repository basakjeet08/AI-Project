package algo.generation;

/**
 * This class stores an information about the particular edge in a passage tree.
 * It stores two coordinates corresponding to the cells locations in a grid.
 * Cells locations are consider as they would be in 1-dimensional array and
 * are calculated using the {row * width + column} formula.
 */
class Edge {

    /**
     * The coordinate of the cells
     */
    private final int firstCell;

    private final int secondCell;

    /**
     * Creates a new edge with given cells coordinates.
     */
    Edge(int firstCell, int secondCell) {
        this.firstCell = firstCell;
        this.secondCell = secondCell;
    }

    /**
     * return the cell coordinates.
     */
    int getFirstCell() {
        return firstCell;
    }

    int getSecondCell() {
        return secondCell;
    }
}
