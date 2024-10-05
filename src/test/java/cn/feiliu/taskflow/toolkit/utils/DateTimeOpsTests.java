package cn.feiliu.taskflow.toolkit.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * 日期时间操作
 *
 * @author shoushen.luan
 * @since 2022-12-22
 */
public class DateTimeOpsTests {
    final String pattern = "yyyy-MM-dd HH:mm:ss";

    @Test
    public void testWorldTimeZone() {
        for (String availableZoneId : ZoneId.getAvailableZoneIds()) {
            System.out.println(availableZoneId + "-->>"
                               + DateTimeOps.localNow().convertAndFormat(availableZoneId, pattern));
        }
    }

    @Test
    public void test() {
        System.out.println(DateTimeOps.localNow());
        System.out.println(DateTimeOps.utcNow());
        System.out.println(DateTimeOps.now(DateTimeOps.NEW_YORK.getId()));

        String dateTime = "2024-10-05 15:36:59";
        Long utcMills = DateTimeOps.parse(DateTimeOps.UTC, DateTimeOps.SIMPLE_FMT, dateTime).getMillis();
        Long newYorkMills = DateTimeOps.parse(DateTimeOps.NEW_YORK, DateTimeOps.SIMPLE_FMT, dateTime).getMillis();
        Long shanghaiMills = DateTimeOps.parse(DateTimeOps.SHANG_HAI, DateTimeOps.SIMPLE_FMT, dateTime).getMillis();
        System.out.println("UTC: " + utcMills + "  -> " + DateTimeOps.of(DateTimeOps.UTC, utcMills));
        System.out.println("纽约: " + newYorkMills + " -> " + DateTimeOps.of(DateTimeOps.NEW_YORK, newYorkMills));
        System.out.println("上海: " + shanghaiMills + " -> " + DateTimeOps.of(DateTimeOps.SHANG_HAI, shanghaiMills));
        Assert.assertNotEquals(utcMills, newYorkMills);
        Assert.assertNotEquals(newYorkMills, shanghaiMills);
        Assert.assertNotEquals(utcMills, shanghaiMills);
        Assert.assertTrue(DateTimeOps.of(DateTimeOps.UTC, utcMills).toString().startsWith(dateTime));
        Assert.assertTrue(DateTimeOps.of(DateTimeOps.NEW_YORK, newYorkMills).toString().startsWith(dateTime));
        Assert.assertTrue(DateTimeOps.of(DateTimeOps.SHANG_HAI, shanghaiMills).toString().startsWith(dateTime));
    }

    /**
     * 北京时间转换到UTC时间
     */
    @Test
    public void testUtcTimeTest() {
        String dateTime = "2022-12-21 17:40:28";
        assertThat(DateTimeOps.parse("Asia/Shanghai", pattern, dateTime).convert("UTC").format(pattern)).isEqualTo(
            "2022-12-21 09:40:28");
    }

    String shanghaiZone = "Asia/Shanghai";
    String newYorkZone  = "America/New_York";

    @Test
    public void testTimeConvert() {
        String newYorkTime = "2022-12-21 03:33:17";
        String shanghaiTime = "2022-12-21 16:33:17";
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        long newYorkDateMs = DateTimeOps.parse(newYorkZone, pattern, newYorkTime).convert(newYorkZone)
            .getZonedDateTime().toInstant().toEpochMilli();
        long shanghaiDateMs = DateTimeOps.parse(shanghaiZone, pattern, shanghaiTime).convert(shanghaiZone)
            .getZonedDateTime().toInstant().toEpochMilli();
        {
            assertThat(DateTimeOps.parse(newYorkZone, pattern, newYorkTime).convert(shanghaiZone).format(pattern))
                .isEqualTo("2022-12-21 16:33:17");
            assertThat(
                DateTimeOps.parse(newYorkZone, pattern, newYorkTime).convert(shanghaiZone).getZonedDateTime()
                    .toInstant().toEpochMilli()).isEqualTo(1671611597000L);
        }
        {
            assertThat(DateTimeOps.parse(shanghaiZone, pattern, shanghaiTime).convertAndFormat(newYorkZone, pattern))
                .isEqualTo("2022-12-21 03:33:17");
            assertThat(
                DateTimeOps.parse(shanghaiZone, pattern, shanghaiTime).convert(newYorkZone).getZonedDateTime()
                    .toInstant().toEpochMilli()).isEqualTo(newYorkDateMs);

            assertThat(
                DateTimeOps.of(shanghaiZone, shanghaiDateMs).convert(newYorkZone).getZonedDateTime().toInstant()
                    .toEpochMilli()).isEqualTo(newYorkDateMs);
        }
    }

