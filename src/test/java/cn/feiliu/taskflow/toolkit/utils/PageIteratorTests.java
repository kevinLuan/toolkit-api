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
package cn.feiliu.taskflow.toolkit.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/***
 * 分页迭代器使用场景：用来持续获取大量数据的场景（批量加载数据）
 *
 * @author SHOUSHEN LUAN
 *
 */
public class PageIteratorTests {
    @Test
    public void test1() {
        AtomicInteger count = new AtomicInteger(0);
        PageIterator<Integer> iterator = PageIterator.of(21, (page) -> {
            count.incrementAndGet();
            return findList(page.getStart(), page.getPageSize());
        });
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        // 最多获取页数
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void test2() {
        AtomicInteger count = new AtomicInteger(0);
        PageIterator<Integer> iterator = PageIterator.of(10, 3, (page) -> {
            count.incrementAndGet();
            return findList(page.getStart(), page.getPageSize());
        });
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        // 最多获取页数
        Assert.assertEquals(4, count.get());
    }

    private List<?> findList(int start, int size) {
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(start + i);
        }
        return result;
    }
}
