package com.lyh.api.page;

import java.util.List;

/**
 * @author shoushen.luan
 * @since 2022-10-23
 */
@FunctionalInterface
public interface FindFunc {
    List<?> load(Page page);
}