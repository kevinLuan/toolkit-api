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

import java.util.List;

public class SensitiveWordFilter {
    private final SensitiveKeywords root;

    SensitiveWordFilter(SensitiveKeywords sensitiveKeywords) {
        this.root = sensitiveKeywords;
    }

    public static SensitiveWordFilter of(SensitiveKeywords sensitiveKeywords) {
        return new SensitiveWordFilter(sensitiveKeywords);
    }

    public String filter(String content) {
        return new SensitiveWordParser().search(content, root).process();
    }

    /**
     * 解析存在的敏感词
     *
     * @param str 文本
     * @return 存在的敏感词token
     */
    public List<KeywordToken> parser(String str) {
        return new SensitiveWordParser().parser(str, root);
    }

    public SensitiveKeywords getNode() {
        return this.root;
    }
}
