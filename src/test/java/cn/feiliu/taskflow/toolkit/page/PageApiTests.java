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
package cn.feiliu.taskflow.toolkit.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class PageApiTests {
    @Test
    public void testPage1() {
        Func.pageFunc(1, 5, Integer.class).count(() -> {
            return 100;// dao.count();
        }).find((p) -> {
            return findList(p.getStart(), p.getPageSize());
        }).forEach((e) -> {
            System.out.println("返回元素:" + e);
            Assert.assertNotNull(e);
        });
    }

    @Test
    public void testPage2() {
        Func.pageFunc(1, 5, Integer.class).count(() -> {
            return 100;// dao.count();
        }).find((page) -> {
            Pagination pagination = page.getPagination();
            System.out.println(pagination.toString());
            return findList(page.getStart(), page.getPageSize());
        }).done((res, page) -> {
            Assert.assertEquals(5, res.size());
        });
    }

    @Test
    public void testZero() {
        Func.pageFunc(1, 5, Integer.class).count(() -> {
            return 0;// 只有这里返回大于零，才会执行下面的find查询
        }).find((p) -> {
            Assert.fail("出错了");
            return null;
        }).done((res, page) -> {
            Assert.assertEquals(0, res.size());
            Assert.assertEquals(0, page.getTotalPage());
        });
    }

    private List<Integer> findList(int start, int size) {
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new Random().nextInt(100000));
        }
        return result;
    }
}