    @Test
    public void testChinaDaylightSavingTime() {
        ZoneId zoneId = ZoneId.of(shanghaiZone);
        LocalDateTime localDateTime = LocalDateTime.of(1986, 5, 3, 0, 0);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        //获取时区计时规则
        ZoneRules rules = zoneId.getRules();
        assertThat(rules.isDaylightSavings(zonedDateTime.toInstant())).isFalse();
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
        assertThat(rules.isDaylightSavings(zonedDateTime.toInstant())).isFalse();
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
    public void testEstAndEdt() throws ParseException {
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
            assertThat(operator.isDaylightSavings()).isFalse();
            System.out.println("解析:" + dateTime + "--`" + targetDateTime + "`->`" + timeMs + "`->`" + dt + "`");
        }
        String[] edtTimes = { "2021-03-14 02:00:00", "2021-03-14 03:00:00", "2021-03-14 04:00:00" };
        for (String dateTime : edtTimes) {
            DateTimeOps operator = DateTimeOps.parse(newYorkZone, pattern, dateTime);
            String targetDateTime = operator.format(newYorkPattern);
            long timeMs = operator.getZonedDateTime().toInstant().toEpochMilli();
            String dt = operator.format(pattern);
            assertThat(operator.isDaylightSavings()).isTrue();
            System.out.println("解析:" + dateTime + "--`" + targetDateTime + "`->`" + timeMs + "`->`" + dt + "`");
        }
    }

    @Test
    public void testNewYorkEdt() {
        String dateTime = "2021-03-14 03:00:00";
        assertThat(DateTimeOps.parse(newYorkZone, pattern, dateTime).isDaylightSavings()).isTrue();
        dateTime = "2021-03-14 01:00:00";
        assertThat(DateTimeOps.parse(newYorkZone, pattern, dateTime).isDaylightSavings()).isFalse();
    }

    @Test
    public void testChinaEdt() {
        String dateTime = "1986-05-04 03:00:00";
        assertThat(DateTimeOps.parse(shanghaiZone, pattern, dateTime).isDaylightSavings()).isTrue();
        dateTime = "1986-05-04 01:00:00";
        assertThat(DateTimeOps.parse(shanghaiZone, pattern, dateTime).isDaylightSavings()).isFalse();
        //夏令时
        assertThat(DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 02:00:00").format(pattern)).isEqualTo(
            "1986-05-04 03:00:00");
        assertThat(DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 05:00:00").isDaylightSavings()).isTrue();
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
            assertThat(dateTimeOps.isDaylightSavingsChangeDay()).isTrue();
            assertThat(dateTimeOps.isWinterTimeChangeDay()).isFalse();
        }
        {
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-09-14 01:00:00");
            System.out.println("冬令时切换日:" + dateTimeOps.format(DateTimeOps.PATTERN_DATE));
            assertThat(dateTimeOps.isDaylightSavingsChangeDay()).isFalse();
            assertThat(dateTimeOps.isWinterTimeChangeDay()).isTrue();
        }
        String[] zones = { shanghaiZone, newYorkZone };
        for (String zone : zones) {
            DateTimeOps dateTimeOps = DateTimeOps.parse(zone, pattern, "1986-01-01 00:00:00");
            while (dateTimeOps.getYear() < 2025) {
                if (dateTimeOps.isDaylightSavingsChangeDay()) {
                    int hour = dateTimeOps.getDaylightSavingsMissingHour().get();
                    assertThat(hour).isEqualTo(2);
                }
                if (dateTimeOps.isWinterTimeChangeDay()) {
                    int hour = dateTimeOps.getWinterTimeHourBack().get();
                    assertThat(hour).isEqualTo(1);
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
                    assertThat(hour).isEqualTo(2);
                }
                dateTimeOps.addDays(1);
            }
        }
    }

