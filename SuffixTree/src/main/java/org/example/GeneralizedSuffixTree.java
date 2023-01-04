package org.example;

import java.util.Collection;
import java.util.Collections;

public class GeneralizedSuffixTree {

    // The index of the last item that was added to the GST
    private int last = 0;

    // The root of the suffix tree
    private final Node root = new Node();

     // The last leaf that was added during the update operation
    private Node activeLeaf = root;

    public Collection<Integer> search(String word) {
        return search(word, -1);
    }

    public Collection<Integer> search(String word, int results) {
        Node tmpNode = searchNode(word);
        if (tmpNode == null) {
            return Collections.EMPTY_LIST;
        }
        return tmpNode.getData(results);
    }

    // Returns the tree node (if present) that corresponds to the given string.
    private Node searchNode(String word) {
        /*
         * Verifies if exists a path from the root to a node such that the concatenation
         * of all the labels on the path is a superstring of the given word.
         * If such a path is found, the last node on it is returned.
         */
        Node currentNode = root;
        Structure currentEdge;

        for (int i = 0; i < word.length(); ++i) {
            char ch = word.charAt(i);
            // follow the edge corresponding to this char
            currentEdge = currentNode.getEdge(ch);
            if (null == currentEdge) {
                // there is no edge starting with this char
                return null;
            } else {
                String label = currentEdge.getLabel();
                int lenToMatch = Math.min(word.length() - i, label.length());
                if (!word.regionMatches(i, label, 0, lenToMatch)) {
                    // the label on the edge does not correspond to the one in the string to search
                    return null;
                }

                if (label.length() >= word.length() - i) {
                    return currentEdge.getDest();
                } else {
                    // advance to next node
                    currentNode = currentEdge.getDest();
                    i += lenToMatch - 1;
                }
            }
        }

        return null;
    }

    public void put(String key, int index) throws IllegalStateException {
        if (index < last) {
            throw new IllegalStateException("The input index must not be less than any of the previously inserted ones. Got " + index + ", expected at least " + last);
        } else {
            last = index;
        }

        // reset activeLeaf
        activeLeaf = root;

        Node s = root;

        // proceed with tree construction (closely related to procedure in
        // Ukkonen's paper)
        StringBuilder text = new StringBuilder();
        // iterate over the string, one char at a time
        for (int i = 0; i < key.length(); i++) {
            // line 6
            text.append(key.charAt(i));
            // use intern to make sure the resulting string is in the pool.
            text = new StringBuilder(text.toString().intern());

            // line 7: update the tree with the new transitions due to this new char
            Pair<Node, String> active = update(s, text.toString(), key.substring(i), index);
            // line 8: make sure the active pair is canonical
            active = canonize(active.getFirst(), active.getSecond());

            s = active.getFirst();
            text = new StringBuilder(active.getSecond());
        }

        // add leaf suffix link, is necessary
        if (null == activeLeaf.getSuffix() && activeLeaf != root && activeLeaf != s) {
            activeLeaf.setSuffix(s);
        }

    }

    private Pair<Boolean, Node> testAndSplit(final Node inputs, final String stringPart, final char t, final String remainder, final int value) {
        // descend the tree as far as possible
        Pair<Node, String> ret = canonize(inputs, stringPart);
        Node s = ret.getFirst();
        String str = ret.getSecond();

        if (!"".equals(str)) {
            Structure g = s.getEdge(str.charAt(0));

            String label = g.getLabel();
            // must see whether "str" is substring of the label of an edge
            if (label.length() > str.length() && label.charAt(str.length()) == t) {
                return new Pair<>(true, s);
            } else {
                // need to split the edge
                String newlabel = label.substring(str.length());
                assert (label.startsWith(str));

                // build a new node
                Node r = new Node();
                // build a new edge
                Structure newedge = new Structure(str, r);

                g.setLabel(newlabel);

                // link s -> r
                r.addEdge(newlabel.charAt(0), g);
                s.addEdge(str.charAt(0), newedge);

                return new Pair<>(false, r);
            }

        } else {
            Structure e = s.getEdge(t);
            if (null == e) {
                // if there is no t-transtion from s
                return new Pair<>(false, s);
            } else {
                if (remainder.equals(e.getLabel())) {
                    // update payload of destination node
                    e.getDest().addRef(value);
                    return new Pair<>(true, s);
                } else if (remainder.startsWith(e.getLabel())) {
                    return new Pair<>(true, s);
                } else if (e.getLabel().startsWith(remainder)) {
                    // need to split as above
                    Node newNode = new Node();
                    newNode.addRef(value);

                    Structure newEdge = new Structure(remainder, newNode);

                    e.setLabel(e.getLabel().substring(remainder.length()));

                    newNode.addEdge(e.getLabel().charAt(0), e);

                    s.addEdge(t, newEdge);

                    return new Pair<>(false, s);
                } else {
                    // they are different words. No prefix. but they may still share some common substr
                    return new Pair<>(true, s);
                }
            }
        }

    }

