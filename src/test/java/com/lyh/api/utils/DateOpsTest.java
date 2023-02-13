package com.lyh.api.utils;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

/**
 * @author SHOUSHEN.LUAN
 * @since 2023-02-13
 */
public class DateOpsTest {
    @Test
    public void test() {
        Instant now = Instant.now();
        int dayOfMonth1 = DateOps.of(now).getDayOfMonth();
        int dayOfMonth2 = DateOps.of(System.currentTimeMillis()).getDayOfMonth();
        Assertions.assertThat(dayOfMonth1).isEqualTo(dayOfMonth2);
    }
}
