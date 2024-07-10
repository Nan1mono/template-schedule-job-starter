package com.project.template.schedule_job.core.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuartzCronGenerator {

    // 生成每秒执行一次的 Cron 表达式
    public static String everySecond() {
        return "0/1 * * * * ?";
    }

    // 生成每分钟执行一次的 Cron 表达式
    public static String everyMinute() {
        return "0 * * * * ?";
    }

    // 生成每小时执行一次的 Cron 表达式
    public static String everyHour() {
        return "0 0 * * * ?";
    }

    // 生成每天固定时间执行的 Cron 表达式，如每天的10点15分
    public static String everyDayAtFixedTime(int hour, int minute) {
        return String.format("0 %d %d * * ?", minute, hour);
    }

    // 生成每周固定星期几的 Cron 表达式，如每周一的10点15分
    public static String everyWeekOnDayAtTime(DayOfWeek dayOfWeek, int hour, int minute) {
        return String.format("0 %d %d ? * %d", minute, hour, dayOfWeek.getValue());
    }

    // 生成每月固定日期时间执行的 Cron 表达式，如每月1号的10点15分
    public static String everyMonthOnDateAtTime(int dayOfMonth, int hour, int minute) {
        return String.format("0 %d %d %d * ?", minute, hour, dayOfMonth);
    }

    // 生成指定日期时间执行的 Cron 表达式，如2023年5月20日10点15分
    public static String specificDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("ss mm HH dd MM ? yyyy"));
    }

    // 生成每隔多少秒执行一次的 Cron 表达式
    public static String everyNSeconds(int seconds) {
        return String.format("0/%d * * * * ?", seconds);
    }

}
