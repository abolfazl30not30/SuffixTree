package org.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

class Struct implements Map<Character, Structure> {
    private byte[] chars;

    private Structure[] values;

    private static final int BSEARCH_THRESHOLD = 6;

    @Override
    public Structure put(Character character, Structure e) {
        char c = character;
        if (c != (char) (byte) c) {
            throw new IllegalArgumentException("Illegal input character " + c + ".");
        }

        if (chars == null) {
            chars = new byte[0];
            values = new Structure[0];
        }
        int idx = search(c);
        Structure previous = null;

        if (idx < 0) {
            int currsize = chars.length;
            byte[] copy = new byte[currsize + 1];
            System.arraycopy(chars, 0, copy, 0, currsize);
            chars = copy;
            Structure[] copy1 = new Structure[currsize + 1];
            System.arraycopy(values, 0, copy1, 0, currsize);
            values = copy1;
            chars[currsize] = (byte) c;
            values[currsize] = e;
            currsize++;
            if (currsize > BSEARCH_THRESHOLD) {
                sortArrays();
            }
        } else {
            previous = values[idx];
            values[idx] = e;
        }
        return previous;
    }

    @Override
    public Structure get(Object maybeCharacter) {
        return get(((Character) maybeCharacter).charValue());  // throws if cast fails.
    }

    public Structure get(char c) {
        if (c != (char) (byte) c) {
            throw new IllegalArgumentException("Illegal input character " + c + ".");
        }

        int idx = search(c);
        if (idx < 0) {
            return null;
        }
        return values[idx];
    }

    private int search(char c) {
        if (chars == null)
            return -1;

        if (chars.length > BSEARCH_THRESHOLD) {
            return java.util.Arrays.binarySearch(chars, (byte) c);
        }

        for (int i = 0; i < chars.length; i++) {
            if (c == chars[i]) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Collection<Structure> values() {
        return Arrays.asList(values == null ? new Structure[0] : values);
    }

    private void sortArrays() {
        for (int i = 0; i < chars.length; i++) {
            for (int j = i; j > 0; j--) {
                if (chars[j-1] > chars[j]) {
                    byte swap = chars[j];
                    chars[j] = chars[j-1];
                    chars[j-1] = swap;

                    Structure swapEdge = values[j];
                    values[j] = values[j-1];
                    values[j-1] = swapEdge;
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return chars == null || chars.length == 0;
    }

    @Override
    public int size() {
        return chars == null ? 0 : chars.length;
    }

    @Override
    public Set<Entry<Character, Structure>> entrySet() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<Character> keySet() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Structure> m) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Structure remove(Object key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean containsValue(Object key) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
