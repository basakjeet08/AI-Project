package algo.generation;

import static java.util.stream.IntStream.range;

public class DisjointSet {

    /**
     * parent -> Representatives for disjoint subsets. If the set consists
     * only of the one element its parent equals to its id. Otherwise, it
     * is the next element up the tree.
     * 
     * rank -> Heights of the trees corresponding to the subsets.
     * A subset with one element has a rank of zero.
     */
    private final int[] parent;

    private final int[] rank;

    /**
     * Constructs a disjoint set of @param size disjoint
     * subsets.
     */
    public DisjointSet(int size) {
        parent = new int[size];
        rank = new int[size];
        range(0, size).forEach(this::makeSet);
    }

    /**
     * Initializes a particular set.
     * 
     */
    private void makeSet(int i) {
        parent[i] = i;
        rank[i] = 0;
    }

    /**
     * Finds a representative for the set. If the set consists
     * only of the one element its parent equals to its id.
     *
     */
    public int find(int i) {
        if (i != parent[i])
            parent[i] = find(parent[i]);
        return parent[i];
    }

    /**
     * Merges two disjoint sets into one by the ids
     * if their ids are not in the same set already.
     */
    public boolean union(int i, int j) {
        var iRoot = find(i);
        var jRoot = find(j);
        if (iRoot == jRoot)
            return false;
        if (rank[iRoot] < rank[jRoot]) {
            parent[iRoot] = jRoot;
        } else {
            parent[jRoot] = iRoot;
            if (rank[iRoot] == rank[jRoot])
                rank[iRoot]++;
        }
        return true;
    }
}
