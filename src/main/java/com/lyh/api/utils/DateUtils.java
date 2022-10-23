package com.lyh.api.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author shoushen.luan
 * @since 2022-10-23
 */
public class DateUtils {
    private static ZoneId defaultZoneId = ZoneOffset.systemDefault();

    public static Date addYears(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addYears(instant, amount).toEpochMilli());
    }

    public static Instant addYears(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusYears(amount).toInstant();
    }

    public static Date addMonths(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addMonths(instant, amount).toEpochMilli());
    }

    public static Instant addMonths(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusMonths(amount).toInstant();
    }

    public static Date addWeeks(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addWeeks(instant, amount).toEpochMilli());
    }

    public static Instant addWeeks(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusWeeks(amount).toInstant();
    }

    public static Date addDays(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addDays(instant, amount).toEpochMilli());
    }

    public static Instant addDays(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusDays(amount).toInstant();
    }

    public static Date addHours(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addHours(instant, amount).toEpochMilli());
    }

    public static Instant addHours(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusHours(amount).toInstant();
    }

    public static Date addMinutes(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addMinutes(instant, amount).toEpochMilli());
    }

    public static Instant addMinutes(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusMinutes(amount).toInstant();
    }

    public static Date addSeconds(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addSeconds(instant, amount).toEpochMilli());
    }

    public static Instant addSeconds(final Instant instant, final int amount) {
        return instant.atZone(defaultZoneId).plusSeconds(amount).toInstant();
    }

    public static Date addMilliseconds(final Date date, final int amount) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return new Date(addMilliseconds(instant, amount).toEpochMilli());
    }

    public static Instant addMilliseconds(final Instant instant, final int amount) {
        return instant.plusMillis(amount).atZone(defaultZoneId).toInstant();
    }

}
