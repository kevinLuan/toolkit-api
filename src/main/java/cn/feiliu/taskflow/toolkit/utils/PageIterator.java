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

import cn.feiliu.taskflow.toolkit.page.FindFunc;
import cn.feiliu.taskflow.toolkit.page.Page;
import cn.feiliu.taskflow.toolkit.page.CountFunc;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

/**
 * 分页迭代器
 *
 * @author KEVIN.LUAN
 * @since 2024-10-06
 */
@Getter
public class PageIterator<E> implements Iterator<E> {

    public Page getPage() {
        int start = (currentPn - 1) * pageSize;
        return Page.builder().start(start).pageSize(pageSize).totalItem(totalItem).totalPn(totalPn)
            .currentPn(currentPn).build();
    }

    private final Integer totalItem;
    private final Integer totalPn;
    private Integer       pageSize;

    // 当前页
    private Integer       currentPn    = 0;
    private List<?>       items        = Collections.EMPTY_LIST;
    private int           itemPos      = -1;
    private AtomicLong    itemPosition = new AtomicLong();
    private FindFunc      func;

    protected PageIterator(int totalItem, int pageSize, FindFunc func) {
        if (func == null) {
            throw new IllegalArgumentException("func 参数不能为空");
        }
        if (totalItem < 0) {
            throw new IllegalArgumentException("totalItem 参数无效");
        }
        if (pageSize < 1 || pageSize > 1000) {
            throw new IllegalArgumentException("pageSize限制范围在1~1000");
        }
        this.pageSize = pageSize;
        this.totalItem = totalItem;
        if (totalItem % pageSize != 0) {
            totalPn = (int) (totalItem / pageSize) + 1;
        } else {
            totalPn = (int) (totalItem / pageSize);
        }
        this.func = func;
    }

    public static <T> PageIterator<T> of(int pageSize, CountFunc countFunc, FindFunc func) {
        int total = countFunc.count();
        return of(total, pageSize, func);
    }

    /**
     * 默认每页10条
     *
     * @param total
     * @param func
     * @return
     */
    public static <T> PageIterator<T> of(int total, FindFunc func) {
        return of(total, 10, func);
    }

    public static <T> PageIterator<T> of(int total, int pageSize, FindFunc func) {
        return new PageIterator<T>(total, pageSize, func);
    }

    @Override
    public boolean hasNext() {
        if (totalItem > 0) {
            if (this.items.size() > 0 && this.items.size() > this.itemPos + 1) {
                return true;
            } else {
                if (reload()) {
                    return hasNext();
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private boolean reload() {
        if (totalItem > 0) {
            if (totalPn > currentPn) {
                currentPn++;
                this.items = func.load(this.getPage());
                this.itemPos = -1;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E next() {
        if (hasNext()) {
            itemPos++;
            itemPosition.incrementAndGet();
            return (E) items.get(itemPos);
        }
        return null;
    }

    /**
     * 获取全局Item位置
     *
     * @return
     */
    public long getItemPosition() {
        return itemPosition.get();
    }
}
