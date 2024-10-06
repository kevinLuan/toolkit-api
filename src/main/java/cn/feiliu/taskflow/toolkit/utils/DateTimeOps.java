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
package cn.feiliu.taskflow.toolkit.utils;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRules;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@NotThreadSafe
public class DateTimeOps {
    public static final DateTimeFormatter ZONE_FMT     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss VV");
    public static final DateTimeFormatter SIMPLE_FMT   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final ZoneId            UTC          = ZoneId.of("UTC");
    public static final ZoneId            SHANG_HAI    = ZoneId.of("Asia/Shanghai");
    public static final ZoneId            NEW_YORK     = ZoneId.of("America/New_York");
    public final static String            PATTERN_DATE = "yyyy-MM-dd";
    private volatile ZonedDateTime        zonedDateTime;

    private DateTimeOps(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime);
        this.zonedDateTime = zonedDateTime;
    }

    public static DateTimeOps ofUtcMilli(long utcTimestamp) {
        Instant instant = Instant.ofEpochMilli(utcTimestamp);
        return new DateTimeOps(instant.atZone(UTC));
    }

    /**
     * 使用系统默认时区创建
     *
     * @param sysMilli 系统默认时区（时间戳）
     * @return
     */
    public static DateTimeOps ofSys(Long sysMilli) {
        return new DateTimeOps(Instant.ofEpochMilli(sysMilli).atZone(ZoneId.systemDefault()));
    }

    public ZonedDateTime getZonedDateTime() {
        return this.zonedDateTime;
    }

    public static DateTimeOps of(ZonedDateTime zonedDateTime) {
        return new DateTimeOps(zonedDateTime);
    }

    public static DateTimeOps parse(String zone, String pattern, String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(zone));
        return new DateTimeOps(zonedDateTime);
    }

    public static DateTimeOps parse(ZoneId zone, DateTimeFormatter formatter, String dateTime) {
        return new DateTimeOps(LocalDateTime.parse(dateTime, formatter).atZone(zone));
    }

    public static DateTimeOps parseDate(String zone, String pattern, String dateTime) {
        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.of(zone));
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 解析时间字符串
     *
     * @param zoneId       描述当前时间所在时区
     * @param milliseconds 毫秒时间戳
     * @return
     */
    public static DateTimeOps of(String zoneId, Long milliseconds) {
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(milliseconds).atZone(ZoneId.of(zoneId));
        return new DateTimeOps(zonedDateTime);
    }

    public static DateTimeOps of(ZoneId zoneId, Long milliseconds) {
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(milliseconds).atZone(zoneId);
        return new DateTimeOps(zonedDateTime);
    }

    public static DateTimeOps of(String zoneId, Date date) {
        ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.of(zoneId));
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 获取当前UTC时间
     *
     * @return
     */
    public static DateTimeOps utcNow() {
        return new DateTimeOps(ZonedDateTime.now(Clock.systemUTC()));
    }

    /**
     * 根据传入时区ID创建
     *
     * @param zoneId
     * @return
     */
    public static DateTimeOps now(String zoneId) {
        return new DateTimeOps(ZonedDateTime.now(ZoneId.of(zoneId)));
    }

    /**
     * 获取系统默认时区时间
     *
     * @return
     */
    public static DateTimeOps localNow() {
        return new DateTimeOps(ZonedDateTime.now(Clock.systemDefaultZone()));
    }

    /**
     * 格式化到目标时区
     *
     * @param zone    目标时区
     * @param pattern 描述日期和时间格式的模式
     * @return
     */
    public String convertAndFormat(String zone, String pattern) {
        return convert(zone).format(pattern);
    }

    /**
     * 转换到目标时区
     *
     * @return
     */
    public DateTimeOps convert(String zone) {
        this.zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of(zone));
        return new DateTimeOps(zonedDateTime);
    }

    public DateTimeOps convert(ZoneId zone) {
        this.zonedDateTime = zonedDateTime.withZoneSameInstant(zone);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 格式化输出
     *
     * @param pattern
     * @return
     */
    public String format(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return zonedDateTime.format(formatter);
    }

    /**
     * 添加小时
     *
     * @param amount 正值为添加，负值为减少
     * @return
     */
    public DateTimeOps addHours(int amount) {
        this.zonedDateTime = zonedDateTime.plusHours(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 添加分钟
     *
     * @param amount 正值为添加，负值为减少
     * @return
     */
    public DateTimeOps addMinutes(int amount) {
        this.zonedDateTime = zonedDateTime.plusMinutes(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 添加秒
     *
     * @param amount 正值为添加，负值为减少
     * @return
     */
    public DateTimeOps addSeconds(int amount) {
        this.zonedDateTime = zonedDateTime.plusSeconds(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 添加年
     *
     * @param amount 正值为添加，负值为减少
     * @return
     */
    public DateTimeOps addYears(int amount) {
        this.zonedDateTime = zonedDateTime.plusYears(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 添加月
     *
     * @param amount 正值为添加，负值为减少
     * @return
     */
    public DateTimeOps addMonths(int amount) {
        this.zonedDateTime = zonedDateTime.plusMonths(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 添加日
     *
     * @param amount 正值为添加，负值为减少
     * @return
     */
    public DateTimeOps addDays(int amount) {
        this.zonedDateTime = zonedDateTime.plusDays(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 添加星期数
     *
     * @param amount
     * @return
     */
    public DateTimeOps addWeeks(int amount) {
        this.zonedDateTime = zonedDateTime.plusWeeks(amount);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 是否处于夏令时时间段
     *
     * @return
     */
    public boolean isDaylightSavings() {
        ZoneId zone = zonedDateTime.getZone();
        ZoneRules rules = zone.getRules();
        return rules.isDaylightSavings(zonedDateTime.toInstant());
    }

    /**
     * 获取毫秒值
     *
     * @return
     */
    public long getMillis() {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 截取到毫秒部分
     *
     * @return
     */
    public DateTimeOps truncatedToMillis() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.MILLIS);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 截取到纳秒
     *
     * @return
     */
    public DateTimeOps truncatedToNanos() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.NANOS);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 截取到微妙
     *
     * @return
     */
    public DateTimeOps truncatedToMicros() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.MICROS);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 截取到秒
     *
     * @return
     */
    public DateTimeOps truncatedToSeconds() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.SECONDS);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 截取到分钟(秒值清零)
     *
     * @return
     */
    public DateTimeOps truncatedToMinutes() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.MINUTES);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 截取到小时(分钟清零)
     *
     * @return
     */
    public DateTimeOps truncatedToHour() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.HOURS);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 截取到天(小时清零)
     *
     * @return
     */
    public DateTimeOps truncatedToDay() {
        this.zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 获取纳秒时间
     *
     * @return
     */
    public int getNanoOfSecond() {
        return zonedDateTime.toInstant().get(ChronoField.NANO_OF_SECOND);
    }

    /**
     * 获取微秒时间
     *
     * @return
     */
    public int getMicroOfSecond() {
        return zonedDateTime.toInstant().get(ChronoField.MICRO_OF_SECOND);
    }

    public int getMilliOfSecond() {
        return zonedDateTime.toInstant().get(ChronoField.MILLI_OF_SECOND);
    }

    /**
     * 改变本地日期-修改为几号
     *
     * @param dayOfMonth
     * @return
     */
    public DateTimeOps withDayOfMonth(int dayOfMonth) {
        this.zonedDateTime = zonedDateTime.withDayOfMonth(dayOfMonth);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 改变本地日期-时间的年份
     *
     * @return
     */
    public DateTimeOps withYear(int year) {
        this.zonedDateTime = zonedDateTime.withYear(year);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 改变本地日期-时间的月份
     *
     * @param month
     * @return
     */

    public DateTimeOps withMonth(int month) {
        this.zonedDateTime = this.zonedDateTime.withMonth(month);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 改变本地日期-时间的小时
     *
     * @param hour – the hour-of-day to set in the result, from 0 to 23
     * @return
     */

    public DateTimeOps withHour(int hour) {
        this.zonedDateTime = this.zonedDateTime.withHour(hour);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 改变本地日期 时间分钟值
     *
     * @param minute
     * @return
     */
    public DateTimeOps withMinute(int minute) {
        this.zonedDateTime = this.zonedDateTime.withMinute(minute);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 改变本地日期 时间秒值
     *
     * @param second
     * @return
     */
    public DateTimeOps withSecond(int second) {
        this.zonedDateTime = this.zonedDateTime.withSecond(second);
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 获取时区，例如“Europe/Paris”
     *
     * @return
     */
    public ZoneId getZone() {
        return this.zonedDateTime.getZone();
    }

    /**
     * 验证当前时间是否在other时间之后
     * DateOperator dateOperatorA = DateOperator.parse("UTC", pattern, "2022-01-01 10:06:05");
     * DateOperator dateOperatorB = DateOperator.parse("UTC", pattern, "2022-01-01 10:06:04");
     * dateOperatorA.isAfter(dateOperatorB) ->返回:true
     * 验证dateOperatorA是否在dateOperatorB之后
     *
     * @param other 其他比较时间
     * @return
     */
    public boolean isAfter(DateTimeOps other) {
        return this.zonedDateTime.isAfter(other.getZonedDateTime());
    }

    /**
     * 检查该日期-时间的瞬间是否在指定日期-时间之前。
     * 此方法不同于{@link #//compareTo}中的比较,只比较date-time的瞬间。这就相当于使用
     * {@code dateTime1.toInstant().isbefore(dateTime2.toInstant());}。
     * 此默认实现基于epoch-second执行比较和纳秒。
     *
     * @param other 要比较的其他日期-时间，不为空,如果这个点在指定的日期-时间之前返回true
     * @return
     */
    public boolean isBefore(DateTimeOps other) {
        return this.zonedDateTime.isBefore(other.getZonedDateTime());
    }

    /**
     * 获取小时,取值范围:0~23
     *
     * @return
     */
    public int getHour() {
        return this.zonedDateTime.getHour();
    }

    /**
     * 获取当前小时距离零点过去的小时数(兼容:夏令时&冬令时)
     *
     * @return
     */
    public int getHourOfDay() {
        int currentHour = getHour();
        if (isDaylightSavingsChangeDay()) {
            int hour = getDaylightSavingsMissingHour().get();
            if (currentHour > hour) {
                return currentHour - 1;
            }
        } else if (isWinterTimeChangeDay()) {
            int hour = getWinterTimeHourBack().get();
            if (currentHour > hour) {
                return currentHour + 1;
            } else if (currentHour == hour) {
                //时针回拨
                if (zonedDateTime.plusHours(1).getHour() > hour) {
                    return currentHour + 1;
                }
            }
        }
        return currentHour;
    }

    /**
     * 获取当前分钟，取值范围:0~59
     *
     * @return
     */
    public int getMinute() {
        return this.zonedDateTime.getMinute();
    }

    /**
     * 获取当前秒值，取值范围:0~59
     *
     * @return
     */
    public int getSecond() {
        return this.zonedDateTime.getSecond();
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public int getYear() {
        return this.zonedDateTime.getYear();
    }

    /**
     * 获取当前月份，取值范围:1~12
     *
     * @return
     */
    public int getMonth() {
        return this.zonedDateTime.getMonth().getValue();
    }

    /**
     * 获取当月日期，取值范围:1~31
     *
     * @return
     */
    public int getDayOfMonth() {
        return this.zonedDateTime.getDayOfMonth();
    }

    /**
     * 获取星期几,取值范围:from 1 (Monday) to 7 (Sunday)
     *
     * @return
     */
    public int getDayOfWeek() {
        return this.zonedDateTime.getDayOfWeek().getValue();
    }

    /**
     * 判断是否为上午时间
     *
     * @return
     */
    public boolean isAM() {
        return zonedDateTime.get(ChronoField.AMPM_OF_DAY) == 0;
    }

    /**
     * 判断是否为下午时间
     *
     * @return
     */
    public boolean isPM() {
        return zonedDateTime.get(ChronoField.AMPM_OF_DAY) == 1;
    }

    /**
     * 格式化时间，并添加AM OR PM
     *
     * @param pattern MM/dd/yyyy hh:mm
     * @return 返回示例:`12/26/2022 11:59AM` or `12/26/2022 12:59PM`
     */
    public String formatAndAmPm(String pattern) {
        if (isAM()) {
            return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern)) + "AM";
        } else {
            return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern)) + "PM";
        }
    }

    /**
     * 是否为冬令时切换日
     *
     * @return
     */
    public boolean isWinterTimeChangeDay() {
        Pair<Boolean, Integer> pair = checkDaylightSavingsChangeDay();
        return pair.getKey() && pair.getValue().intValue() == 25;
    }

    /**
     * 是否为夏令时切换日
     *
     * @return
     */
    public boolean isDaylightSavingsChangeDay() {
        Pair<Boolean, Integer> pair = checkDaylightSavingsChangeDay();
        return pair.getKey() && pair.getValue().intValue() == 23;
    }

    /**
     * 验证是否为夏令时和冬令时切换日
     *
     * @return
     */
    private Pair<Boolean, Integer> checkDaylightSavingsChangeDay() {
        ZonedDateTime zdt = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        ZoneRules rules = zonedDateTime.getZone().getRules();
        boolean start = rules.isDaylightSavings(zdt.toInstant());
        boolean end = rules.isDaylightSavings(zdt.withHour(23).withMinute(59).toInstant());
        if (start != end) {
            int hourCnt = 0;
            final int day = zdt.getDayOfMonth();
            do {
                hourCnt++;
                zdt = zdt.plusHours(1);
            } while (day == zdt.getDayOfMonth());
            return Pair.of(true, hourCnt);
        }
        return Pair.of(false, 24);
    }

    /**
     * 获取夏令时减少的时间：小时
     *
     * @return
     */
    public Optional<Integer> getDaylightSavingsMissingHour() {
        if (isDaylightSavingsChangeDay()) {
            ZonedDateTime zdt = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
            DateTimeOps dateTimeOps = DateTimeOps.of(zdt);
            final int day = dateTimeOps.getDayOfMonth();
            do {
                if (dateTimeOps.isDaylightSavings()) {
                    return Optional.of(dateTimeOps.getHour() - 1);
                }
                dateTimeOps.addHours(1);
            } while (day == dateTimeOps.getDayOfMonth());
        }
        return Optional.empty();
    }

    /**
     * 获取冬令时时针回拨时间:小时取值范围:(0~23)
     *
     * @return
     */
    public Optional<Integer> getWinterTimeHourBack() {
        if (isWinterTimeChangeDay()) {
            ZonedDateTime zdt = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
            DateTimeOps dateTimeOps = DateTimeOps.of(zdt);
            final int today = dateTimeOps.getDayOfMonth();
            do {
                if (!dateTimeOps.isDaylightSavings()) {
                    return Optional.of(dateTimeOps.getHour());
                }
                dateTimeOps.addHours(1);
            } while (today == dateTimeOps.getDayOfMonth());
        }
        return Optional.empty();
    }

    /**
     * 转换到系统默认时区
     *
     * @return
     */
    public DateTimeOps convertSysZone() {
        this.zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        return new DateTimeOps(zonedDateTime);
    }

    /**
     * 转换到UTC时区
     *
     * @return
     */
    public DateTimeOps convertUTC() {
        this.zonedDateTime = zonedDateTime.withZoneSameInstant(UTC);
        return new DateTimeOps(zonedDateTime);
    }

    public String toString() {
        return zonedDateTime.format(ZONE_FMT);
    }

    public String format(DateTimeFormatter fmt) {
        return zonedDateTime.format(fmt);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        DateTimeOps that = (DateTimeOps) object;
        return Objects.equals(zonedDateTime, that.zonedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zonedDateTime);
    }
}
