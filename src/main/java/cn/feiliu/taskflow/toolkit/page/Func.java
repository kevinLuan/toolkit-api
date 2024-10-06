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
package cn.feiliu.taskflow.toolkit.page;

/**
 * 编排函数API
 * @author kevin.LUAN
 * @since 2024-10-06
 */
public final class Func {
    /**
     * 分页编排函数调用
     * 
     * <pre>
     * Func.pageFunc(req.getPageNo(), PAGE_SIZE, Promotion.class).count(() -> {
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
     * Func.pageFunc(req.getPageNo(), PAGE_SIZE, Promotion.class).count(() -> {
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
