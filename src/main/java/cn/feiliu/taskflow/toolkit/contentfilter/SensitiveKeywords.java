/*
 * Copyright © 2024 Taskflow, Inc.
 *
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

import java.util.HashMap;
import java.util.Map;

public class SensitiveKeywords {
    private String                                  keyword;
    private boolean                                 isEnd;
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
