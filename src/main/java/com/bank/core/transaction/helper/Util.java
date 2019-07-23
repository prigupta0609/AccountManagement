package com.bank.core.transaction.helper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Util {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static DateTime getCurrentDateTime() {
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_TIME_PATTERN);
        DateTime dateTime = LocalDateTime.now().toDateTime(DateTimeZone.UTC);
        return format.parseDateTime(dateTime.toString());
    }
}
