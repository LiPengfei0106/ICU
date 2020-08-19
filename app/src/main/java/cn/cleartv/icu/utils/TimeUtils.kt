package cn.cleartv.icu.utils

import androidx.annotation.IntDef
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/02
 * desc  : utils about time
</pre> *
 */
object TimeUtils {
    const val MSEC = 1
    const val SEC = 1000
    const val MIN = 60000
    const val HOUR = 3600000
    const val DAY = 86400000

    @IntDef(MSEC, SEC, MIN, HOUR, DAY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TimeUnit

    private val SDF_THREAD_LOCAL = ThreadLocal<SimpleDateFormat>()

    private val defaultFormat: SimpleDateFormat = getDateFormat("yyyy-MM-dd HH:mm:ss")

    private fun getDateFormat(pattern: String): SimpleDateFormat {
        var simpleDateFormat = SDF_THREAD_LOCAL.get()
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            SDF_THREAD_LOCAL.set(simpleDateFormat)
        } else {
            simpleDateFormat.applyPattern(pattern)
        }
        return simpleDateFormat
    }

    /**
     * Milliseconds to the formatted time string.
     *
     * @param millis  The milliseconds.
     * @param pattern The pattern of date format, such as yyyy/MM/dd HH:mm
     * @return the formatted time string
     */
    fun millis2String(millis: Long, pattern: String): String {
        return millis2String(millis, getDateFormat(pattern))
    }
    /**
     * Milliseconds to the formatted time string.
     *
     * @param millis The milliseconds.
     * @param format The format.
     * @return the formatted time string
     */
    /**
     * Milliseconds to the formatted time string.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param millis The milliseconds.
     * @return the formatted time string
     */
    @JvmOverloads
    fun millis2String(millis: Long, format: DateFormat = defaultFormat): String {
        return format.format(Date(millis))
    }

