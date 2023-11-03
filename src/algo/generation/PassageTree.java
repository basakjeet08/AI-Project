package algo.generation;

import model.Cell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static model.Cell.Type.PASSAGE;

/**
 * This class is used for creating random passages between
 * isolated passage cells such that every cell is connected
 * to the other in one way and the maze has no cycles.
 * Actually, it is a tree.
 * <p>
 * For example:
 * 
 * <pre>
 * ██  ██████
 * ██  ██  ██
 * ██████████
 * ██  ██  ██
 * ██████  ██
 * </pre>
 * 
 * can become
 * 
 * <pre>
 * ██  ██████
 * ██      ██
 * ██████  ██
 * ██      ██
 * ██████  ██
 * </pre>
 * <p>
 * Internally, it creates a tree of edges between cells in a square
 * of doubly decreased size such that all cells are in the one
 * 
 * Initially, there is a set of all possible edges in the small grid.
 * 
 * On each step of the algorithm it removes one of the edges such
 * that an edge connects two distinct connected components.
 * 
 * At the end, each cell is connected to the others in a one way.
 * 
 */
public class PassageTree {

    /**
     * The height of the maze in an imaginary edge form.
     */
    private final int height;

    /**
     * The width of the maze in an imaginary edge form.
     */
    private final int width;

    /**
     * Creates a new imaginary edge form
     */
    public PassageTree(int height, int width) {
        this.height = (height - 1) / 2;
        this.width = (width - 1) / 2;
    }

    /**
     * Generates a random list of cells that connect passages in
     * an original form such that a maze is simply connected.
     */
    public List<Cell> generate() {
        var edges = createEdges();
        Collections.shuffle(edges);
        var tree = buildRandomSpanningTree(edges);
        return createPassages(tree);
    }

    /**
     * Creates a list of all possible edges in an imaginary edge form
     */
    private List<Edge> createEdges() {
        var edges = new ArrayList<Edge>();
        for (int column = 1; column < width; column++) {
            edges.add(new Edge(toIndex(0, column),
                    toIndex(0, column - 1)));
        }
        for (int row = 1; row < height; row++) {
            edges.add(new Edge(toIndex(row, 0),
                    toIndex(row - 1, 0)));
        }
        for (int row = 1; row < height; row++) {
            for (int column = 1; column < width; column++) {
                edges.add(new Edge(toIndex(row, column),
                        toIndex(row, column - 1)));
                edges.add(new Edge(toIndex(row, column),
                        toIndex(row - 1, column)));
            }
        }
        return edges;
    }

    /**
     * Transforms the coordinates in a 2-dimensional array
     * to the coordinate in a 1-dimensional array using the
     * {row * width + column} formula.
     */
    private int toIndex(int row, int column) {
        return row * width + column;
    }

    /**
     * Generates a list of edges that connect passages.
     */
    private List<Edge> buildRandomSpanningTree(List<Edge> edges) {
        var disjointSets = new DisjointSet(width * height);
        return edges
                .stream()
                .filter(edge -> connects(edge, disjointSets))
                .collect(toList());
    }

    /**
     * Checks if an {@code edge} connects two disjoint subsets.
     */
    private boolean connects(Edge edge, DisjointSet disjointSet) {
        return disjointSet.union(edge.getFirstCell(), edge.getSecondCell());
    }

    /**
     * Scales and converts edges in an imaginary edge form to the cells
     * which connect passages in an original form.
     */
    private List<Cell> createPassages(List<Edge> spanningTree) {
        return spanningTree
                .stream()
                .map(edge -> {
                    var first = fromIndex(edge.getFirstCell());
                    var second = fromIndex(edge.getSecondCell());
                    return getPassage(first, second);
                }).collect(toList());
    }

    /**
     * Transforms the coordinate in a 1-dimensional array
     * back to the coordinates in a 2-dimensional array using the
     * {@code row = index / width} and {@code column = index % width}
     * formulas.
     */
    private Cell fromIndex(int index) {
        var row = index / width;
        var column = index % width;
        return new Cell(row, column, PASSAGE);
    }

    /**
     * Given the coordinates of two cells that compose an edge in
     * an imaginary edge form, it scales and transforms them to
     * the coordinates of the cell that connect passages in an
     * original form. Returns a passage cell with these coordinates.
     */
    private Cell getPassage(Cell first, Cell second) {
        var row = first.getRow() + second.getRow() + 1;
        var column = first.getColumn() + second.getColumn() + 1;
        return new Cell(row, column, PASSAGE);
    }
}
