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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeOpsTests {
    private final String pattern      = "yyyy-MM-dd HH:mm:ss";
    private final String shanghaiZone = "Asia/Shanghai";
    private final String newYorkZone  = "America/New_York";
    private DateTimeOps  dateTimeOps;

    @Before
    public void setUp() {
        dateTimeOps = DateTimeOps.utcNow();
    }

    @Test
    public void testWorldTimeZone() {
        for (String availableZoneId : ZoneId.getAvailableZoneIds()) {
            assertNotNull(DateTimeOps.localNow().convertAndFormat(availableZoneId, pattern));
        }
    }

    @Test
    public void testBasicOperations() {
        assertNotNull(DateTimeOps.localNow());
        assertNotNull(DateTimeOps.utcNow());
        assertNotNull(DateTimeOps.now(DateTimeOps.NEW_YORK.getId()));

        String dateTime = "2024-10-05 15:36:59";
        Long utcMills = DateTimeOps.parse(DateTimeOps.UTC, DateTimeOps.SIMPLE_FMT, dateTime).getMillis();
        Long newYorkMills = DateTimeOps.parse(DateTimeOps.NEW_YORK, DateTimeOps.SIMPLE_FMT, dateTime).getMillis();
        Long shanghaiMills = DateTimeOps.parse(DateTimeOps.SHANG_HAI, DateTimeOps.SIMPLE_FMT, dateTime).getMillis();

        assertNotEquals(utcMills, newYorkMills);
        assertNotEquals(newYorkMills, shanghaiMills);
        assertNotEquals(utcMills, shanghaiMills);
        assertTrue(DateTimeOps.of(DateTimeOps.UTC, utcMills).toString().startsWith(dateTime));
        assertTrue(DateTimeOps.of(DateTimeOps.NEW_YORK, newYorkMills).toString().startsWith(dateTime));
        assertTrue(DateTimeOps.of(DateTimeOps.SHANG_HAI, shanghaiMills).toString().startsWith(dateTime));
    }

    @Test
    public void testUtcTimeConversion() {
        String dateTime = "2022-12-21 17:40:28";
        assertEquals("2022-12-21 09:40:28", DateTimeOps.parse("Asia/Shanghai", pattern, dateTime).convert("UTC")
            .format(pattern));
    }

    @Test
    public void testTimeConvert() {
        String newYorkTime = "2022-12-21 03:33:17";
        String shanghaiTime = "2022-12-21 16:33:17";
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        assertEquals("2022-12-21 16:33:17", DateTimeOps.parse(newYorkZone, pattern, newYorkTime).convert(shanghaiZone)
            .format(pattern));
        assertEquals("2022-12-21 03:33:17",
            DateTimeOps.parse(shanghaiZone, pattern, shanghaiTime).convertAndFormat(newYorkZone, pattern));
    }

    @Test
    public void testChinaDaylightSavingTime() {
        ZoneId zoneId = ZoneId.of(shanghaiZone);
        LocalDateTime localDateTime = LocalDateTime.of(1986, 5, 3, 0, 0);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        //获取时区计时规则
        ZoneRules rules = zoneId.getRules();
        assertFalse(rules.isDaylightSavings(zonedDateTime.toInstant()));
        System.out.println("------------中国夏令时原本是5月4日开始到9月14日结束，目前已经暂停使用--------------");
        String start = null, end = null;
        System.out.println(DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 02:00:00").isDaylightSavings());
        while (zonedDateTime.getYear() < 2025) {
            if (rules.isDaylightSavings(zonedDateTime.toInstant())) {
                if (start == null) {
                    start = DateTimeOps.of(zonedDateTime).format(pattern);
                }
                end = DateTimeOps.of(zonedDateTime).format(pattern);
            } else {
                if (start != null) {
                    ZonedDateTime startZoneDateTime = DateTimeOps.parse(shanghaiZone, pattern, start)
                        .getZonedDateTime();
                    ZonedDateTime endZoneDateTime = DateTimeOps.parse(shanghaiZone, pattern, end).getZonedDateTime();
                    System.out.println("夏令时:" + start + "(" + show(startZoneDateTime) + ")~" + end + "("
                                       + show(endZoneDateTime) + ")");
                }
                start = null;
                end = null;
            }
            zonedDateTime = zonedDateTime.plusHours(1);
        }
    }

    @Test
    public void testNewYorkDaylightSavingTime() {
        ZoneId zoneId = ZoneId.of(newYorkZone);
        LocalDateTime localDateTime = LocalDateTime.of(1985, 1, 1, 0, 0);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        ZoneRules rules = zoneId.getRules();
        assertFalse(rules.isDaylightSavings(zonedDateTime.toInstant()));
        String start = null, end = null;
        for (int i = 0; zonedDateTime.getYear() < 2025; i++) {
            zonedDateTime = zonedDateTime.plusHours(1);
            if (rules.isDaylightSavings(zonedDateTime.toInstant())) {
                if (start == null) {
                    start = DateTimeOps.of(zonedDateTime).format(pattern);
                }
                end = DateTimeOps.of(zonedDateTime).format(pattern);
            } else {
                if (start != null) {
                    ZonedDateTime startZoneDateTime = DateTimeOps.parse(newYorkZone, pattern, start).getZonedDateTime();
                    String startDateTime = DateTimeOps.of(startZoneDateTime).format("EEE MMM dd HH:mm:ss zzz yyyy");
                    ZonedDateTime endZoneDateTime = DateTimeOps.parse(newYorkZone, pattern, end).getZonedDateTime();
                    System.out.println("纽约夏令时:" + startDateTime + "|" + start + "(" + show(startZoneDateTime) + ")~"
                                       + end + "(" + show(endZoneDateTime) + ")");

                }
                start = null;
                end = null;
            }
        }
    }

    @Test
    public void testEstAndEdt() {
        String patterStr = "yyyy-MM-dd";
        String birthdayStr = "1988-09-11";
        DateTimeOps dateTimeOps = DateTimeOps.parseDate(shanghaiZone, patterStr, birthdayStr);
        System.out.println(dateTimeOps.format("EEE MMM dd HH:mm:ss zzz yyyy"));
        System.out.println(dateTimeOps.getZonedDateTime().toInstant().toEpochMilli());
        DateTimeOps dateTimeOps2 = DateTimeOps.of(shanghaiZone, dateTimeOps.getZonedDateTime().toInstant()
            .toEpochMilli());
        System.out.println(dateTimeOps2.format("EEE MMM dd HH:mm:ss zzz yyyy"));
        System.out.println(dateTimeOps2.format("yyyy-MM-dd"));
        String newYorkPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
        String[] estTimes = { "2021-03-13 23:00:00", "2021-03-14 00:00:00", "2021-03-14 01:00:00", };
        for (String dateTime : estTimes) {
            DateTimeOps operator = DateTimeOps.parse(newYorkZone, pattern, dateTime);
            String targetDateTime = operator.format(newYorkPattern);
            long timeMs = operator.getZonedDateTime().toInstant().toEpochMilli();
            String dt = operator.format(pattern);
            assertFalse(operator.isDaylightSavings());
            System.out.println("解析:" + dateTime + "--`" + targetDateTime + "`->`" + timeMs + "`->`" + dt + "`");
        }
        String[] edtTimes = { "2021-03-14 02:00:00", "2021-03-14 03:00:00", "2021-03-14 04:00:00" };
        for (String dateTime : edtTimes) {
            DateTimeOps operator = DateTimeOps.parse(newYorkZone, pattern, dateTime);
            String targetDateTime = operator.format(newYorkPattern);
            long timeMs = operator.getZonedDateTime().toInstant().toEpochMilli();
            String dt = operator.format(pattern);
            assertTrue(operator.isDaylightSavings());
            System.out.println("解析:" + dateTime + "--`" + targetDateTime + "`->`" + timeMs + "`->`" + dt + "`");
        }
    }

    @Test
    public void testNewYorkEdt() {
        String dateTime = "2021-03-14 03:00:00";
        assertTrue(DateTimeOps.parse(newYorkZone, pattern, dateTime).isDaylightSavings());
        dateTime = "2021-03-14 01:00:00";
        assertFalse(DateTimeOps.parse(newYorkZone, pattern, dateTime).isDaylightSavings());
    }

    @Test
    public void testChinaEdt() {
        String dateTime = "1986-05-04 03:00:00";
        assertTrue(DateTimeOps.parse(shanghaiZone, pattern, dateTime).isDaylightSavings());
        dateTime = "1986-05-04 01:00:00";
        assertFalse(DateTimeOps.parse(shanghaiZone, pattern, dateTime).isDaylightSavings());
        //夏令时
        assertEquals("1986-05-04 03:00:00",
            DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 02:00:00").format(pattern));
        assertTrue(DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 05:00:00").isDaylightSavings());
        System.out.println(DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 02:00:00").getMillis());
        System.out.println(DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 03:00:00").getMillis());
        DateTimeOps dateTimeOps2 = DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 02:00:00");
        DateTimeOps dateTimeOps3 = DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 03:00:00");
        System.out.println(dateTimeOps2);
        System.out.println(dateTimeOps3);
    }

    @Test
    public void testDstChangeDay() {
        {
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 02:00:00");
            System.out.println("夏令时切换日:" + dateTimeOps.format(DateTimeOps.PATTERN_DATE));
            assertTrue(dateTimeOps.isDaylightSavingsChangeDay());
            assertFalse(dateTimeOps.isWinterTimeChangeDay());
        }
        {
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-09-14 01:00:00");
            System.out.println("冬令时切换日:" + dateTimeOps.format(DateTimeOps.PATTERN_DATE));
            assertFalse(dateTimeOps.isDaylightSavingsChangeDay());
            assertTrue(dateTimeOps.isWinterTimeChangeDay());
        }
        String[] zones = { shanghaiZone, newYorkZone };
        for (String zone : zones) {
            DateTimeOps dateTimeOps = DateTimeOps.parse(zone, pattern, "1986-01-01 00:00:00");
            while (dateTimeOps.getYear() < 2025) {
                if (dateTimeOps.isDaylightSavingsChangeDay()) {
                    int hour = dateTimeOps.getDaylightSavingsMissingHour().get();
                    assertEquals(2, hour);
                }
                if (dateTimeOps.isWinterTimeChangeDay()) {
                    int hour = dateTimeOps.getWinterTimeHourBack().get();
                    assertEquals(1, hour);
                }
                dateTimeOps.addDays(1);
            }
        }
    }

    @Test
    public void testChinaDstMissingHour() {
        String[] zones = { shanghaiZone, newYorkZone };
        for (String zone : zones) {
            DateTimeOps dateTimeOps = DateTimeOps.parse(zone, pattern, "1986-01-01 00:00:00");
            while (dateTimeOps.getYear() < 2025) {
                if (dateTimeOps.isDaylightSavingsChangeDay()) {
                    int hour = dateTimeOps.getDaylightSavingsMissingHour().get();
                    System.out.println(dateTimeOps.format("yyyy-MM-dd") + "(跳过时间:" + hour + "时)");
                    assertEquals(2, hour);
                }
                dateTimeOps.addDays(1);
            }
        }
    }

    @Test
    public void testDstHour() {
        {
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 00:00:00");
            assertTrue(dateTimeOps.isDaylightSavingsChangeDay());
            System.out.println("------------夏令时------------");
            for (int i = 0; i < 23; i++) {
                System.out.println(dateTimeOps.toString() + "->" + dateTimeOps.getHourOfDay());
                dateTimeOps.addHours(1);
                if (dateTimeOps.getHour() <= 1) {
                    assertEquals(dateTimeOps.getHour(), dateTimeOps.getHourOfDay());
                } else {
                    assertEquals(dateTimeOps.getHour() - 1, dateTimeOps.getHourOfDay());
                }
            }
        }
        {
            System.out.println("------------冬令时------------");
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-09-14 00:00:00");
            assertTrue(dateTimeOps.isWinterTimeChangeDay());
            for (int i = 0; i < 25; i++) {
                System.out.println(dateTimeOps + "->" + dateTimeOps.getHourOfDay());
                if (dateTimeOps.getHour() == 0) {
                    assertEquals(dateTimeOps.getHour(), dateTimeOps.getHourOfDay());
                } else if (dateTimeOps.getHour() > 1) {
                    assertEquals(dateTimeOps.getHour() + 1, dateTimeOps.getHourOfDay());
                } else {
                    if (i == 1) {
                        assertEquals(dateTimeOps.getHour(), dateTimeOps.getHourOfDay());
                    } else {
                        assertEquals(dateTimeOps.getHour() + 1, dateTimeOps.getHourOfDay());
                    }
                }
                dateTimeOps.addHours(1);
            }
        }
    }

    /**
     * 测试夏令时转冬令时时针回拨
     */
    @Test
    public void testWinterTimeHourBackFormat() {
        long startHour1 = 527011200000L;
        long startHour2 = 527014800000L;
        String result = "1986-09-14 01:00:00";
        assertEquals(result, DateTimeOps.of(shanghaiZone, startHour1).format(pattern));
        assertEquals(result, DateTimeOps.of(shanghaiZone, startHour2).format(pattern));
    }

    @Test
    public void testWinterTimeHourBack() {
        DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-01-01 00:00:00");
        while (dateTimeOps.getYear() < 2025) {
            if (dateTimeOps.isWinterTimeChangeDay()) {
                int hour = dateTimeOps.getWinterTimeHourBack().get();
                System.out.println(dateTimeOps.format("yyyy-MM-dd") + "(指针回拨:" + hour + "时)");
                assertEquals(1, hour);
            }
            dateTimeOps.addDays(1);
        }
    }

    @Test
    public void testOpsDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeOps dateTimeOps = DateTimeOps.utcNow();
        String utcDateTime = dateTimeOps.format(pattern);
        long timeMs = dateTimeOps.getZonedDateTime().toInstant().toEpochMilli();
        assertEquals(utcDateTime, DateTimeOps.parse("UTC", pattern, utcDateTime).format(pattern));
        assertEquals(utcDateTime, DateTimeOps.of("UTC", timeMs).format(pattern));
        System.out.println("UTC(时间戳):" + DateTimeOps.of("UTC", timeMs).getZonedDateTime().toInstant().toEpochMilli());
        System.out.println("北京时间:" + dateTimeOps.convert(shanghaiZone).format(pattern));
    }

    private static String show(ZonedDateTime zonedDateTime) {
        int week = zonedDateTime.get(ChronoField.DAY_OF_WEEK);
        int weekMonth = zonedDateTime.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
        return "第" + weekMonth + "周,星期" + week;
    }

    @Test
    public void testTimeMillis() {
        DateTimeOps utcDate = DateTimeOps.utcNow();
        System.out.println("UTC时间戳:" + utcDate.getMillis());
        Long utcTimeMs = utcDate.getMillis();
        System.out.println("UTC时间:" + DateTimeOps.of("UTC", utcTimeMs).format(pattern));
        Long localTimeMs = DateTimeOps.localNow().getMillis();
        System.out.println("北京时间戳:" + localTimeMs);
        System.out.println("北京时间:" + DateTimeOps.of(shanghaiZone, localTimeMs).format(pattern));
        assertEquals(DateTimeOps.of("UTC", utcTimeMs).convertAndFormat(shanghaiZone, pattern),
            DateTimeOps.of(shanghaiZone, localTimeMs).format(pattern));
    }

    @Test
    public void testTrimHms() {
        DateTimeOps utcDate = DateTimeOps.utcNow();
        System.out.println("UTC时间戳:" + utcDate.getMillis());
        Long utcTimeMs = utcDate.getMillis();
        System.out.println("UTC时间:" + DateTimeOps.of("UTC", utcTimeMs).format(pattern));
        DateTimeOps dateTimeOps = DateTimeOps.of("UTC", utcTimeMs);
        System.out.println("毫秒:" + dateTimeOps.getMillis() + "|纳秒:" + dateTimeOps.getNanoOfSecond());
        dateTimeOps.truncatedToNanos();
        System.out.println("truncatedToNanos 毫秒:" + dateTimeOps.getMillis() + "|纳秒:" + dateTimeOps.getNanoOfSecond());
        {
            dateTimeOps.truncatedToMicros();
            assertEquals(0, dateTimeOps.getNanoOfSecond() % 1000000);
        }
        {
            dateTimeOps.truncatedToMillis();
            assertEquals(0, dateTimeOps.getMicroOfSecond() % 1000);
            System.out.println("trimMillis:" + dateTimeOps.getMillis() + "|微秒:" + dateTimeOps.getMicroOfSecond()
                               + "|纳秒:" + dateTimeOps.getNanoOfSecond());
        }
        {
            dateTimeOps.truncatedToSeconds();
            System.out.println("毫秒:" + dateTimeOps.getMillis() + "|微秒:" + dateTimeOps.getMicroOfSecond() + "|纳秒:"
                               + dateTimeOps.getNanoOfSecond());
            System.out.println("trimSeconds:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertEquals(0, dateTimeOps.getMilliOfSecond());
        }
        {
            dateTimeOps.truncatedToMinutes();
            System.out.println("trimMinutes:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertEquals(0, dateTimeOps.getZonedDateTime().getSecond());
        }
        {//trimHour
            dateTimeOps.truncatedToHour();
            System.out.println("trimHour:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertEquals(0, dateTimeOps.getZonedDateTime().getMinute());
        }
        {//截取到天(小时清零)
            dateTimeOps.truncatedToDay();
            System.out.println("trimDay:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertEquals(0, dateTimeOps.getZonedDateTime().getHour());
        }
    }

    @Test
    public void testWithDateTime() {
        DateTimeOps.utcNow();
        DateTimeOps.now(shanghaiZone);
        DateTimeOps.now(newYorkZone);
        DateTimeOps.now("UTC");
        DateTimeOps.localNow();
        DateTimeOps.localNow().isDaylightSavings();
        System.out.println(DateTimeOps.localNow().format(pattern));
        System.out.println(DateTimeOps.localNow().withYear(2020).format(pattern));
        System.out.println(DateTimeOps.localNow().withYear(2020).withMonth(2).format(pattern));
        System.out.println(DateTimeOps.localNow().withYear(2020).withMonth(2).withDayOfMonth(18).format(pattern));
        System.out.println(DateTimeOps.localNow().withHour(20).withMinute(8).withSecond(18).format(pattern));

        DateTimeOps.localNow().getZonedDateTime().isBefore(DateTimeOps.localNow().getZonedDateTime());
        DateTimeOps.localNow().getZonedDateTime().isAfter(DateTimeOps.localNow().getZonedDateTime());
        System.out.println(DateTimeOps.localNow().convert(newYorkZone).isDaylightSavings());
        String dateTime = "2022-01-01 10:00:00";
        DateTimeOps dateTimeOpsNewYork = DateTimeOps.parse(newYorkZone, pattern, dateTime);
        assertEquals(dateTime, dateTimeOpsNewYork.convertAndFormat(newYorkZone, pattern));
        dateTimeOpsNewYork.convert("UTC");
        assertEquals(2022, dateTimeOpsNewYork.getYear());
        assertEquals(1, dateTimeOpsNewYork.getMonth());
        assertEquals(1, dateTimeOpsNewYork.getDayOfMonth());
        assertEquals(15, dateTimeOpsNewYork.getHour());
        assertEquals(0, dateTimeOpsNewYork.getMinute());
        assertEquals(0, dateTimeOpsNewYork.getSecond());

        DateTimeOps dateTimeOps = DateTimeOps.parse("UTC", pattern, dateTime);
        assertEquals(ZoneId.of("UTC"), dateTimeOps.getZone());
        assertEquals(dateTime, dateTimeOps.convertAndFormat("UTC", pattern));
        assertEquals("2022-01-01 18:00:00", dateTimeOps.convertAndFormat(shanghaiZone, pattern));
        assertEquals(ZoneId.of(shanghaiZone), dateTimeOps.getZone());

    }

    @Test
    public void testAfter() {
        String dateTimeA = "2022-01-01 10:06:05";
        String dateTimeB = "2022-01-01 10:06:04";
        DateTimeOps dateTimeOpsA = DateTimeOps.parse("UTC", pattern, dateTimeA);
        DateTimeOps dateTimeOpsB = DateTimeOps.parse("UTC", pattern, dateTimeB);
        assertTrue(dateTimeOpsA.isAfter(dateTimeOpsB));
        assertFalse(dateTimeOpsA.isAfter(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:05")));
        assertFalse(dateTimeOpsA.isAfter(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:06")));
    }

    @Test
    public void testBefore() {
        String dateTimeA = "2022-01-01 10:06:04";
        String dateTimeB = "2022-01-01 10:06:05";
        DateTimeOps dateTimeOpsA = DateTimeOps.parse("UTC", pattern, dateTimeA);
        DateTimeOps dateTimeOpsB = DateTimeOps.parse("UTC", pattern, dateTimeB);
        assertTrue(dateTimeOpsA.isBefore(dateTimeOpsB));
        assertFalse(dateTimeOpsA.isBefore(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:04")));
        assertFalse(dateTimeOpsA.isBefore(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:03")));
        Date date = new Date();
        System.out.println(date.getTime());
        DateTimeOps dateTimeOps = DateTimeOps.of("UTC", date);
        int minute = dateTimeOps.getHour() * 60 + dateTimeOps.getMinute();
        System.out.println(minute);
        System.out.println("星期" + dateTimeOps.getDayOfWeek());
        System.out.println(dateTimeOps.addDays(-1).format(pattern));
        System.out.println("星期" + dateTimeOps.getDayOfWeek());
        for (int i = 0; i < 24; i++) {
            if (dateTimeOps.getHour() < 12) {
                assertTrue(dateTimeOps.isAM());
                assertFalse(dateTimeOps.isPM());
            } else {
                assertTrue(dateTimeOps.isPM());
                assertFalse(dateTimeOps.isAM());
            }
            String pattern = "MM/dd/yyyy hh:mm";
            assertEquals(dateTimeOps.format(pattern) + (dateTimeOps.isAM() ? "AM" : "PM"),
                dateTimeOps.formatAndAmPm(pattern));
            dateTimeOps.addHours(1);
        }
    }

    @Test
    public void testAddDateTime() {
        String dateTime = "2022-01-01 00:00:00";
        DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, dateTime);
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addYears(1);
            assertEquals(i + 2022, dateTimeOps.getYear());
        }
        assertEquals("2027-01-01 00:00:00", dateTimeOps.format(pattern));
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addMonths(1);
            assertEquals(i + 1, dateTimeOps.getMonth());
        }
        assertEquals("2027-06-01 00:00:00", dateTimeOps.format(pattern));
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addDays(1);
            assertEquals(i + 1, dateTimeOps.getDayOfMonth());
        }
        assertEquals("2027-06-06 00:00:00", dateTimeOps.format(pattern));
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addHours(1);
            assertEquals(i, dateTimeOps.getHour());
        }
        assertEquals("2027-06-06 05:00:00", dateTimeOps.format(pattern));
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addMinutes(1);
            assertEquals(i, dateTimeOps.getMinute());
        }
        assertEquals("2027-06-06 05:05:00", dateTimeOps.format(pattern));
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addSeconds(1);
            assertEquals(i, dateTimeOps.getSecond());
        }
        assertEquals("2027-06-06 05:05:05", dateTimeOps.format(pattern));
    }

    @Test
    public void testDaylightSavingsEmpty() {
        assertFalse(DateTimeOps.now(shanghaiZone).getDaylightSavingsMissingHour().isPresent());
        assertFalse(DateTimeOps.now(shanghaiZone).getWinterTimeHourBack().isPresent());
    }

    @Test(expected = DateTimeParseException.class)
    public void testParseErrorWithInvalidDate() {
        DateTimeOps.parse(null, pattern, "invalidDate");
    }

    @Test(expected = ZoneRulesException.class)
    public void testParseErrorWithInvalidZone() {
        DateTimeOps.parse("invalidZone", pattern, "2022-01-01 00:00:00");
    }

    @Test(expected = DateTimeException.class)
    public void testOfErrorWithInvalidZone() {
        DateTimeOps.of("invalidZone", 0L);
    }

    @Test
    public void testParseDate() {
        String dateStr = "dateX";
        try {
            DateTimeOps.parseDate(null, "yyyy-MM-dd", dateStr);
            fail("未出现预期异常");
        } catch (DateTimeParseException e) {
            assertEquals("Text '" + dateStr + "' could not be parsed at index 0", e.getMessage());
        }
        String zone = "xx";
        dateStr = "2022-01-01";
        try {
            DateTimeOps.parseDate(zone, "yyyy-MM-dd HH:mm:ss", dateStr);
            fail("未出现预期异常");
        } catch (DateTimeParseException e) {
            assertEquals("Text '" + dateStr + "' could not be parsed at index 10", e.getMessage());
        }
        try {
            DateTimeOps.parseDate(zone, "yyyy-MM-dd", dateStr);
            fail("未出现预期异常");
        } catch (ZoneRulesException e) {
            assertEquals("Unknown time-zone ID: " + zone, e.getMessage());
        }
        DateTimeOps dateTimeOps = DateTimeOps.parseDate(shanghaiZone, pattern, "2022-02-01 10:30:05");
        assertEquals("2022-02-01 00:00:00", dateTimeOps.format(pattern));
    }
}