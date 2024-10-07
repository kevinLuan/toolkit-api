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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * 敏感词过滤单元测试
 *
 * @author KEVIN LUAN
 */
public class SensitiveWordFilterTests {
    public static void main(String[] args) {

    }

    @Test
    public void test() {
        String content = "最近网上流传一些关于政府的谣言，说什么国家领导人习大大要辞职了，还有人造谣说中国经济要崩溃。这些都是一派胡言！我们要相信党和政府，不要轻信这些黄色新闻。\n"
                         + "有些人总喜欢讨论一些敏感话题，比如法轮功啊、六四事件啊，这些都是被禁止的。我们应该多关注积极向上的内容，为祖国的繁荣发展贡献力量。\n"
                         + "现在社会上还有一些不良现象，比如有人吸毒、嫖娼、贩卖军火，这些都是违法的。我们每个公民都有责任抵制这些行为，维护社会治安。\n"
                         + "总之，我们要擦亮眼睛，不要被一些别有用心的人蛊惑。让我们团结一致，在党的领导下，为实现中国梦而努力奋斗！";

        String[] sensitiveWords = new String[] { "习大大", "辞职", "中国经济", "崩溃", "谣言", "黄色新闻", "法轮功", "六四事件", "敏感话题", "被禁止",
                "吸毒", "嫖娼", "贩卖军火", "违法", "党", "政府", "国家领导人", "中国梦" };
        SensitiveWordFilter sensitiveWordFilter = new SensitiveWordFilter(SensitiveKeywords.buildTrie(sensitiveWords));
        List<KeywordToken> tokenizers = sensitiveWordFilter.parser(content);
        for (KeywordToken tokenizer : tokenizers) {
            System.out.println(tokenizer);
        }
        Assert.assertTrue(tokenizers.size() == 20);
        for (int i = 0; i < 100000; i++) {
            sensitiveWordFilter.filter(content);
        }
        Assert
            .assertEquals("最近网上流传一些关于*的*，说什么**要*了，还有人造谣说*要*。这些都是一派胡言！我们要相信*和*，不要轻信这些*。\n"
                          + "有些人总喜欢讨论一些*，比如*啊、*啊，这些都是*的。我们应该多关注积极向上的内容，为祖国的繁荣发展贡献力量。\n"
                          + "现在社会上还有一些不良现象，比如有人*、*、*，这些都是*的。我们每个公民都有责任抵制这些行为，维护社会治安。\n"
                          + "总之，我们要擦亮眼睛，不要被一些别有用心的人蛊惑。让我们团结一致，在*的领导下，为实现*而努力奋斗！", sensitiveWordFilter.filter(content));
    }
}
