package cn.feiliu.taskflow.toolkit.contentfilter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-06
 */
public class SensitiveKeywordsTests {
    private static SensitiveWordFilter filterService;
    private static String testString;
    private static String[] keywords;

    @BeforeClass
    public static void setUp() {
        testString = "最近搞到一本黄色小说，《维奥莱特罗曼史》，习作者马努里伯爵夫人-习大-习大大";
        keywords = new String[]{"搞到", "黄色", "黄", "习", "习大大", "习大", "中国"};
        filterService = SensitiveWordFilter.of(SensitiveKeywords.buildTrie(keywords));
    }

    @Test
    public void test() {
        String str = "最近搞到一本黄色小说，《维奥莱特罗曼史》，习作者马努里伯爵夫人-习大-习大大";
        String[] keywords = new String[]{"搞到", "黄色", "黄", "习", "习大大", "习大", "中国"};
        LinkedList<KeywordTokenizer> list = SensitiveWordFilter.of(SensitiveKeywords.buildTrie(keywords)).parser(str);
        for (KeywordTokenizer token : list) {
            System.out.println(token);
        }
        Assert.assertEquals(9, list.size());
    }

    @Test
    public void test1() {
        String str = "最近搞到一本黄色小说，《维奥莱特罗曼史》，习作者马努里伯爵夫人-习大-习大大";
        String[] keywords = new String[]{"黄色", "黄", "习", "习大大", "习大", "中国"};
        SensitiveWordFilter filterService = SensitiveWordFilter.of(SensitiveKeywords.buildTrie(keywords));
        System.out.println(filterService.filter(str));
        Assert.assertEquals("最近搞到一本*小说，《维奥莱特罗曼史》，*作者马努里伯爵夫人-*-*", filterService.filter(str));
    }

    @Test
    @DisplayName("Should correctly parse sensitive keywords")
    public void shouldCorrectlyParseSensitiveKeywords() {
        LinkedList<KeywordTokenizer> list = filterService.parser(testString);

        assertEquals(9, list.size(), "Should find 9 sensitive keywords");

        // Verify some specific tokens
        assertTrue(list.stream().anyMatch(token -> token.getToken().equals("搞到") && token.getStartPosition() == 2));
        assertTrue(list.stream().anyMatch(token -> token.getToken().equals("黄色") && token.getStartPosition() == 6));
        assertTrue(list.stream().anyMatch(token -> token.getToken().equals("习大大") && token.getStartPosition() == 35));
    }

    @Test
    @DisplayName("Should correctly filter sensitive keywords")
    public void shouldCorrectlyFilterSensitiveKeywords() {
        String filteredString = filterService.filter(testString);
        String expectedString = "最近*一本*小说，《维奥莱特罗曼史》，*作者马努里伯爵夫人-*-*";

        assertEquals(expectedString, filteredString, "Filtered string should match expected string");
    }

    @Test
    @DisplayName("Should handle text without sensitive keywords")
    public void shouldHandleTextWithoutSensitiveKeywords() {
        String safeString = "这是一个安全的字符串，不包含敏感词。";
        String filteredString = filterService.filter(safeString);

        assertEquals(safeString, filteredString, "Safe string should remain unchanged");
    }

    @Test
    @DisplayName("Should handle empty input")
    public void shouldHandleEmptyInput() {
        String emptyString = "";
        String filteredString = filterService.filter(emptyString);

        assertEquals(emptyString, filteredString, "Empty string should remain unchanged");
    }
}
