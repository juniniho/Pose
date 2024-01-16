package com.zhaiker.pose.view.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import com.zhaiker.pose.beans.Point
import com.zhaiker.pose.beans.Skeleton
import com.zhaiker.pose.utils.Logger
import kotlin.math.abs

class PersonDraw: DrawBean() {
    //骨骼点
    var skeleton:Skeleton? = null
        set(value) {
            field = value
            if(value!=null){
                timestamp = System.currentTimeMillis()
                val brct = value.bodyRect
                val ws = width/brct.width()
                val hs = height/brct.height()
                scale = if(ws>hs) hs else ws
            }
        }
    //画布宽度
    private var width:Float = 1f
    //画布高度
    private var height:Float = 1f
    //画布中心x
    private val cx:Float
        get() {
            return width/2f
        }
    //画布中心Y
    private val cy:Float
        get() {
            return height/2f
        }
    //缩放比例
    private var scale:Float = 1f

    private var bodyPath = Path()

    private var timestamp:Long = 0L

    private var count:Int = 0

    private var state:Int = -1  //状态
        set(value) {
            if(field==value)return
            if(field==0&&value==1){
                count++
            }
            field=value
        }

    //画图
    override fun draw(canvas: Canvas) {
        width = canvas.width.toFloat()
        height = canvas.height.toFloat()
        val sk = skeleton?:return
        val now = System.currentTimeMillis()
        val timeout = (now-timestamp)
        if(sk.likelihood<0.7f||width<=0f||height<=0f||timeout>1000){
            if(timeout>1000){
                count = 0
            }
            return
        }

        val scale:Float = if(sk.imgWidth*1f/width>sk.imgHeight*1f/height)  sk.imgHeight*1f/height else sk.imgWidth*1f/width
        canvas.save()
        canvas.translate((width -sk.imgWidth*scale*0.8f) /2f,(height*scale-sk.imgHeight*scale)/2f)
        canvas.scale(scale,scale)

        val hrct = sk.headRect

        val lwp = Point(sk.leftWrist.position)
        val rwp = Point(sk.rightWrist.position)
        val hcp = Point(hrct.centerX(),hrct.centerY())
        val ch = (Point(sk.leftHip.position) + Point(sk.rightHip.position))/2f
        val cs = (Point(sk.leftShoulder.position) + Point(sk.rightShoulder.position))/2f
        val bh = (cs - ch).length()

        if(lwp.y>cs.y||rwp.y>cs.y){
            state = -1
        }else if(lwp.distance(rwp)<=hrct.width()){
            if(lwp.y>(hrct.top-bh*0.2f)&&rwp.y>(hrct.top-bh*0.2f)&&lwp.y<cs.y&&rwp.y<cs.y) {
                state = 0  //表示准备
            }else if(lwp.y<(hrct.top-bh*0.35f)&&rwp.y<(hrct.top-bh*0.35f)){
                state = 1
            }
        }



        bodyPath.reset()
        paint.color = zColor(0f)
        paint.textSize = 30f


        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = bh/5f
        if(sk.leftWrist.position3D.z>0f) {
            paint.shader = LinearGradient(
                sk.leftWrist.position.x,
                sk.leftWrist.position.y,
                sk.leftElbow.position.x,
                sk.leftElbow.position.y,
                intArrayOf(zColor(sk.leftWrist.position3D.z), zColor(0f)),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(
                sk.leftWrist.position.x,
                sk.leftWrist.position.y,
                sk.leftElbow.position.x,
                sk.leftElbow.position.y,
                paint
            )
        }

        if(sk.rightWrist.position3D.z>0f) {
            paint.shader = LinearGradient(
                sk.rightWrist.position.x,
                sk.rightWrist.position.y,
                sk.rightElbow.position.x,
                sk.rightElbow.position.y,
                intArrayOf(zColor(sk.rightWrist.position3D.z), zColor(0f)),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(
                sk.rightWrist.position.x,
                sk.rightWrist.position.y,
                sk.rightElbow.position.x,
                sk.rightElbow.position.y,
                paint
            )
        }
        paint.shader = null



        paint.style = Paint.Style.FILL


        paint.setShadowLayer(3f,1f,1f,Color.BLACK)
        canvas.drawCircle(hrct.centerX(),hrct.centerY(),bh/4.5f,paint)
        paint.clearShadowLayer()


        bodyPath.moveTo(sk.leftShoulder.position.x,sk.leftShoulder.position.y)
        bodyPath.lineTo(sk.rightShoulder.position.x,sk.rightShoulder.position.y)
        bodyPath.lineTo(sk.rightHip.position.x,sk.rightHip.position.y)
        bodyPath.lineTo(sk.leftHip.position.x,sk.leftHip.position.y)
        bodyPath.close()

        paint.strokeWidth = bh/8f
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(bodyPath,paint)


//        sk.leftWrist.position.x,sk.leftWrist.position.y,sk.leftElbow.position.x,sk.leftElbow.position.y,
        val handPoints = floatArrayOf(
            sk.leftElbow.position.x,sk.leftElbow.position.y,sk.leftShoulder.position.x,sk.leftShoulder.position.y,
            sk.leftShoulder.position.x,sk.leftShoulder.position.y,sk.rightShoulder.position.x,sk.rightShoulder.position.y,
            sk.rightShoulder.position.x,sk.rightShoulder.position.y,sk.rightElbow.position.x,sk.rightElbow.position.y,
            )

//        sk.rightElbow.position.x,sk.rightElbow.position.y,sk.rightWrist.position.x,sk.rightWrist.position.y

//
        val legPoints = floatArrayOf(
            sk.leftAnkle.position.x,sk.leftAnkle.position.y,sk.leftKnee.position.x,sk.leftKnee.position.y,
            sk.leftKnee.position.x,sk.leftKnee.position.y,sk.leftHip.position.x,sk.leftHip.position.y,
            sk.leftHip.position.x,sk.leftHip.position.y,sk.rightHip.position.x,sk.rightHip.position.y,
            sk.rightHip.position.x,sk.rightHip.position.y,sk.rightKnee.position.x,sk.rightKnee.position.y,
            sk.rightKnee.position.x,sk.rightKnee.position.y,sk.rightAnkle.position.x,sk.rightAnkle.position.y
            )
        //
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = bh/5f
        canvas.drawLines(handPoints,paint)
        canvas.drawLines(legPoints,paint)

        if(sk.leftWrist.position3D.z<0f) {
            paint.shader = LinearGradient(
                sk.leftWrist.position.x,
                sk.leftWrist.position.y,
                sk.leftElbow.position.x,
                sk.leftElbow.position.y,
                intArrayOf(zColor(sk.leftWrist.position3D.z), zColor(0f)),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(
                sk.leftWrist.position.x,
                sk.leftWrist.position.y,
                sk.leftElbow.position.x,
                sk.leftElbow.position.y,
                paint
            )
        }

        if(sk.rightWrist.position3D.z<0f) {
            paint.shader = LinearGradient(
                sk.rightWrist.position.x,
                sk.rightWrist.position.y,
                sk.rightElbow.position.x,
                sk.rightElbow.position.y,
                intArrayOf(zColor(sk.rightWrist.position3D.z), zColor(0f)),
                null,
                Shader.TileMode.CLAMP
            )
            canvas.drawLine(
                sk.rightWrist.position.x,
                sk.rightWrist.position.y,
                sk.rightElbow.position.x,
                sk.rightElbow.position.y,
                paint
            )
        }
        paint.shader = null

//        paint.shader = LinearGradient(sk.leftAnkle.position.x,sk.leftAnkle.position.y,sk.leftKnee.position.x,sk.leftKnee.position.y,
//            intArrayOf(zColor(sk.leftAnkle.position3D.z),zColor(0f)),null,Shader.TileMode.CLAMP)
//        canvas.drawLine(sk.leftAnkle.position.x,sk.leftAnkle.position.y,sk.leftKnee.position.x,sk.leftKnee.position.y,paint)
//
//        paint.shader = LinearGradient(sk.rightAnkle.position.x,sk.rightAnkle.position.y,sk.rightKnee.position.x,sk.rightKnee.position.y,
//            intArrayOf(zColor(sk.rightAnkle.position3D.z),zColor(0f)),null,Shader.TileMode.CLAMP)
//        canvas.drawLine(sk.rightAnkle.position.x,sk.rightAnkle.position.y,sk.rightKnee.position.x,sk.rightKnee.position.y,paint)

        canvas.restore()

        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#6dcc88")
        paint.textSize = 162f
        val txt = "$count"
        paint.alpha = 100
        canvas.drawText(txt,cx- paint.measureText(txt)/2f ,height-20,paint)
        paint.alpha = 255
    }

    //颜色
    private fun zColor(z:Float):Int{

        val v = 55f
        var coff = (z/600f)*v

        coff = if(abs(coff)>v) v*(coff/abs(coff))  else coff

        val c = ((255-v) - coff).toInt()

        return  Color.argb(255,c,c,c)
    }

}