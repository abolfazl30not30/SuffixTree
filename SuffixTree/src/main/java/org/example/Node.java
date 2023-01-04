package org.example;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Node {
    private int[] data;
    /**
     * Represents index of the last position used in the data int[] array.
     * It should always be less than data.length
     */
    private int lastIdx = 0;
    /**
     * The starting size of the int[] array containing the payload
     */
    private static final int START_SIZE = 0;
    /**
     * The increment in size used when the payload array is full
     */
    private static final int INCREMENT = 1;
    /**
     * The set of edges starting from this node
     */
    private final Map<Character, Structure> edges;
    private Node suffix;

    /**
     * Creates a new Node
     */
    Node() {
        edges = new Struct();
        suffix = null;
        data = new int[START_SIZE];
    }
    Collection<Integer> getData() {
        return getData(-1);
    }

    Collection<Integer> getData(int numElements) {
        Set<Integer> ret = new HashSet<>();
        for (int num : data) {
            ret.add(num);
            if (ret.size() == numElements) {
                return ret;
            }
        }
        // need to get more matches from child nodes. This is what may waste time
        for (Structure e : edges.values()) {
            if (-1 == numElements || ret.size() < numElements) {
                for (int num : e.getDest().getData()) {
                    ret.add(num);
                    if (ret.size() == numElements) {
                        return ret;
                    }
                }
            }
        }
        return ret;
    }

    void addRef(int index) {
        if (contains(index)) {
            return;
        }

        addIndex(index);

        // add this reference to all the suffixes as well
        Node iter = this.suffix;
        while (iter != null) {
            if (iter.contains(index)) {
                break;
            }
            iter.addRef(index);
            iter = iter.suffix;
        }

    }

    private boolean contains(int index) {
        int low = 0;
        int high = lastIdx - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = data[mid];

            if (midVal < index)
                low = mid + 1;
            else if (midVal > index)
                high = mid - 1;
            else
                return true;
        }
        return false;
    }

    void addEdge(char ch, Structure e) {
        edges.put(ch, e);
    }

    Structure getEdge(char ch) {
        return edges.get(ch);
    }

    Node getSuffix() {
        return suffix;
    }

    void setSuffix(Node suffix) {
        this.suffix = suffix;
    }

    private void addIndex(int index) {
        if (lastIdx == data.length) {
            int[] copy = new int[data.length + INCREMENT];
            System.arraycopy(data, 0, copy, 0, data.length);
            data = copy;
        }
        data[lastIdx++] = index;
    }
}