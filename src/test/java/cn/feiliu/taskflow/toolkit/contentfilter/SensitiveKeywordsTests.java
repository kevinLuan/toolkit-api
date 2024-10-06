/*
 * Copyright 2024 Taskflow, Inc.
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

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;

/**
 * Test class for SensitiveKeywords and SensitiveWordFilter
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-10-06
 */
public class SensitiveKeywordsTests {
    private static SensitiveWordFilter filterService;
    private static String              testString;
    private static String[]            keywords;

    @BeforeClass
    public static void setUp() {
        testString = "最近搞到一本黄色小说，《维奥莱特罗曼史》，习作者马努里伯爵夫人-习大-习大大";
        keywords = new String[] { "搞到", "黄色", "黄", "习", "习大大", "习大", "中国" };
        filterService = SensitiveWordFilter.of(SensitiveKeywords.buildTrie(keywords));
    }

    @Test
    public void testParserWithAllKeywords() {
        LinkedList<KeywordTokenizer> list = filterService.parser(testString);
        assertEquals("Should find 9 sensitive keywords", 9, list.size());

        // Verify some specific tokens
        assertTrue("Should contain '搞到' at position 2", 
            list.stream().anyMatch(token -> "搞到".equals(token.getToken()) && token.getStartPosition() == 2));
        assertTrue("Should contain '黄色' at position 6", 
            list.stream().anyMatch(token -> "黄色".equals(token.getToken()) && token.getStartPosition() == 6));
        assertTrue("Should contain '习大大' at position 35", 
            list.stream().anyMatch(token -> "习大大".equals(token.getToken()) && token.getStartPosition() == 35));
    }

    @Test
    public void testFilterWithAllKeywords() {
        String filteredString = filterService.filter(testString);
        String expectedString = "最近*一本*小说，《维奥莱特罗曼史》，*作者马努里伯爵夫人-*-*";
        assertEquals("Filtered string should match expected string", expectedString, filteredString);
    }

    @Test
    public void testFilterWithSubsetOfKeywords() {
        String str = "最近搞到一本黄色小说，《维奥莱特罗曼史》，习作者马努里伯爵夫人-习大-习大大";
        String[] subsetKeywords = new String[] { "黄色", "黄", "习", "习大大", "习大", "中国" };
        SensitiveWordFilter subsetFilter = SensitiveWordFilter.of(SensitiveKeywords.buildTrie(subsetKeywords));
        String filteredString = subsetFilter.filter(str);
        assertEquals("Filtered string should match expected string with subset of keywords",
            "最近搞到一本*小说，《维奥莱特罗曼史》，*作者马努里伯爵夫人-*-*", filteredString);
    }

    @Test
    public void testHandleTextWithoutSensitiveKeywords() {
        String safeString = "这是一个安全的字符串，不包含敏感词。";
        String filteredString = filterService.filter(safeString);
        assertEquals("Safe string should remain unchanged", safeString, filteredString);
    }

    @Test
    public void testHandleEmptyInput() {
        String emptyString = "";
        String filteredString = filterService.filter(emptyString);
        assertEquals("Empty string should remain unchanged", emptyString, filteredString);
    }

    @Test
    public void testBuildTrieWithEmptyKeywords() {
        String[] emptyKeywords = new String[] {};
        SensitiveWordFilter emptyFilter = SensitiveWordFilter.of(SensitiveKeywords.buildTrie(emptyKeywords));
        String testStr = "This string contains no sensitive words.";
        assertEquals("String should remain unchanged with empty keywords", testStr, emptyFilter.filter(testStr));
    }
}
