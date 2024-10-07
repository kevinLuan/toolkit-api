/*
 * Copyright © 2024 Taskflow, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.feiliu.taskflow.toolkit.contentfilter;

import java.util.LinkedList;

public class SensitiveWordParser {

    private LinkedList<KeywordToken> wordTokens = new LinkedList<>();
    private String                   content;

    public SensitiveWordParser search(String content, SensitiveKeywords root) {
        this.content = content;
        char[] chars = content.toCharArray();
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
                KeywordToken wordToken = new KeywordToken(node.getKeyword(), i);
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
            KeywordToken token = wordTokens.removeLast();
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
    public LinkedList<KeywordToken> parser(String str, SensitiveKeywords node) {
        search(str, node);
        return wordTokens;
    }
}
