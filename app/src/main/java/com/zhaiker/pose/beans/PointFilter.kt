package com.zhaiker.pose.beans

import com.zhaiker.pose.beans.Point
import kotlin.math.abs

/**
 * 均值过滤器
 */
class PointFilter {
    //内部存储
    private var dataArray:Array<Point>?=null
    //过滤器大小
    var size:Int = 1
        set(value) {
            field = if(value<1) 1
            else
                value
            dataArray = Array(field){ Point() }
        }
    //序号
    private var index = 0

    private var isFilled:Boolean = false
    //是否最小均值
    val isFixed:Boolean
        get() {
            return isFilled
        }

    //均值
    val avgValue: Point
        get() {
            return if(isFilled) {
                sumValue / size
            }else{
                sumValue / (if(index==0) index+1 else index)
            }
        }
    //总和值
    private var sumValue: Point = Point()

    private var innerSmoothed = false

    //最大晃动幅度
    var maxRange:Float = 0f
        set(value) {
            field = value
            innerSmoothed = isSmooth(field)
        }

    //是否平滑的
    val isSmoothed:Boolean
        get() {
            return innerSmoothed
        }

    constructor(size:Int){
        this.size = size
    }

    //添加值
    fun addValue(v: Point){
        val array = dataArray ?: return
        synchronized(array) {
            sumValue -= array[index] //减去原来的值
            array[index] = v //存储新的值
            sumValue += v  //加上新值

            index++
            if (index >= array.size) {
                isFilled = true
                index = 0
            }

            innerSmoothed = isSmooth(maxRange)
        }
    }

    /**
     * 是否平滑
     * range 最大的浮动范围
     */
    fun isSmooth(range:Float):Boolean{
        val data = dataArray ?: return false
        if(!isFilled)return false
        val avg = avgValue
        val rg = abs(range)
        for(v in data.iterator()){
            if((v-avg).length()>rg){
                return false
            }
        }
        return true
    }

    //清空
    fun clear(){
        val array = dataArray ?: return
        synchronized(array) {
            isFilled = false
            sumValue = Point()
            index=0
            dataArray = Array(size){ Point() }
            innerSmoothed = false
        }
    }
}