package cn.feiliu.taskflow.toolkit.page;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Pagination {
  // 当前页码
  private int pn;
  // 每页限条数
  private int limit;
  // 总页数
  private int totalPage;
  // 总数量
  private int totalItem;
}
