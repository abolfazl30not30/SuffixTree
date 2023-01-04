package org.example;

public class Main {
    public static void main(String[] args) {
        // Test
        GeneralizedSuffixTree suffixTree = new GeneralizedSuffixTree();

        String[] texts = new String[]{"computerScience",
                "Tree",
                "Suffix",
                "generator",
                "apPlication",
                "containsAllStructure",
                "javaApp",
                "simsinalti",
                "application",
                "banana"};
        for (int i = 0; i < 10; i++) {
            suffixTree.put(texts[i], i);
        }

        System.out.println(suffixTree.search("java"));
        System.out.println(suffixTree.search("application"));
        System.out.println(suffixTree.search("tree"));
    }
}