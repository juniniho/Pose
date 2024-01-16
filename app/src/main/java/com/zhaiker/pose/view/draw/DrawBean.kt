package com.zhaiker.pose.view.draw

import android.graphics.Canvas
import android.graphics.Paint

/**
 * 绘画基类
 */
abstract class DrawBean {

    protected open var paint:Paint = Paint()

    //表示延时的毫秒数
    protected open var delay:Int = 25

    //动画播放时长，默认为500毫秒
    open var duration:Int = 500

    //需要执行的步数
    val steps:Int
        get() {
            if(delay==0) return 1
            return (duration*1f/delay).toInt()
        }

    //当前执行的步数
    protected open var stepIndex = 0

    open fun setup(delay:Int){
        this.delay = delay
        paint.isAntiAlias = true
        paint.flags = Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
        paint.textSize = 18f
    }

    abstract fun draw(canvas:Canvas)

}