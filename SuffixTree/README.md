
# Generalized Suffix Tree

Allows for fast storage and fast(er) retrieval by creating a tree-based index out of a set of strings.
Unlike common suffix trees, which are generally used to build an index out of one (very) long string, a *Generalized Suffix Tree* can be used to build an index over many strings.

Its main operations are `put` and `search`:

* `put` adds the given key to the index, allowing for later retrieval of the given value.
* `search` can be used to retrieve the set of all the values that were put in the index with keys that contain a given input.

In particular, after `put(K, V)`, `search(H)` will return a set containing `V` for any string `H` that is substring of `K`.

The overall complexity of the retrieval operation (`search`) is *O(m)* where *m* is the length of the string to search within the index.

## Explanations about the classes of this project:
### main:
This class is for testing the `GeneralizedSuffixTree` class and its methods

### Structure:
* Represents an Edge in the Suffix Tree.
* It has a label and a destination Node

### Struct:
* A specialized implementation of Map that uses native char types and sorted arrays to keep minimize the memory footprint.
* Implements only the operations that are needed within the suffix tree context.
* `sortArray` :
  * A trivial implementation of sort, used to sort chars[] and values[] according to the data in chars.
  * It was preferred to faster sorts (like qsort) because of the small sizes (<=36) of the collections involved.

### Node:
* Represents a node of the generalized suffix tree graph
* `getData` :
    * Returns the first <tt>numElements</tt> elements from the ones associated to this node.
    * Gets data from the payload of both this node and its children, the string representation of the path to this node is a substring of the one of the children nodes.
    * @param numElements the number of results to return. Use -1 to get all
    * @return the first <tt>numElements</tt> associated to this node and children
* `addRef` :
    * Adds the given <tt>index</tt> to the set of indexes associated with <tt>this</tt>

* `contain` :
    * Tests whether a node contains a reference to the given index.
    * <b>IMPORTANT</b>: it works because the array is sorted by construction
    * @param index the index to look for
    * @return true <tt>this</tt> contains a reference to index

* `computeAndCacheCount` :
    * Computes the number of results that are stored on this node and on its children, and caches the result.
    * Performs the same operation on subnodes as well
    * @return the number of results
* `getResultCount` :
    * Returns the number of results that are stored on this node and on its
    * children.
    * Should be called after having called computeAndCacheCount.
    * @throws IllegalStateException when this method is called without having called
    * computeAndCacheCount first
    * @see Node#computeAndCacheCount()

## Differences from the original suffix tree

Although the implementation is based on the original design by Ukkonen, there are a few aspects where it differs significantly.

The tree is composed of a set of nodes and labeled edges. The labels on the edges can have any length as long as it's greater than 0.
The only constraint is that no two edges going out from the same node start with the same character.

Because of this, a given _(startNode, stringSuffix)_ pair can denote a unique path within the tree, and it is the path (if any) that can be composed by sequentially traversing all the edges _(e1, e2, …)_ starting from _startNode_ such that _(e1.label + e2.label + …)_ is equal to the _stringSuffix_.
See the `GeneralizedSuffixTree#search` method for details.

The union of all the edge labels from the root to a given leaf node denotes the set of the strings explicitly contained within the GST.
In addition to those Strings, there are a set of different strings that are implicitly contained within the GST, and it is composed of the strings built by concatenating _e1.label + e2.label + ... + $end_, where _e1, e2, …_ is a proper path and _$end_ is prefix of any of the labels of the edges starting from the last node of the path.

This kind of "implicit path" is important in the testAndSplit method.