    @Test
    public void testDstHour() {
        {
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-05-04 00:00:00");
            assertThat(dateTimeOps.isDaylightSavingsChangeDay()).isTrue();
            System.out.println("------------夏令时------------");
            for (int i = 0; i < 23; i++) {
                System.out.println(dateTimeOps.toString() + "->" + dateTimeOps.getHourOfDay());
                dateTimeOps.addHours(1);
                if (dateTimeOps.getHour() <= 1) {
                    assertThat(dateTimeOps.getHour()).isEqualTo(dateTimeOps.getHourOfDay());
                } else {
                    assertThat(dateTimeOps.getHour() - 1).isEqualTo(dateTimeOps.getHourOfDay());
                }
            }
        }
        {
            System.out.println("------------冬令时------------");
            DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-09-14 00:00:00");
            assertThat(dateTimeOps.isWinterTimeChangeDay()).isTrue();
            for (int i = 0; i < 25; i++) {
                System.out.println(dateTimeOps + "->" + dateTimeOps.getHourOfDay());
                if (dateTimeOps.getHour() == 0) {
                    assertThat(dateTimeOps.getHour()).isEqualTo(dateTimeOps.getHourOfDay());
                } else if (dateTimeOps.getHour() > 1) {
                    assertThat(dateTimeOps.getHour() + 1).isEqualTo(dateTimeOps.getHourOfDay());
                } else {
                    if (i == 1) {
                        assertThat(dateTimeOps.getHour()).isEqualTo(dateTimeOps.getHourOfDay());
                    } else {
                        assertThat(dateTimeOps.getHour() + 1).isEqualTo(dateTimeOps.getHourOfDay());
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
        assertThat(DateTimeOps.of(shanghaiZone, startHour1).format(pattern)).isEqualTo(result);
        assertThat(DateTimeOps.of(shanghaiZone, startHour2).format(pattern)).isEqualTo(result);
    }

    @Test
    public void testWinterTimeHourBack() {
        DateTimeOps dateTimeOps = DateTimeOps.parse(shanghaiZone, pattern, "1986-01-01 00:00:00");
        while (dateTimeOps.getYear() < 2025) {
            if (dateTimeOps.isWinterTimeChangeDay()) {
                int hour = dateTimeOps.getWinterTimeHourBack().get();
                System.out.println(dateTimeOps.format("yyyy-MM-dd") + "(指针回拨:" + hour + "时)");
                assertThat(hour).isEqualTo(1);
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
        assertThat(DateTimeOps.parse("UTC", pattern, utcDateTime).format(pattern)).isEqualTo(utcDateTime).isEqualTo(
            DateTimeOps.of("UTC", timeMs).format(pattern));
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
        assertThat(DateTimeOps.of("UTC", utcTimeMs).convertAndFormat(shanghaiZone, pattern)).isEqualTo(
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
            assertThat(dateTimeOps.getNanoOfSecond() % 1000000).isZero();
        }
        {
            dateTimeOps.truncatedToMillis();
            assertThat(dateTimeOps.getMicroOfSecond() % 1000).isZero();
            System.out.println("trimMillis:" + dateTimeOps.getMillis() + "|微秒:" + dateTimeOps.getMicroOfSecond()
                               + "|纳秒:" + dateTimeOps.getNanoOfSecond());
        }
        {
            dateTimeOps.truncatedToSeconds();
            System.out.println("毫秒:" + dateTimeOps.getMillis() + "|微秒:" + dateTimeOps.getMicroOfSecond() + "|纳秒:"
                               + dateTimeOps.getNanoOfSecond());
            System.out.println("trimSeconds:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertThat(dateTimeOps.getMilliOfSecond()).isZero();
        }
        {
            dateTimeOps.truncatedToMinutes();
            System.out.println("trimMinutes:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertThat(dateTimeOps.getZonedDateTime().getSecond()).isZero();
        }
        {//trimHour
            dateTimeOps.truncatedToHour();
            System.out.println("trimHour:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertThat(dateTimeOps.getZonedDateTime().getMinute()).isZero();
        }
        {//截取到天(小时清零)
            dateTimeOps.truncatedToDay();
            System.out.println("trimDay:" + dateTimeOps.getMillis() + "--" + dateTimeOps.format(pattern));
            assertThat(dateTimeOps.getZonedDateTime().getHour()).isZero();
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
        assertThat(dateTimeOpsNewYork.convertAndFormat(newYorkZone, pattern)).isEqualTo(dateTime);
        dateTimeOpsNewYork.convert("UTC");
        assertThat(dateTimeOpsNewYork.getYear()).isEqualTo(2022);
        assertThat(dateTimeOpsNewYork.getMonth()).isEqualTo(1);
        assertThat(dateTimeOpsNewYork.getDayOfMonth()).isEqualTo(1);
        assertThat(dateTimeOpsNewYork.getHour()).isEqualTo(15);
        assertThat(dateTimeOpsNewYork.getMinute()).isEqualTo(0);
        assertThat(dateTimeOpsNewYork.getSecond()).isEqualTo(0);

        DateTimeOps dateTimeOps = DateTimeOps.parse("UTC", pattern, dateTime);
        assertThat(dateTimeOps.getZone()).isEqualTo(ZoneId.of("UTC"));
        assertThat(dateTimeOps.convertAndFormat("UTC", pattern)).isEqualTo(dateTime);
        assertThat(dateTimeOps.convertAndFormat(shanghaiZone, pattern)).isEqualTo("2022-01-01 18:00:00");
        assertThat(dateTimeOps.getZone()).isEqualTo(ZoneId.of(shanghaiZone));

    }

    @Test
    public void testAfter() {
        String dateTimeA = "2022-01-01 10:06:05";
        String dateTimeB = "2022-01-01 10:06:04";
        DateTimeOps dateTimeOpsA = DateTimeOps.parse("UTC", pattern, dateTimeA);
        DateTimeOps dateTimeOpsB = DateTimeOps.parse("UTC", pattern, dateTimeB);
        assertThat(dateTimeOpsA.isAfter(dateTimeOpsB)).isTrue();
        assertThat(dateTimeOpsA.isAfter(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:05"))).isFalse();
        assertThat(dateTimeOpsA.isAfter(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:06"))).isFalse();
    }

    @Test
    public void testBefore() {
        String dateTimeA = "2022-01-01 10:06:04";
        String dateTimeB = "2022-01-01 10:06:05";
        DateTimeOps dateTimeOpsA = DateTimeOps.parse("UTC", pattern, dateTimeA);
        DateTimeOps dateTimeOpsB = DateTimeOps.parse("UTC", pattern, dateTimeB);
        assertThat(dateTimeOpsA.isBefore(dateTimeOpsB)).isTrue();
        assertThat(dateTimeOpsA.isBefore(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:04"))).isFalse();
        assertThat(dateTimeOpsA.isBefore(DateTimeOps.parse("UTC", pattern, "2022-01-01 10:06:03"))).isFalse();
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
                assertThat(dateTimeOps.isAM()).isTrue();
                assertThat(dateTimeOps.isPM()).isFalse();
            } else {
                assertThat(dateTimeOps.isPM()).isTrue();
                assertThat(dateTimeOps.isAM()).isFalse();
            }
            String pattern = "MM/dd/yyyy hh:mm";
            assertThat(dateTimeOps.format(pattern) + (dateTimeOps.isAM() ? "AM" : "PM")).isEqualTo(
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
            assertThat(dateTimeOps.getYear()).isEqualTo(i + 2022);
        }
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2027-01-01 00:00:00");
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addMonths(1);
            assertThat(dateTimeOps.getMonth()).isEqualTo(i + 1);
        }
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2027-06-01 00:00:00");
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addDays(1);
            assertThat(dateTimeOps.getDayOfMonth()).isEqualTo(i + 1);
        }
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2027-06-06 00:00:00");
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addHours(1);
            assertThat(dateTimeOps.getHour()).isEqualTo(i);
        }
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2027-06-06 05:00:00");
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addMinutes(1);
            assertThat(dateTimeOps.getMinute()).isEqualTo(i);
        }
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2027-06-06 05:05:00");
        for (int i = 1; i <= 5; i++) {
            dateTimeOps.addSeconds(1);
            assertThat(dateTimeOps.getSecond()).isEqualTo(i);
        }
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2027-06-06 05:05:05");
    }

    @Test
    public void testDaylightSavingsEmpty() {
        assertThat(DateTimeOps.now(shanghaiZone).getDaylightSavingsMissingHour().isPresent()).isFalse();
        assertThat(DateTimeOps.now(shanghaiZone).getWinterTimeHourBack().isPresent()).isFalse();
    }

    @Test
    public void testParseError() {
        String dateTime = "dateX";
        try {
            DateTimeOps.parse(null, pattern, dateTime);
            fail("未出现预期异常");
        } catch (DateTimeParseException e) {
            assertThat(e.getMessage()).isEqualTo("Text '" + dateTime + "' could not be parsed at index 0");
        }
        String zone = "xx";
        dateTime = "2022-01-01 00:00:00";
        try {
            DateTimeOps.parse(zone, pattern, dateTime);
            fail("未出现预期异常");
        } catch (ZoneRulesException e) {
            assertThat(e.getMessage()).isEqualTo("Unknown time-zone ID: " + zone);
        }
    }

    @Test
    public void testParseDate() {
        String dateStr = "dateX";
        try {
            DateTimeOps.parseDate(null, "yyyy-MM-dd", dateStr);
            fail("未出现预期异常");
        } catch (DateTimeParseException e) {
            assertThat(e.getMessage()).isEqualTo("Text '" + dateStr + "' could not be parsed at index 0");
        }
        String zone = "xx";
        dateStr = "2022-01-01";
        try {
            DateTimeOps.parseDate(zone, "yyyy-MM-dd HH:mm:ss", dateStr);
            fail("未出现预期异常");
        } catch (DateTimeParseException e) {
            assertThat(e.getMessage()).isEqualTo("Text '" + dateStr + "' could not be parsed at index 10");
        }
        try {
            DateTimeOps.parseDate(zone, "yyyy-MM-dd", dateStr);
            fail("未出现预期异常");
        } catch (ZoneRulesException e) {
            assertThat(e.getMessage()).isEqualTo("Unknown time-zone ID: " + zone);
        }
        DateTimeOps dateTimeOps = DateTimeOps.parseDate(shanghaiZone, pattern, "2022-02-01 10:30:05");
        assertThat(dateTimeOps.format(pattern)).isEqualTo("2022-02-01 00:00:00");
    }

    @Test
    public void testOfError() {
        try {
            DateTimeOps.of("x", 0L);
            fail("未出现预期异常");
        } catch (DateTimeException e) {
            assertThat("Invalid ID for ZoneOffset, invalid format: x").isEqualTo(e.getMessage());
        }

    }
}
