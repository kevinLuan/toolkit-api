package com.lyh.api.page;

import java.util.List;

/**
 * @author shoushen.luan
 * @since 2022-10-23
 */
@FunctionalInterface
public interface ResultFunc<T> {
    /**
     * 获取返回结果
     *
     * @param result 数据库查询返回list
     * @param paged  分页实例信息
     */
    void apply(List<T> result, Paged<T> paged);
}