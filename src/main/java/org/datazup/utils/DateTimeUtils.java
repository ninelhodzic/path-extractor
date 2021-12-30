
package org.datazup.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by admin@datazup on 11/25/16.
 */

public class DateTimeUtils {

    private static List<DateTimeFormatter> COMMON_DATE_TIME_FORMATS =
            Arrays.asList(ISODateTimeFormat.dateTime(),
                    getFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                    getFormatter("yyyy-MM-dd'T'HH:mm:ss"),
                    getFormatter("yyyy-MM-dd'T'HH:mm:ssZ"),
                    getFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
                    getFormatter("yyyy-MM-dd'T'hh:mm:ssZ a"),

                    getFormatter("yyyy-MM-dd HH:mm:ss"),
                    getFormatter("yyyy-MM-dd HH:mm:ss"),

                    getFormatter("EEE MMM dd HH:mm:ss Z yyyy"),
                    getFormatter("EEE MMM dd HH:mm:ss zz yyyy"),
                    getFormatter("YYYYMMddHHmmss.SSSZ"));

    private static DateTimeFormatter getFormatter(String format) {
        DateTimeFormatter fmt;
        if (StringUtils.isEmpty(format)) {
            fmt = DateTimeFormat.longDateTime().withZoneUTC().withLocale(Locale.ENGLISH);//fullDateTime().withZoneUTC().withLocale(Locale.ENGLISH);
        } else {
            fmt = DateTimeFormat.forPattern(format).withZoneUTC().withLocale(Locale.ENGLISH);
        }
        return fmt;
    }

    /*   private static DateTimeFormatter getFormatter(String format, Locale locale) {

           DateTimeFormatter fmt = DateTimeFormat.forPattern(format).withZoneUTC().withLocale(locale);
           return fmt;
       }
   */
    public static ZoneOffset resolveZoneOffset(Integer tzValue) {
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        if (null == tzValue)
            return zoneOffset;


        if (tzValue >= -18 && tzValue <= 18) {
            zoneOffset = ZoneOffset.ofHours(tzValue);
        } else {
            tzValue = tzValue / 60;// if it is in minutes
            zoneOffset = ZoneOffset.ofHours(tzValue);
        }
        return zoneOffset;
    }

    public static ZoneOffset resolveZoneOffset(Object tzValue) {
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        if (null == tzValue)
            return zoneOffset;

        Integer i = TypeUtils.resolveInteger(tzValue);
        if (null != i) {
            zoneOffset = resolveZoneOffset(i);
        }

        return zoneOffset;
    }

    public static Instant resolve(Object dtValue, Object tzValue) {
        Instant dtInstant = resolve(dtValue);
        if (null == dtInstant)
            return null;
        if (null == tzValue)
            return dtInstant;

        ZoneOffset zoneOffset = resolveZoneOffset(tzValue);

        Instant dtInstantWithZone = dtInstant.atOffset(zoneOffset).toLocalDateTime().toInstant(ZoneOffset.UTC);
        return dtInstantWithZone;
    }

    public static Instant resolve(Object obj) {
        if (null == obj) return null;
        if (obj instanceof Instant) {
            return (Instant) obj;
        } else if (obj instanceof DateTime) {
            return from((DateTime) obj);
        } else if (obj instanceof Date) {
            return ((Date) obj).toInstant();
        } else if (obj instanceof java.sql.Date) {
            return Instant.ofEpochMilli(((java.sql.Date) obj).getTime());
        } else if (obj instanceof Number)
            return Instant.ofEpochMilli(((Number) obj).longValue());
        else if (obj instanceof String) {
            Instant dt = null;
            try {
                dt = Instant.parse((String) obj);
                return dt;
            } catch (Exception e) {
                // nothing
            }

            if (null == dt) {
                for (DateTimeFormatter fmt : COMMON_DATE_TIME_FORMATS) {
                    dt = resolve(fmt, (String) obj);
                    if (null != dt) {
                        return dt;
                    }
                }
            }
        }
        return null;
    }

    public static Instant from(DateTime dt) {
        Instant i = Instant.ofEpochMilli(dt.getMillis());
        return i;
    }

    public static Instant resolve(DateTimeFormatter fmt, String dateString) {

        try {
            DateTime dt = DateTime.parse(dateString, fmt);
            dt = dt.withZone(DateTimeZone.UTC);
            return from(dt);
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    public static Instant resolve(Long timestamp) {
        return Instant.ofEpochMilli(timestamp);
    }

    public static Instant resolve(String datetime) {
        return Instant.parse(datetime);
    }

    public static Instant resolve(String datetime, String format) {
        DateTimeFormatter fmt = getFormatter(format);
        return resolve(fmt, datetime);
    }

    public static int getSecond(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getSecond();
    }

    public static int getMinute(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getMinute();
    }

    public static int getHour(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getHour();
    }

    public static int getDayOfMonth(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getDayOfMonth();
    }

    public static int getMonth(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getMonthValue();
    }

    public static int getYear(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).getYear();
    }

    public static Instant format(Instant dt, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        DateTime dti = new DateTime(dt.toEpochMilli());
        String dtString = dti.toString(formatter);
        return resolve(dtString, format);
    }

    public static Instant format(DateTime dt, String format) {
        String dtString = dt.toString(format);

        return resolve(dtString, format);
    }

    public static Instant format(Date dt, String format) {
        Instant instant = dt.toInstant();
        return format(instant, format);
    }

    public static Instant format(Long dt, String format) {
        Instant instant = Instant.ofEpochMilli(dt);
        if (null == format || format.isEmpty()) {
            return instant;
        }
        return format(instant, format);
    }

}
