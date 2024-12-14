package de.stynxyxy.angrybot.util;

import java.time.Instant;
import java.util.Date;

public class TimeUtil {
    public static Date getCurrentTime() {
        return Date.from(Instant.now());
    }
    public static String formatTime(Date date) {
        StringBuilder time = new StringBuilder();
        time.append(date.getDay()+".");
        time.append(date.getMonth()+".");
        time.append(date.getYear()+".");
        time.append(" ");
        time.append(date.getHours()+":");
        time.append(date.getMinutes()+":");
        time.append(date.getSeconds());
        return time.toString();
    }
}
