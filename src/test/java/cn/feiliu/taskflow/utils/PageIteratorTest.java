package cn.feiliu.taskflow.utils;

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
public class PageIteratorTest {
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
