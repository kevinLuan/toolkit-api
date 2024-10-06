package cn.feiliu.taskflow.toolkit.contentfilter;

import java.util.HashMap;
import java.util.Map;

public class SensitiveKeywords {
    private String keyword;
    private boolean isEnd;
    private final Map<Character, SensitiveKeywords> children;

    private SensitiveKeywords() {
        this.isEnd = false;
        this.children = new HashMap<>();
    }

    public static SensitiveKeywords buildTrie(String[] keywords) {
        final SensitiveKeywords root = new SensitiveKeywords();
        for (String word : keywords) {
            SensitiveKeywords currentNode = root;
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                currentNode = currentNode.children.computeIfAbsent(ch, k -> new SensitiveKeywords());
                if (i == word.length() - 1) {
                    currentNode.isEnd = true;
                    currentNode.keyword = word;
                }
            }
        }
        return root;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isEnd() {
        return isEnd;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public SensitiveKeywords getChild(char ch) {
        return children.get(ch);
    }
}
