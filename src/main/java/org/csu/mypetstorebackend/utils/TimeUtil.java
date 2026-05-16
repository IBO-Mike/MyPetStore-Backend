package org.csu.mypetstorebackend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {
    private static final DateTimeFormatter MYSQL_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtil() {
    }

    public static String currentMysqlDateTime() {
        return LocalDateTime.now().format(MYSQL_DATETIME_FORMATTER);
    }
}