    /**
     * Formatted time string to the milliseconds.
     *
     * @param time    The formatted time string.
     * @param pattern The pattern of date format, such as yyyy/MM/dd HH:mm
     * @return the milliseconds
     */
    fun string2Millis(time: String, pattern: String): Long {
        return string2Millis(time, getDateFormat(pattern))
    }
    /**
     * Formatted time string to the milliseconds.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the milliseconds
     */
    /**
     * Formatted time string to the milliseconds.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the milliseconds
     */
    @JvmOverloads
    fun string2Millis(time: String, format: DateFormat = defaultFormat): Long {
        try {
            return format.parse(time)?.time ?: -1
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return -1
    }

    /**
     * Formatted time string to the date.
     *
     * @param time    The formatted time string.
     * @param pattern The pattern of date format, such as yyyy/MM/dd HH:mm
     * @return the date
     */
    fun string2Date(time: String, pattern: String): Date? {
        return string2Date(time, getDateFormat(pattern))
    }
    /**
     * Formatted time string to the date.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the date
     */
    /**
     * Formatted time string to the date.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the date
     */
    @JvmOverloads
    fun string2Date(time: String, format: DateFormat = defaultFormat): Date? {
        try {
            return format.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Date to the formatted time string.
     *
     * @param date    The date.
     * @param pattern The pattern of date format, such as yyyy/MM/dd HH:mm
     * @return the formatted time string
     */
    fun date2String(date: Date, pattern: String): String {
        return getDateFormat(pattern).format(date)
    }
    /**
     * Date to the formatted time string.
     *
     * @param date   The date.
     * @param format The format.
     * @return the formatted time string
     */
    /**
     * Date to the formatted time string.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param date The date.
     * @return the formatted time string
     */
    @JvmOverloads
    fun date2String(
        date: Date,
        format: DateFormat = defaultFormat
    ): String {
        return format.format(date)
    }

    /**
     * Return the time span, in unit.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time1 The first formatted time string.
     * @param time2 The second formatted time string.
     * @param unit  The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span, in unit
     */
    fun getTimeSpan(
        time1: String,
        time2: String,
        @TimeUnit unit: Int
    ): Long {
        return getTimeSpan(time1, time2, defaultFormat, unit)
    }

    /**
     * Return the time span, in unit.
     *
     * @param time1  The first formatted time string.
     * @param time2  The second formatted time string.
     * @param format The format.
     * @param unit   The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span, in unit
     */
    fun getTimeSpan(
        time1: String,
        time2: String,
        format: DateFormat,
        @TimeUnit unit: Int
    ): Long {
        return millis2TimeSpan(
            string2Millis(
                time1,
                format
            ) - string2Millis(time2, format), unit
        )
    }

    /**
     * Return the time span, in unit.
     *
     * @param date1 The first date.
     * @param date2 The second date.
     * @param unit  The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span, in unit
     */
    fun getTimeSpan(
        date1: Date,
        date2: Date,
        @TimeUnit unit: Int
    ): Long {
        return millis2TimeSpan(date1.time - date2.time, unit)
    }

    /**
     * Return the time span, in unit.
     *
     * @param millis1 The first milliseconds.
     * @param millis2 The second milliseconds.
     * @param unit    The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span, in unit
     */
    fun getTimeSpan(
        millis1: Long,
        millis2: Long,
        @TimeUnit unit: Int
    ): Long {
        return millis2TimeSpan(millis1 - millis2, unit)
    }

    /**
     * Return the fit time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time1     The first formatted time string.
     * @param time2     The second formatted time string.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        time1: String,
        time2: String,
        precision: Int
    ): String? {
        val delta = string2Millis(time1, defaultFormat) - string2Millis(time2, defaultFormat)
        return millis2FitTimeSpan(delta, precision)
    }

    /**
     * Return the fit time span.
     *
     * @param time1     The first formatted time string.
     * @param time2     The second formatted time string.
     * @param format    The format.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        time1: String,
        time2: String,
        format: DateFormat,
        precision: Int
    ): String? {
        val delta = string2Millis(time1, format) - string2Millis(time2, format)
        return millis2FitTimeSpan(delta, precision)
    }

    /**
     * Return the fit time span.
     *
     * @param date1     The first date.
     * @param date2     The second date.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        date1: Date,
        date2: Date,
        precision: Int
    ): String? {
        return millis2FitTimeSpan(date1.time - date2.time, precision)
    }

    /**
     * Return the fit time span.
     *
     * @param millis1   The first milliseconds.
     * @param millis2   The second milliseconds.
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return the fit time span
     */
    fun getFitTimeSpan(
        millis1: Long,
        millis2: Long,
        precision: Int
    ): String? {
        return millis2FitTimeSpan(millis1 - millis2, precision)
    }

    /**
     * Return the current time in milliseconds.
     *
     * @return the current time in milliseconds
     */
    val nowMills: Long
        get() = System.currentTimeMillis()

    /**
     * Return the current formatted time string.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @return the current formatted time string
     */
    val nowString: String
        get() = millis2String(
            System.currentTimeMillis(),
            defaultFormat
        )

    /**
     * Return the current formatted time string.
     *
     * @param format The format.
     * @return the current formatted time string
     */
    fun getNowString(format: DateFormat): String {
        return millis2String(
            System.currentTimeMillis(),
            format
        )
    }

    /**
     * Return the current date.
     *
     * @return the current date
     */
    val nowDate: Date
        get() = Date()

    /**
     * Return the time span by now, in unit.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @param unit The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(time: String, @TimeUnit unit: Int): Long {
        return getTimeSpan(time, nowString, defaultFormat, unit)
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @param unit   The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(
        time: String,
        format: DateFormat,
        @TimeUnit unit: Int
    ): Long {
        return getTimeSpan(time, getNowString(format), format, unit)
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param date The date.
     * @param unit The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(date: Date, @TimeUnit unit: Int): Long {
        return getTimeSpan(date, Date(), unit)
    }

    /**
     * Return the time span by now, in unit.
     *
     * @param millis The milliseconds.
     * @param unit   The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the time span by now, in unit
     */
    fun getTimeSpanByNow(millis: Long, @TimeUnit unit: Int): Long {
        return getTimeSpan(millis, System.currentTimeMillis(), unit)
    }

    /**
     * Return the fit time span by now.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time      The formatted time string.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(time: String, precision: Int): String? {
        return getFitTimeSpan(time, nowString, defaultFormat, precision)
    }

    /**
     * Return the fit time span by now.
     *
     * @param time      The formatted time string.
     * @param format    The format.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(time: String, format: DateFormat, precision: Int): String? {
        return getFitTimeSpan(time, getNowString(format), format, precision)
    }

    /**
     * Return the fit time span by now.
     *
     * @param date      The date.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(date: Date, precision: Int): String? {
        return getFitTimeSpan(date, nowDate, precision)
    }

    /**
     * Return the fit time span by now.
     *
     * @param millis    The milliseconds.
     * @param precision The precision of time span.
     *
     *  * precision = 0，返回 null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision &gt;= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return the fit time span by now
     */
    fun getFitTimeSpanByNow(millis: Long, precision: Int): String? {
        return getFitTimeSpan(millis, System.currentTimeMillis(), precision)
    }

    /**
     * Return the friendly time span by now.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(time: String): String {
        return getFriendlyTimeSpanByNow(time, defaultFormat)
    }

    /**
     * Return the friendly time span by now.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(time: String, format: DateFormat): String {
        return getFriendlyTimeSpanByNow(
            string2Millis(time, format)
        )
    }

    /**
     * Return the friendly time span by now.
     *
     * @param date The date.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(date: Date): String {
        return getFriendlyTimeSpanByNow(date.time)
    }

    /**
     * Return the friendly time span by now.
     *
     * @param millis The milliseconds.
     * @return the friendly time span by now
     *
     *  * 如果小于 1 秒钟内，显示刚刚
     *  * 如果在 1 分钟内，显示 XXX秒前
     *  * 如果在 1 小时内，显示 XXX分钟前
     *  * 如果在 1 小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(millis: Long): String {
        val now = System.currentTimeMillis()
        val span = now - millis
        when {
            span < 0 -> return String.format("%tc", millis)
            span < 1000 -> {
                return "刚刚"
            }
            span < MIN -> {
                return java.lang.String.format(
                    Locale.getDefault(),
                    "%d秒前",
                    span / SEC
                )
            }
            span < HOUR -> {
                return java.lang.String.format(
                    Locale.getDefault(),
                    "%d分钟前",
                    span / MIN
                )
            }
            // 获取当天 00:00
            else -> {
                val wee = weeOfToday
                return when {
                    millis >= wee -> {
                        String.format("今天%tR", millis)
                    }
                    millis >= wee - DAY -> {
                        String.format("昨天%tR", millis)
                    }
                    else -> {
                        String.format("%tF", millis)
                    }
                }
            }
        }
    }

    private val weeOfToday: Long
        get() {
            val cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.timeInMillis
        }

    /**
     * Return the milliseconds differ time span.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the milliseconds differ time span
     */
    fun getMillis(millis: Long, timeSpan: Long, @TimeUnit unit: Int): Long {
        return millis + timeSpan2Millis(timeSpan, unit)
    }

    /**
     * Return the milliseconds differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the milliseconds differ time span
     */
    fun getMillis(time: String, timeSpan: Long, @TimeUnit unit: Int): Long {
        return getMillis(time, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the milliseconds differ time span.
     */
    fun getMillis(time: String, format: DateFormat, timeSpan: Long, @TimeUnit unit: Int): Long {
        return string2Millis(time, format) + timeSpan2Millis(timeSpan, unit)
    }

    /**
     * Return the milliseconds differ time span.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the milliseconds differ time span.
     */
    fun getMillis(date: Date, timeSpan: Long, @TimeUnit unit: Int): Long {
        return date.time + timeSpan2Millis(timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(millis: Long, timeSpan: Long, @TimeUnit unit: Int): String {
        return getString(millis, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param millis   The milliseconds.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(millis: Long, format: DateFormat, timeSpan: Long, @TimeUnit unit: Int): String {
        return millis2String(millis + timeSpan2Millis(timeSpan, unit), format)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(time: String, timeSpan: Long, @TimeUnit unit: Int): String {
        return getString(time, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(time: String, format: DateFormat, timeSpan: Long, @TimeUnit unit: Int): String {
        return millis2String(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit), format)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(date: Date, timeSpan: Long, @TimeUnit unit: Int): String {
        return getString(date, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span.
     *
     * @param date     The date.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span
     */
    fun getString(date: Date, format: DateFormat, timeSpan: Long, @TimeUnit unit: Int): String {
        return millis2String(date.time + timeSpan2Millis(timeSpan, unit), format)
    }

    /**
     * Return the date differ time span.
     *
     * @param millis   The milliseconds.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the date differ time span
     */
    fun getDate(millis: Long, timeSpan: Long, @TimeUnit unit: Int): Date {
        return Date(millis + timeSpan2Millis(timeSpan, unit))
    }

    /**
     * Return the date differ time span.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time     The formatted time string.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the date differ time span
     */
    fun getDate(time: String, timeSpan: Long, @TimeUnit unit: Int): Date {
        return getDate(time, defaultFormat, timeSpan, unit)
    }

    /**
     * Return the date differ time span.
     *
     * @param time     The formatted time string.
     * @param format   The format.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the date differ time span
     */
    fun getDate(time: String, format: DateFormat, timeSpan: Long, @TimeUnit unit: Int): Date {
        return Date(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit))
    }

    /**
     * Return the date differ time span.
     *
     * @param date     The date.
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the date differ time span
     */
    fun getDate(date: Date, timeSpan: Long, @TimeUnit unit: Int): Date {
        return Date(date.time + timeSpan2Millis(timeSpan, unit))
    }

    /**
     * Return the milliseconds differ time span by now.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the milliseconds differ time span by now
     */
    fun getMillisByNow(timeSpan: Long, @TimeUnit unit: Int): Long {
        return getMillis(nowMills, timeSpan, unit)
    }

    /**
     * Return the formatted time string differ time span by now.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span by now
     */
    fun getStringByNow(timeSpan: Long, @TimeUnit unit: Int): String {
        return getStringByNow(timeSpan, defaultFormat, unit)
    }

    /**
     * Return the formatted time string differ time span by now.
     *
     * @param timeSpan The time span.
     * @param format   The format.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the formatted time string differ time span by now
     */
    fun getStringByNow(timeSpan: Long, format: DateFormat, @TimeUnit unit: Int): String {
        return getString(nowMills, format, timeSpan, unit)
    }

    /**
     * Return the date differ time span by now.
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [MSEC]
     *  * [SEC]
     *  * [MIN]
     *  * [HOUR]
     *  * [DAY]
     *
     * @return the date differ time span by now
     */
    fun getDateByNow(timeSpan: Long, @TimeUnit unit: Int): Date {
        return getDate(nowMills, timeSpan, unit)
    }

    /**
     * Return whether it is today.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(time: String): Boolean {
        return isToday(string2Millis(time, defaultFormat))
    }

    /**
     * Return whether it is today.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(time: String, format: DateFormat): Boolean {
        return isToday(string2Millis(time, format))
    }

    /**
     * Return whether it is today.
     *
     * @param date The date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(date: Date): Boolean {
        return isToday(date.time)
    }

    /**
     * Return whether it is today.
     *
     * @param millis The milliseconds.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isToday(millis: Long): Boolean {
        val wee = weeOfToday
        return millis >= wee && millis < wee + DAY
    }

    /**
     * Return whether it is leap year.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(time: String): Boolean {
        return string2Date(time, defaultFormat)?.let { isLeapYear(it) } ?: false
    }

    /**
     * Return whether it is leap year.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(time: String, format: DateFormat): Boolean {
        return string2Date(time, format)?.let { isLeapYear(it) } ?: false
    }

    /**
     * Return whether it is leap year.
     *
     * @param date The date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(date: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.time = date
        val year = cal[Calendar.YEAR]
        return isLeapYear(year)
    }

    /**
     * Return whether it is leap year.
     *
     * @param millis The milliseconds.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(millis: Long): Boolean {
        return isLeapYear(Date(millis))
    }

    /**
     * Return whether it is leap year.
     *
     * @param year The year.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    /**
     * Return the day of week in Chinese.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the day of week in Chinese
     */
    fun getChineseWeek(time: String): String? {
        return string2Date(time, defaultFormat)?.let { getChineseWeek(it) }
    }

    /**
     * Return the day of week in Chinese.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the day of week in Chinese
     */
    fun getChineseWeek(time: String, format: DateFormat): String? {
        return string2Date(time, format)?.let { getChineseWeek(it) }
    }

    /**
     * Return the day of week in Chinese.
     *
     * @param date The date.
     * @return the day of week in Chinese
     */
    fun getChineseWeek(date: Date): String {
        return SimpleDateFormat("E", Locale.CHINA).format(date)
    }

    /**
     * Return the day of week in Chinese.
     *
     * @param millis The milliseconds.
     * @return the day of week in Chinese
     */
    fun getChineseWeek(millis: Long): String {
        return getChineseWeek(Date(millis))
    }

    /**
     * Return the day of week in US.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the day of week in US
     */
    fun getUSWeek(time: String): String? {
        return string2Date(time, defaultFormat)?.let { getUSWeek(it) }
    }

    /**
     * Return the day of week in US.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the day of week in US
     */
    fun getUSWeek(time: String, format: DateFormat): String? {
        return string2Date(time, format)?.let { getUSWeek(it) }
    }

    /**
     * Return the day of week in US.
     *
     * @param date The date.
     * @return the day of week in US
     */
    fun getUSWeek(date: Date): String {
        return SimpleDateFormat("EEEE", Locale.US).format(date)
    }

    /**
     * Return the day of week in US.
     *
     * @param millis The milliseconds.
     * @return the day of week in US
     */
    fun getUSWeek(millis: Long): String {
        return getUSWeek(Date(millis))
    }

    /**
     * Return whether it is am.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isAm: Boolean
        get() {
            val cal = Calendar.getInstance()
            return cal[GregorianCalendar.AM_PM] == 0
        }

    /**
     * Return whether it is am.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(time: String): Boolean {
        return getValueByCalendarField(time, defaultFormat, GregorianCalendar.AM_PM) == 0
    }

    /**
     * Return whether it is am.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(time: String, format: DateFormat): Boolean {
        return getValueByCalendarField(time, format, GregorianCalendar.AM_PM) == 0
    }

    /**
     * Return whether it is am.
     *
     * @param date The date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(date: Date): Boolean {
        return getValueByCalendarField(date, GregorianCalendar.AM_PM) == 0
    }

    /**
     * Return whether it is am.
     *
     * @param millis The milliseconds.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAm(millis: Long): Boolean {
        return getValueByCalendarField(millis, GregorianCalendar.AM_PM) == 0
    }

    /**
     * Return whether it is am.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isPm: Boolean
        get() = !isAm

    /**
     * Return whether it is am.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(time: String): Boolean {
        return !isAm(time)
    }

    /**
     * Return whether it is am.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(time: String, format: DateFormat): Boolean {
        return !isAm(time, format)
    }

    /**
     * Return whether it is am.
     *
     * @param date The date.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(date: Date): Boolean {
        return !isAm(date)
    }

    /**
     * Return whether it is am.
     *
     * @param millis The milliseconds.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPm(millis: Long): Boolean {
        return !isAm(millis)
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param field The given calendar field.
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return the value of the given calendar field
     */
    fun getValueByCalendarField(field: Int): Int {
        val cal = Calendar.getInstance()
        return cal[field]
    }

    /**
     * Returns the value of the given calendar field.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time  The formatted time string.
     * @param field The given calendar field.
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return the value of the given calendar field
     */
    fun getValueByCalendarField(time: String, field: Int): Int {
        return string2Date(time, defaultFormat)?.let { getValueByCalendarField(it, field) } ?: -1
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @param field  The given calendar field.
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return the value of the given calendar field
     */
    fun getValueByCalendarField(time: String, format: DateFormat, field: Int): Int {
        return string2Date(time, format)?.let { getValueByCalendarField(it, field) } ?: -1
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param date  The date.
     * @param field The given calendar field.
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return the value of the given calendar field
     */
    fun getValueByCalendarField(date: Date, field: Int): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[field]
    }

    /**
     * Returns the value of the given calendar field.
     *
     * @param millis The milliseconds.
     * @param field  The given calendar field.
     *
     *  * [Calendar.ERA]
     *  * [Calendar.YEAR]
     *  * [Calendar.MONTH]
     *  * ...
     *  * [Calendar.DST_OFFSET]
     *
     * @return the value of the given calendar field
     */
    fun getValueByCalendarField(millis: Long, field: Int): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return cal[field]
    }

    private val CHINESE_ZODIAC =
        arrayOf("猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊")

    /**
     * Return the Chinese zodiac.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the Chinese zodiac
     */
    fun getChineseZodiac(time: String): String? {
        return string2Date(time, defaultFormat)?.let { getChineseZodiac(it) }
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the Chinese zodiac
     */
    fun getChineseZodiac(time: String, format: DateFormat): String? {
        return string2Date(time, format)?.let { getChineseZodiac(it) }
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param date The date.
     * @return the Chinese zodiac
     */
    fun getChineseZodiac(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        return CHINESE_ZODIAC[cal[Calendar.YEAR] % 12]
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param millis The milliseconds.
     * @return the Chinese zodiac
     */
    fun getChineseZodiac(millis: Long): String {
        return getChineseZodiac(Date(millis))
    }

    /**
     * Return the Chinese zodiac.
     *
     * @param year The year.
     * @return the Chinese zodiac
     */
    fun getChineseZodiac(year: Int): String {
        return CHINESE_ZODIAC[year % 12]
    }

    private val ZODIAC_FLAGS =
        intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)
    private val ZODIAC = arrayOf(
        "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
        "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"
    )

    /**
     * Return the zodiac.
     *
     * The pattern is `yyyy-MM-dd HH:mm:ss`.
     *
     * @param time The formatted time string.
     * @return the zodiac
     */
    fun getZodiac(time: String): String? {
        return string2Date(time, defaultFormat)?.let { getZodiac(it) }
    }

    /**
     * Return the zodiac.
     *
     * @param time   The formatted time string.
     * @param format The format.
     * @return the zodiac
     */
    fun getZodiac(time: String, format: DateFormat): String? {
        return string2Date(time, format)?.let { getZodiac(it) }
    }

    /**
     * Return the zodiac.
     *
     * @param date The date.
     * @return the zodiac
     */
    fun getZodiac(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        val month = cal[Calendar.MONTH] + 1
        val day = cal[Calendar.DAY_OF_MONTH]
        return getZodiac(month, day)
    }

    /**
     * Return the zodiac.
     *
     * @param millis The milliseconds.
     * @return the zodiac
     */
    fun getZodiac(millis: Long): String {
        return getZodiac(
            Date(millis)
        )
    }

    /**
     * Return the zodiac.
     *
     * @param month The month.
     * @param day   The day.
     * @return the zodiac
     */
    fun getZodiac(month: Int, day: Int): String {
        return ZODIAC[if (day >= ZODIAC_FLAGS[month - 1]
        ) month - 1 else (month + 10) % 12]
    }

    private fun timeSpan2Millis(timeSpan: Long, @TimeUnit unit: Int): Long {
        return timeSpan * unit
    }

    private fun millis2TimeSpan(millis: Long, @TimeUnit unit: Int): Long {
        return millis / unit
    }

    private fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
        var tempMillis = millis
        var tempPrecision = precision
        if (tempPrecision <= 0) return null
        tempPrecision = tempPrecision.coerceAtMost(5)
        val units =
            arrayOf("天", "小时", "分钟", "秒", "毫秒")
        if (tempMillis == 0L) return "0${units[tempPrecision - 1]}"
        val sb = StringBuilder()
        if (tempMillis < 0) {
            sb.append("-")
            tempMillis = -tempMillis
        }
        val unitLen = intArrayOf(DAY, HOUR, MIN, SEC, MSEC)
        for (i in 0 until tempPrecision) {
            if (tempMillis >= unitLen[i]) {
                val mode = tempMillis / unitLen[i]
                tempMillis -= mode * unitLen[i]
                sb.append(mode).append(units[i])
            }
        }
        return sb.toString()
    }
}