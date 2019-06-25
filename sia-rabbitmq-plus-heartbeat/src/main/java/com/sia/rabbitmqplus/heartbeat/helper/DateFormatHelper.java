
package com.sia.rabbitmqplus.heartbeat.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: pengfeili23@creditease.cn
 * @Description: 线程安全的SimpleDateFormat
 * @date: 2018年6月21日 下午8:42:06
 */
public class DateFormatHelper {

    private DateFormatHelper() {
    }

    private static final Map<String, ThreadLocal<DateFormat>> DATE_FORMAT = new ConcurrentHashMap<String, ThreadLocal<DateFormat>>();
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * if the specified key is null and this map does not permit null keys
     *
     * @param pattern must not be null
     * @return
     */
    private static DateFormat getSDF(final String pattern) {

        // 尝试获取该 pattern 下的 DateFormat
        ThreadLocal<DateFormat> threadLocalSDF = DATE_FORMAT.get(pattern);

        // 没有则初始化
        if (threadLocalSDF == null) {
            threadLocalSDF = new ThreadLocal<DateFormat>() {

                @Override
                protected DateFormat initialValue() {

                    return new SimpleDateFormat(pattern);
                }
            };
            // the action is performed atomically，由ConcurrentHashMap操作的原子性保证线程安全
            DATE_FORMAT.putIfAbsent(pattern, threadLocalSDF);
            // 此时 DATE_FORMAT.get(pattern)不一定是前文新建的 threadLocalSDF
            threadLocalSDF = DATE_FORMAT.get(pattern);
        }
        return threadLocalSDF.get();
    }

    public static Date parse(String pattern, String dateStr) throws ParseException {

        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        return getSDF(pattern).parse(dateStr);
    }

    public static String format(String pattern, Date date) {

        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        return getSDF(pattern).format(date);
    }

    public static Date parse(String dateStr) throws ParseException {

        return getSDF(DEFAULT_PATTERN).parse(dateStr);
    }

    public static String format(Date date) {

        return getSDF(DEFAULT_PATTERN).format(date);
    }
}
