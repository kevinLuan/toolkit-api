package cn.feiliu.taskflow.page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;

/**
 * 分页器
 *
 * @param <T>
 * @author KEVIN LUAN
 */
public class Paged<T> {
    // 数据总条数
    private int totalSize = 0;
    @SuppressWarnings("unchecked")
    @Getter
    private List<T> resultList = Collections.EMPTY_LIST;
    // 当前页面
    private int pageNo = 0;
    @Getter
    // 每页数据条数
    private int pageSize = 10;
    @Getter
    private int totalPage = 0;

    public Paged(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 获取Limit $start
     *
     * @return
     */
    public int getStart() {
        return PaginationHelper.calcStart(pageNo, pageSize);
    }

    public Paged(int pageNo, int pageSize) {
        if (pageSize < 1) {
            pageSize = 10;
        } else if (pageSize > 200) {
            pageSize = 200;
        }
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    /**
     * 统计总数量 <br/>
     *
     * <pre>
     * 示例
     *    public int DAO.count(..);
     * </pre>
     *
     * @param func
     * @return
     */
    public Paged<T> count(CountFunc<?> func) {
        Objects.requireNonNull(func);
        this.totalSize = func.count();
        return this;
    }

    private void adjustPageNo() {
        this.pageNo = PaginationHelper.adjustPageNo(pageNo, totalSize, pageSize);
    }

    /**
     * 执行数据查询
     *
     * <pre>
     * 示例：
     *     public List<Promotion> XxxMapper.findList(..);
     * </pre>
     *
     * @param func
     * @return
     */
    public Paged<T> find(Function<Paged<T>, List<T>> func) {
        Objects.requireNonNull(func);
        this.adjustPageNo();
        if (totalSize > 0) {
            this.resultList = func.apply(this);
        }
        return this;
    }

    /**
     * 消费数据查询
     *
     * @return
     */
    public Paged<T> forEach(Consumer<T> action) {
        Objects.requireNonNull(action);
        for (T t : resultList) {
            action.accept(t);
        }
        return this;
    }

    /**
     * 获取分页对象
     *
     * @return
     */
    public Pagination getPagination() {
        return PaginationHelper.make(totalSize, pageNo, pageSize);
    }


    /**
     * 执行结束获取返回结果
     *
     * @param func
     */
    public void done(ResultFunc<T> func) {
        func.apply(resultList, this);
    }
}
