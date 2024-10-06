package cn.feiliu.taskflow.toolkit.contentfilter;

import java.util.LinkedList;

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
    public LinkedList<KeywordTokenizer> parser(String str) {
        return new SensitiveWordParser().parser(str, root);
    }

    public SensitiveKeywords getNode() {
        return this.root;
    }
}
