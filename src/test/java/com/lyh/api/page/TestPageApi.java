package com.lyh.api.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class TestPageApi {
  @Test
  public void testPage1() {
    PageFunc.pageFunc(1, 5, Integer.class).count(() -> {
      return 100;// dao.count();
    }).find((p) -> {
      return findList(p.getStart(), p.getPageSize());
    }).forEach((e) -> {
      System.out.println("返回元素:"+e);
      Assert.assertNotNull(e);
    });
  }

  @Test
  public void testPage2() {
    PageFunc.pageFunc(1, 5, Integer.class).count(() -> {
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
    PageFunc.pageFunc(1, 5, Integer.class).count(() -> {
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
