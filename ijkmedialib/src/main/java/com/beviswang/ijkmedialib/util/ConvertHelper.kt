package com.beviswang.ijkmedialib.util

import android.content.Context

/**
 * Conversion tools
 * Created by shize on 2018/3/22.
 */
object ConvertHelper {
    /**
     * 将毫秒转换为显示时间字符串，从秒显示到小时
     *
     * @param timeValue   总时间 ms
     * @return 显示时间字符串
     */
    fun getDisplayTimeByMsec(timeValue: Long): String {
        // 将毫秒转化为秒
        val durationS = (timeValue / 1000).toInt()
        return getTimeString(durationS / 60) + ":" + getTimeString(durationS % 60)
    }

    /**
     * 将时间转化为字符串

     * @param time 时间
     * *
     * @return String
     */
    private fun getTimeString(time: Int): String {
        return if (time < 10) "0" + time else time.toString()
    }

    /**
     * 从dp转换为px

     * @param context  上下文
     * *
     * @param dipValue dp值
     * *
     * @return px值
     */
    fun dipToPx(context: Context, dipValue: Float): Float {
        // 获取比例
        val scale = context.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }

    /**
     * 从px转换为dp

     * @param context 上下文
     * *
     * @param pxValue px值
     * *
     * @return dp值
     */
    fun pxToDip(context: Context, pxValue: Float): Float {
        // 获取比例
        val scale = context.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }
}