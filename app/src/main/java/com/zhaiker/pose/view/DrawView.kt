package com.zhaiker.pose.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.annotation.RequiresApi
import com.zhaiker.pose.view.draw.DrawBean
import kotlin.collections.ArrayList

//独立线程的画板
class DrawView:SurfaceView, SurfaceHolder.Callback,Runnable {

    var play = false
        set(value) {
            if(!field&&value)
                drawThread.start()
            field = value
        }

    private val paint: Paint = Paint()

    private val delay = 25L

    private var mSurfaceHolder: SurfaceHolder? = null

    private val drawThread = Thread(this)

    var drawFun:((canvas:Canvas)->Unit)? = null
    //绘画列表
    private val drawList = ArrayList<DrawBean>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init{
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        mSurfaceHolder = holder
        mSurfaceHolder!!.addCallback(this)
        isFocusable = true
        keepScreenOn = true
        isFocusableInTouchMode = true
        // 让整个界面透明
        this.setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT);
    }

    //添加绘画单元
    fun addDraw(bean: DrawBean){
        synchronized(this.drawList) {
            bean.setup(this.delay.toInt())
            this.drawList.add(bean)
        }
    }
    //删除
    fun removeDraw(bean: DrawBean){
        synchronized(this.drawList) {
            this.drawList.remove(bean)
        }
    }

    //清除所有绘画单元
    fun clearDraw(){
        synchronized(this.drawList) {
            this.drawList.clear()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
       // play = false
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        play = true
    }

    override fun run() {
        try {
            while (play) {
                val canvas = mSurfaceHolder!!.lockCanvas()
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    try {
                        for (draw in drawList) {
                            draw.draw(canvas)
                        }
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                    val df = drawFun
                    if (df != null) {
                        df(canvas)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
                mSurfaceHolder!!.unlockCanvasAndPost(canvas)
                Thread.sleep(delay)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}