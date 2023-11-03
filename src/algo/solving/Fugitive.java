package algo.solving;

import model.Cell;
import java.util.*;
import static java.util.Comparator.comparingInt;
import static model.Cell.Type.ESCAPE;

/**
 * This class is used for finding an escape path from the maze
 * entrance to the maze exit. It is the implementation of the
 * 
 * A* search algorithm</a>.
 */
public class Fugitive {

    /**
     * Moves to up, left, right and down from the current cell.
     */
    private static final int[][] DELTAS = { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };

    /**
     * The height and width of the maze in nodes.
     */
    private final int height;

    private final int width;

    /**
     * Two-dimensional array of nodes representing maze.
     */
    private final Node[][] grid;

    /**
     * The start and end points to find a path from.
     */
    private final Node start;

    private final Node end;

    /**
     * A priority queue to perform the selection of minimum
     * estimated cost node on every step of the algorithm.
     */
    private final PriorityQueue<Node> open = new PriorityQueue<>(comparingInt(Node::getFinalCost));

    /**
     * Already processed nodes.
     */
    private final Set<Node> closed = new HashSet<>();

    /**
     * Constructs a new object with given grid of cells
     * and start and end cells. Creates a grid of nodes
     * based on that.
     */
    public Fugitive(Cell[][] grid, Cell start, Cell end) {
        this.height = grid.length;
        this.width = grid[0].length;
        this.grid = new Node[height][width];
        this.start = new Node(start.getRow(), start.getColumn(), false);
        this.end = new Node(end.getRow(), end.getColumn(), false);
        createNodes(grid);
    }

    /**
     * For each cell in a given grid it creates the corresponding
     * node in a grid of nodes. Calculates an estimated cost
     * to the end for each node.
     */
    private void createNodes(Cell[][] grid) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var node = new Node(i, j, grid[i][j].isWall());
                node.calcHeuristicTo(end);
                this.grid[i][j] = node;
            }
        }
    }

    /**
     * Find a path from the start to the end using
     * A* search algorithm</a>.
     */
    public List<Cell> findEscape() {
        open.add(start);
        while (!open.isEmpty()) {
            var cur = open.poll();
            if (isEnd(cur))
                return reconstructPath(cur);
            closed.add(cur);
            updateNeighbors(cur);
        }
        return new ArrayList<>();
    }

    /**
     * Check if a node is the end point to find a path to.
     */
    private boolean isEnd(Node currentNode) {
        return currentNode.equals(end);
    }

    /**
     * Reconstructs the path from the given node to the
     * start node, i.e. node having no parent. Returns a
     * list of cells in the format: start -> ... -> cur.
     */
    private List<Cell> reconstructPath(Node cur) {
        var path = new LinkedList<Cell>();
        path.add(toCell(cur));
        while (cur.getParent() != cur) {
            var parent = cur.getParent();
            path.addFirst(toCell(parent));
            cur = parent;
        }
        return path;
    }

    /**
     * Converts a node back to the cell format.
     * Cell type is escape path.
     */
    private Cell toCell(Node node) {
        return new Cell(node.getRow(), node.getColumn(), ESCAPE);
    }

    /**
     * Updates an estimated and a final costs of neighboring
     * cells according to the A* search algorithm</a>.
     */
    private void updateNeighbors(Node cur) {
        for (var delta : DELTAS) {
            var row = cur.getRow() + delta[0];
            var column = cur.getColumn() + delta[1];
            if (inBounds(row, column)) {
                var node = grid[row][column];
                if (!node.isWall() && !closed.contains(node)) {
                    if (open.contains(node)) {
                        if (node.hasBetterPath(cur)) {
                            open.remove(node);
                        } else {
                            continue;
                        }
                    }
                    node.updatePath(cur);
                    open.add(node);
                }
            }
        }
    }

    /**
     * Checks if given cell indices are in bounds
     * of the 2-dimensional array.
     */
    private boolean inBounds(int row, int column) {
        return row >= 0 && row < height
                && column >= 0 && column < width;
    }
}
