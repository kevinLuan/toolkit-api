package cn.feiliu.taskflow.toolkit.contentfilter;

import java.util.LinkedList;

public class SensitiveWordParser {

  private LinkedList<KeywordTokenizer> wordTokens = new LinkedList<>();
  private String content;

  public SensitiveWordParser search(String word, SensitiveKeywords root) {
    this.content = word;
    char[] chars = word.toCharArray();
    SensitiveKeywords node = root;
    int count = 0;

    for (int i = 0; i < chars.length;) {
      char ch = chars[i + count];
      count++;
      node = findNode(node, ch);

      if (node == null) {
        node = root;
        i++;
        count = 0;
      } else if (node.isEnd()) {
        KeywordTokenizer wordToken = new KeywordTokenizer(node.getKeyword(),i);
        wordTokens.add(wordToken);

        if (node.isLeaf()) {
          i++;
          node = root;
          count = 0;
        }
      }
    }
    return this;
  }

  private SensitiveKeywords findNode(SensitiveKeywords node, char ch) {
    return node.getChild(ch);
  }

  public String process() {
    StringBuilder builder = new StringBuilder(this.content);
    while (!wordTokens.isEmpty()) {
      KeywordTokenizer token = wordTokens.removeLast();
      if (builder.length() >= token.getEndPosition()) {
        builder.replace(token.getStartPosition(), token.getEndPosition(), "*");
      }
    }
    return builder.toString();
  }

  /**
   * 解析敏感词Token
   * @param str
   * @param node
   * @return
   */
  public LinkedList<KeywordTokenizer> parser(String str, SensitiveKeywords node) {
    search(str, node);
    return wordTokens;
  }
}
