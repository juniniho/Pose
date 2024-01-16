package com.zhaiker.pose.beans

import android.graphics.PointF
import kotlin.math.sqrt

/**
 * 点
 */
class Point {

    var x: Float = 0f
    var y: Float = 0f

    constructor()

    constructor( x: Float,  y: Float){
        this.x = x
        this.y = y
    }

    constructor(p:PointF){
        this.x = p.x
        this.y = p.y
    }

    //加法运算
    operator fun plus(p: Point): Point {
        return Point(x+p.x,y+p.y)
    }

//    operator fun plusAssign(p:Point){
//        x+=p.x
//        y+=p.y
//        z+=p.z
//    }

    //减法
    operator fun minus(p: Point): Point {
        return Point(x-p.x,y-p.y)
    }
//
//    operator fun minusAssign(p:Point):Point{
//        x-=p.x
//        y-=p.y
//        z-=p.z
//    }

    //乘法
    operator fun times(t:Float): Point {
        return Point(x*t,y*t)
    }

    //乘法
    operator fun times(t:Int): Point {
        return Point(x*t,y*t)
    }

    //乘法
    operator fun times(t:Double): Point {
        return Point((x*t).toFloat(), (y*t).toFloat())
    }

    //除法
    operator fun div(t:Float): Point {
        return Point(x/t,y/t)
    }

    //除法
    operator fun div(t:Int): Point {
        return Point(x/t,y/t)
    }

    //除法
    operator fun div(t:Double): Point {
        return Point((x/t).toFloat(), (y/t).toFloat())
    }

    //表示向量时的长度
    fun length():Float{
        return sqrt(x*x+y*y)
    }

    //与另外一个点的距离
    fun distance(p: Point):Float{
        return (p - this).length()
    }

    override fun toString(): String {
        return "Point($x, $y)";
    }


    override fun equals(other: Any?): Boolean {
        if(other is Point){
            return this.x == other.x && this.y == other.y
        }
        return super.equals(other)
    }

    companion object{
        //向量点积运算
        fun dot(p1: Point, p2: Point):Float{
            return (p1.x*p2.x + p1.y*p2.y)
        }

        fun fromPointF(p : PointF) = Point(p.x,p.y)

        fun minusNullable(p1 : Point?, p2 : Point?) : Point?{
            if(p1 == null || p2 == null) return null
            return p1 - p2
        }
    }
}