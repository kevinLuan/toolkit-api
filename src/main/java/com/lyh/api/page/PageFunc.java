package com.lyh.api.page;

/**
 * 编排函数API
 * 
 * @author KEVIN LUAN
 *
 */
public final class PageFunc {
  /**
   * 分页编排函数调用
   * 
   * <pre>
   * PageFunc.pageFunc(req.getPageNo(), PAGE_SIZE, Promotion.class).count(() -> {
   *   return promotionService.count(...);
   * }).find((p) -> {
   *   return promotionService.find(...);
   * }).forEach((p) -> {
   *   ... 
   * });
   * </pre>
   * 
   * @param pageNo 请求页码（1,2 ...）
   * @param findMethodRtnType 数据库查询返回类型(->find())
   * @return
   */
  public static <T> Paged<T> pageFunc(int pageNo, Class<T> findMethodRtnType) {
    return new Paged<T>(pageNo);
  }

  /**
   * 分页编排函数调用
   * 
   * <pre>
   * PageFunc.pageFunc(req.getPageNo(), PAGE_SIZE, Promotion.class).count(() -> {
   *   return promotionService.count(...);
   * }).find((p) -> {
   *   return promotionService.find(...);
   * }).forEach((p) -> {
   *   ... 
   * });
   * </pre>
   * 
   * @param pageNo 请求页码（1,2 ...）
   * @param pageSize 每页返回数据条数
   * @param findMethodRtnType 数据库查询返回类型(->find())
   * @return
   */
  public static <T> Paged<T> pageFunc(int pageNo, int pageSize, Class<T> findMethodRtnType) {
    return new Paged<T>(pageNo, pageSize);
  }

}