    private Pair<Node, String> canonize(final Node s, final String inputstr) {

        if ("".equals(inputstr)) {
            return new Pair<>(s, inputstr);
        } else {
            Node currentNode = s;
            String str = inputstr;
            Structure g = s.getEdge(str.charAt(0));
            // descend the tree as long as a proper label is found
            while (g != null && str.startsWith(g.getLabel())) {
                str = str.substring(g.getLabel().length());
                currentNode = g.getDest();
                if (str.length() > 0) {
                    g = currentNode.getEdge(str.charAt(0));
                }
            }

            return new Pair<>(currentNode, str);
        }
    }
    private Pair<Node, String> update(final Node inputNode, final String stringPart, final String rest, final int value) {
        Node s = inputNode;
        String tempstr = stringPart;
        char newChar = stringPart.charAt(stringPart.length() - 1);

        // line 1
        Node oldroot = root;

        // line 1b
        Pair<Boolean, Node> ret = testAndSplit(s, tempstr.substring(0, tempstr.length() - 1), newChar, rest, value);

        Node r = ret.getSecond();
        boolean endpoint = ret.getFirst();

        Node leaf;
        // line 2
        while (!endpoint) {
            // line 3
            Structure tempEdge = r.getEdge(newChar);
            if (null != tempEdge) {
                // such a node is already present. This is one of the main differences from Ukkonen's case:
                // the tree can contain deeper nodes at this stage because different strings were added by previous iterations.
                leaf = tempEdge.getDest();
            } else {
                // must build a new leaf
                leaf = new Node();
                leaf.addRef(value);
                Structure newedge = new Structure(rest, leaf);
                r.addEdge(newChar, newedge);
            }

            // update suffix link for newly created leaf
            if (activeLeaf != root) {
                activeLeaf.setSuffix(leaf);
            }
            activeLeaf = leaf;

            // line 4
            if (oldroot != root) {
                oldroot.setSuffix(r);
            }

            // line 5
            oldroot = r;

            // line 6
            if (null == s.getSuffix()) { // root node
                assert (root == s);
                // this is a special case to handle what is referred to as node _|_ on the paper
                tempstr = tempstr.substring(1);
            } else {
                Pair<Node, String> canret = canonize(s.getSuffix(), safeCutLastChar(tempstr));
                s = canret.getFirst();
                // use intern to ensure that tempstr is a reference from the string pool
                tempstr = (canret.getSecond() + tempstr.charAt(tempstr.length() - 1)).intern();
            }

            // line 7
            ret = testAndSplit(s, safeCutLastChar(tempstr), newChar, rest, value);
            r = ret.getSecond();
            endpoint = ret.getFirst();

        }

        // line 8
        if (oldroot != root) {
            oldroot.setSuffix(r);
        }

        return new Pair<>(s, tempstr);
    }

    private String safeCutLastChar(String seq) {
        if (seq.length() == 0) {
            return "";
        }
        return seq.substring(0, seq.length() - 1);
    }

    /**
     * A private class used to return a tuples of two elements
     */
    private class Pair<A, B> {

        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }
}