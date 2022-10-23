package com.lyh.api.utils;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 日期时间处理工具
 *
 * @author kevin.luan
 * @since 2022-10-23
 */
@AllArgsConstructor
public class DateOps {

    private ZonedDateTime zonedDateTime;

    public static DateOps of(Instant instant) {
        ZonedDateTime zone = instant.atZone(ZoneOffset.systemDefault());
        DateOps dateOps = new DateOps(zone);
        return dateOps;
    }

    /**
     * Sets this Calendar's current time from the given long value.
     *
     * @param millis the new time in UTC milliseconds from the epoch.
     * @return DateOps
     */
    public static DateOps of(long millis) {
        DateOps dateOps = of(Instant.ofEpochMilli(millis));
        return dateOps;
    }

    public static DateOps current() {
        return of(Instant.now());
    }

    public DateOps addHour(int amount) {
        zonedDateTime = zonedDateTime.plusHours(amount);
        return this;
    }

    public DateOps addMinute(int amount) {
        zonedDateTime = zonedDateTime.plusMinutes(amount);
        return this;
    }

    public DateOps addSecond(int amount) {
        zonedDateTime = zonedDateTime.plusSeconds(amount);
        return this;
    }

    public DateOps addMillisecond(int amount) {
        zonedDateTime = zonedDateTime.plus(amount, ChronoUnit.MILLIS);
        return this;
    }

    public DateOps addYear(int amount) {
        zonedDateTime = zonedDateTime.plusYears(amount);
        return this;
    }

    public DateOps addMonth(int amount) {
        zonedDateTime = zonedDateTime.plusMonths(amount);
        return this;
    }

    public DateOps addDay(int amount) {
        zonedDateTime = zonedDateTime.plusDays(amount);
        return this;
    }

    /**
     * 去掉时分秒,毫秒，仅保留年月日部分
     *
     * @return DateOps
     */
    public DateOps trimHmsMsPart() {
        zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        return this;
    }

    /**
     * 去掉分秒,毫秒，仅保留年月日小时部分
     *
     * @return DateOps
     */
    public DateOps trimMsMsPart() {
        zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.HOURS);
        return this;
    }

    /**
     * 去掉时间毫秒值部分
     *
     * @return DateOps
     */
    public DateOps trimMsPart() {
        zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.SECONDS);
        return this;
    }

    public Instant getTime() {
        return zonedDateTime.toInstant();
    }

    public long getMilliseconds() {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 获取过期时间(秒)值
     * <p>
     * 注意：该操作可能会丢失精度，另外强转换到int类型会出现int类型越界情况的可能，如果超出int最大范围将返回Integer.MAX_VALUE
     * </p>
     *
     * @return DateOps
     */
    public int intSecondsValue() {
        long seconds = zonedDateTime.toInstant().getEpochSecond();
        if (seconds > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) seconds;
    }

    /**
     * 设置时间秒值到整秒后的值(UP)
     *
     * @return DateOps
     */
    public DateOps secondCeil() {
        long ms = zonedDateTime.toInstant().toEpochMilli();
        if (ms % 1000 != 0) {
            return this.trimMsPart().addSecond(1);
        }
        return this;
    }

    /**
     * 获取时间整秒后的值
     *
     * @return DateOps
     */
    public DateOps secondFloor() {
        long ms = zonedDateTime.toInstant().toEpochMilli();
        if (ms % 1000 != 0) {
            return this.trimMsPart();
        }
        return this;
    }

    /**
     * 获取TTL过期时间秒值
     *
     * @return DateOps
     */
    public int ttlSeconds() {
        long restTime = ttlMs();
        if (restTime > 0) {
            return (int) Math.min(restTime / 1000, Integer.MAX_VALUE);
        } else {
            return 0;
        }
    }

    /**
     * 获取TTL过期时间毫秒值
     *
     * @return DateOps
     */
    public long ttlMs() {
        long restTime = zonedDateTime.toInstant().toEpochMilli() - System.currentTimeMillis();
        if (restTime > 0) {
            return restTime;
        } else {
            return 0;
        }
    }

    public int getDayOfMonth() {
        return zonedDateTime.getDayOfMonth();
    }

    public String getFormatDayOfMonth() {
        int dayOfMonth = getDayOfMonth();
        return dayOfMonth > 9 ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;
    }

    /**
     * 格式化输出
     *
     * @param pattern yyyy-MM-dd HH:mm:ss,SSS
     * @return DateOps
     */
    public String format(String pattern) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 显示标准格式
     *
     * @return DateOps
     */
    public String show() {
        return format("yyyy-MM-dd HH:mm:ss,SSS");
    }

    public int getMinute() {
        return zonedDateTime.getMinute();

    }

    /**
     * 获取当前的秒
     *
     * @return DateOps
     */
    public int getSecond() {
        return zonedDateTime.getSecond();
    }

    /**
     * 设置当前天
     *
     * @param dayOfMonth 设置dayOfMonth
     * @return DateOps
     */
    public DateOps setDay(int dayOfMonth) {
        zonedDateTime = zonedDateTime.withDayOfMonth(dayOfMonth);
        return this;
    }
}
