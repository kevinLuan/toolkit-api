package com.lyh.api.page;

import lombok.Builder;
import lombok.Getter;

/**
 * @author shoushen.luan
 * @since 2022-10-23
 */
@Builder
@Getter
public class Page {
    // 查询开始index
    private int start;
    // 每页条数
    private int pageSize;
    // 总数据量
    private int totalItem;
    // 总页数
    private int totalPn;
    // 当前页
    private int currentPn;
}