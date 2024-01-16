package com.zhaiker.pose.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description: 时间工具类（时间格式转换方便类）
 */
internal object TimeUtils {

    private val SIMPLE_DATE_FORMAT = SimpleDateFormat()

    /**
     * 返回一定格式的当前时间
     *
     * @param pattern "yyyy-MM-dd HH:mm:ss E"
     * @return
     */
    fun getCurrentDate(pattern: String): String {
        SIMPLE_DATE_FORMAT.applyPattern(pattern)
        val date = Date(System.currentTimeMillis())
        return SIMPLE_DATE_FORMAT.format(date)
    }

    fun getDateMillis(dateString: String, pattern: String): Long {
        var millionSeconds: Long = 0
        SIMPLE_DATE_FORMAT.applyPattern(pattern)
        try {
            millionSeconds = SIMPLE_DATE_FORMAT.parse(dateString).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        // 毫秒

        return millionSeconds
    }

    /**
     * 格式化输入的millis
     *
     * @param millis
     * @param pattern yyyy-MM-dd HH:mm:ss E
     * @return
     */
    fun dateFormat(millis: Long, pattern: String): String {
        SIMPLE_DATE_FORMAT.applyPattern(pattern)
        val date = Date(millis)
        return SIMPLE_DATE_FORMAT.format(date)
    }

    /**
     * 将dateString原来old格式转换成new格式
     *
     * @param dateString
     * @param oldPattern yyyy-MM-dd HH:mm:ss E
     * @param newPattern
     * @return oldPattern和dateString形式不一样直接返回dateString
     */
    fun dateFormat(dateString: String, oldPattern: String,
                   newPattern: String): String {
        val millis = getDateMillis(dateString, oldPattern)
        return if (0L == millis) {
            dateString
        } else dateFormat(millis, newPattern)
    }

    /**
     * 获取今天的星期几序号，星期一 返回0  星期天返回 6
     */
    fun getTodayWeekIndex():Int{
        Locale.setDefault(Locale.CHINA)
        var  week =  Calendar.getInstance()[Calendar.DAY_OF_WEEK]-2
        return if(week<0) 6 else week
    }
    /**
     * 获取指定日期的星期数
     */
    fun getWeekIndex(date:Date):Int{
        Locale.setDefault(Locale.CHINA)
        var calendar = Calendar.getInstance()
        calendar.time = date
        var  week =  calendar[Calendar.DAY_OF_WEEK]-2
        return if(week<0) 6 else week
    }

    /**
     * 获取一年的第一天
     */
    fun getFirstDayOfYear():Date{
        val date = Calendar.getInstance()
        date.set(date[Calendar.YEAR],0,1,0,0,0)
        return date.time
    }
    /**
     * 获取本月的第一天
     */
    fun getFirstDayOfMonth():Date{
        val date = Calendar.getInstance()
        date.set(date[Calendar.YEAR],date[Calendar.MONTH],1,0,0,0)
        return date.time
    }

    /**
     * 近七天
     */
    fun getFirstDayOfNearSeven():Date{
        val date = Calendar.getInstance()
        date[Calendar.DAY_OF_YEAR] = date[Calendar.DAY_OF_YEAR]-7
        return date.time
    }

}
