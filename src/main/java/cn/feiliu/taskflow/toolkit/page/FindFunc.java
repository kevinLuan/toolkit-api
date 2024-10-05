package cn.feiliu.taskflow.toolkit.page;

import java.util.List;

/**
 * @author kevin.LUAN
 * @since 2024-10-06
 */
@FunctionalInterface
public interface FindFunc {
    List<?> load(Page page);
}