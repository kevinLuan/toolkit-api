package cn.feiliu.taskflow.toolkit.page;

import lombok.Builder;
import lombok.Getter;

/**
 * @author KEVIN.LUAN
 * @since 2024-10-06
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