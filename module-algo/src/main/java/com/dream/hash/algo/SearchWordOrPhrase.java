package com.dream.hash.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 有一些 docId 以及 doc 对应的 content，要支持 search 单词 和 phraseSearch
 * 希望 dot 和 comma 可以兼容掉，全部按照小写来搜索。
 *
 * docId → Content
 * 1 → hello world, Cloud monitoring is great
 * 2 → Cloud computing is xxx, and Cloud monitoring yyyy
 * 3 → Cloud computing is xxx, and Cloud computing is yyy, and monitoring is zzzz
 * search(”cloud”) → 1,2,3
 * search(”computing”) → 2,3
 * phraseSearch(”cloud monitoring”) → 1, 2
 */
public class SearchWordOrPhrase {

    private static class Pair {
        private final int docId;
        private final int wordIndex;

        private Pair(int docId, int wordIndex) {
            this.docId = docId;
            this.wordIndex = wordIndex;
        }

        public int getDocId() {
            return docId;
        }

        public int getWordIndex() {
            return wordIndex;
        }
    }

    // key is docId, value is the word array.
    private final Map<Integer, String[]> originalContents;

    // key is word, value is the docId and word index in the content.
    private final Map<String, Set<Pair>> map;

    public SearchWordOrPhrase(Map<Integer, String> contents) {
        this.originalContents = new HashMap<>();
        this.map = new HashMap<>();
        for (Map.Entry<Integer, String> contentEntry : contents.entrySet()) {
            int docId = contentEntry.getKey();
            String content = contentEntry.getValue();
            String[] words = content.toLowerCase().replace(".", "").replace(",", "").split(" ");

            originalContents.put(docId, words);
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                final int index = i;
                map.compute(word, (s, set) -> {
                    if (set == null) {
                        set = new HashSet<>();
                    }
                    set.add(new Pair(docId, index));
                    return set;
                });
            }
        }
    }

    public Set<Integer> search(String word) {
        return map.get(word).stream().map(Pair::getDocId).collect(Collectors.toSet());
    }

    public Set<Integer> phraseSearch(String phrase) {
        String[] searchWords = phrase.split(" ");
        Set<Pair> pairs = map.get(searchWords[0]);
        Set<Integer> result = new HashSet<>();
        for (Pair pair : pairs) {
            int currentDocId = pair.docId;
            // Skip search if current doc has included this phrase.
            if (result.contains(currentDocId)) {
                continue;
            }
            for (int i = 0; i <= searchWords.length; i++) {
                if (i == searchWords.length) {
                    // All words are matched, add currentDocId to the result.
                    result.add(currentDocId);
                    break;
                }
                String currentWord = searchWords[i];
                int currentWordIndex = pair.getWordIndex() + i;
                // Stop match if any word are not equal
                if (!originalContents.get(currentDocId)[currentWordIndex].equals(currentWord)) {
                    break;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        final HashMap<Integer, String> contents = new HashMap<>();
        contents.put(1, "hello world, Cloud monitoring is great.");
        contents.put(2, "Cloud computing is xxx, and Cloud monitoring yyyy.");
        contents.put(3, "Cloud computing is xxx, and Cloud computing is yyy, and monitoring is zzzz.");
        SearchWordOrPhrase searchWordOrPhrase = new SearchWordOrPhrase(contents);

        System.out.println(searchWordOrPhrase.search("cloud"));
        System.out.println(searchWordOrPhrase.search("computing"));

        System.out.println(searchWordOrPhrase.phraseSearch("cloud computing"));
        System.out.println(searchWordOrPhrase.phraseSearch("cloud monitoring"));
    }

}
