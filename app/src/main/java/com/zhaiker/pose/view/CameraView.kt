package com.zhaiker.pose.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.*
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.zhaiker.bodyscale.utils.ImageUtils
import com.zhaiker.pose.beans.Size
import com.zhaiker.pose.utils.Logger


/**
 * 摄像头视图
 */
@Suppress("DEPRECATION")
class CameraView:LinearLayout, SurfaceHolder.Callback, Camera.PreviewCallback {

    companion object{
        //全屏
        const val MODEL_FULL_SCREEN = 0
        //宽度优先
        const val MODEL_FILL_WIDTH = 1
        //高度优先
        const val MODEL_FILL_HEIGHT = 2

        const val ROTATION_0 = 0

        const val ROTATION_90 = 90

        const val ROTATION_180 = 180

        const val ROTATION_270 = 270
    }

    private var mBuffer: ByteArray?=null
    //回调帧
    var frameCallback:((bitmap:YuvImage,fliped:Boolean)->Unit?)?=null

    private var previewSize:Camera.Size?=null

    var direction:Int
        get() {
            val config = context.getSharedPreferences("zk_camera_config", MODE_PRIVATE)
            val r = config.getInt("rotation",90)
            Logger.log("获取摄像头旋转角度：${r}")
            return if(r>-1){
                r
            }else{
                ROTATION_90
            }
        }
        set(value) {
            if(value!=ROTATION_0&&value!=ROTATION_90&&value!=ROTATION_180&&value!=ROTATION_270){
                return
            }
            try {
                val config = context.getSharedPreferences("zk_camera_config", MODE_PRIVATE)
                val edit = config.edit()
                edit.putInt("rotation", value)
                edit.apply()
                this.stopPreview()
                this.adjuestOptimalSize()
                Logger.log("成功设置摄像头旋转角度：${value}")
            }catch(e:java.lang.Exception){
                e.printStackTrace()
                Logger.log(e.localizedMessage)
            }
        }

    //是否镜像旋转
    var isFliped:Boolean
        get() {
            val config = context.getSharedPreferences("zk_camera_config", MODE_PRIVATE)
            val r = config.getBoolean("mirror",false)
            Logger.log("获取摄像头镜像：${r}")
            return r
        }
        set(value) {
            try {
                val config = context.getSharedPreferences("zk_camera_config", MODE_PRIVATE)
                val edit = config.edit()
                edit.putBoolean("mirror", value)
                edit.apply()
                this.stopPreview()
                this.adjuestOptimalSize()
                Logger.log("成功设置摄像头镜像旋转：${value}")
            }catch(e:java.lang.Exception){
                e.printStackTrace()
                Logger.log(e.localizedMessage)
            }
        }

    private var innerFliped = false

    private var mCamera: Camera?=null

    private var isPreview: Boolean = false

    private var cameraPreview: SurfaceView?=null

    private var mHolder: SurfaceHolder?=null

    private var innerWidth = 0f

    private var innerHeight = 0f

    val viewWidth:Float
        get() {return innerWidth}

    val viewHeight:Float
        get() {
            return innerHeight
        }

    var mode:Int = MODEL_FULL_SCREEN
    //是否在预览
    val isPreviewing:Boolean
        get() {
            return isPreview
        }
    //释放时回调
    var onDestroyed:(()->Unit?)?=null
    //截图回调
    private var screenshotCallback:((img:Bitmap?)->Unit?)?=null

    constructor(context: Context) : super(context){init() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init() }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ){init()}


    private fun init(){
        cameraPreview = SurfaceView(context)
        this.mHolder = cameraPreview!!.holder
        this.mHolder!!.addCallback(this)
        //this.setBackgroundColor(Color.BLACK)
        this.gravity = Gravity.CENTER
        this.addView(cameraPreview)
    }

    /**
     * 设置摄像头
     */
    fun setCamera(camera:Camera){
        mCamera = camera
        //camera.setDisplayOrientation(90)
        preview()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //preview()
    }
    /**
     *预览与摄像头设置
     */
    private fun preview(){
        try {
            if (mCamera == null) return
            if (isPreview) {
                mCamera!!.stopPreview()
            }
            adjuestOptimalSize()
            isPreview = true
        }catch (e:Exception){
            e.printStackTrace()
            Logger.log("ERROR：${e.message}")
        }
    }

    //开始预览
    fun startPreview(){
        try {
            if (mCamera == null) return
            mCamera!!.startPreview()
            isPreview = true
        }catch (e:Exception){
            Logger.log("ERROR：${e.cause}")
        }
    }

    //停止预览
    fun stopPreview(){
        try {
            if (mCamera == null) return
            mCamera!!.stopPreview()
            isPreview = false
        }catch (e:Exception){
            Logger.log("ERROR：${e.cause}")
        }
    }

    /**
     * 拍照
     */
    fun takePhoto(callback:(data:ByteArray)->Unit?){
        try {
            mCamera?.takePicture(null, null, null) { data, camera1 ->
                //视图动画
                callback(data)
            }
        }catch (e:java.lang.Exception){
            Logger.log("拍照发生异常了....")
        }
    }

    /**
     * 获取原尺寸截图
     */
    fun getScreenshot(callback:((data:Bitmap?)->Unit?)){
        screenshotCallback = callback  //设置回调
    }

    //调整最佳的显示尺寸
    private fun adjuestOptimalSize(){
        val parameters = mCamera!!.parameters
        val sizes = parameters.supportedPreviewSizes
        var maxW = 0
        val maxSize = Size(0,0)
        for(size in sizes){
            if(maxW<size.width){
                maxW = size.width
                maxSize.width = size.width
                maxSize.height = size.height
            }
        }

        val dr = direction

        /* Image format NV21 causes issues in the Android emulators */
        if (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || "google_sdk" == Build.PRODUCT
        ) parameters.previewFormat = ImageFormat.YV12 // "generic" or "android" = android emulator
        else parameters.previewFormat = ImageFormat.NV21

        Logger.log("摄像头最大分辨率：${maxSize.width}  *  ${maxSize.height}  旋转角度：${dr}")
        BaseView.setSize(cameraPreview!!,width, (width*(maxSize.width*1f/maxSize.height*1f)).toInt())

        innerWidth = width.toFloat()
        innerHeight = (width*(maxSize.width*1f/maxSize.height*1f))

        parameters.setPreviewSize(maxSize.width, maxSize.height)
        previewSize = parameters.previewSize
        parameters.setPictureSize(640, 480)
        parameters.pictureFormat = ImageFormat.JPEG
        if(dr>1) {
            parameters.setRotation(dr)
            mCamera!!.setDisplayOrientation(dr)
        }

       // parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO;  //此处因为新摄像头原因屏蔽
        mCamera!!.parameters = parameters

        try {
            mCamera!!.setPreviewCallbackWithBuffer(this)
            //设置缓存大小
            var size: Int = maxSize.width * maxSize.height
            size = size*ImageFormat.getBitsPerPixel(parameters.previewFormat)/8
            mBuffer = ByteArray(size)
            mCamera!!.addCallbackBuffer(mBuffer)
        }catch (e:Exception){
            e.printStackTrace()
            //ZkDialog.showAlert(context,"Camera Error",null)
        }


        mCamera?.setPreviewDisplay(mHolder)
        //mCamera?.setPreviewTexture(surfaceTexture)
        mCamera?.startPreview()

        if(isFliped){ //画面做翻转
            this.scaleY = -1f
            innerFliped = true
        }else{
            innerFliped = false
        }

        Logger.log("摄像头开始预览........")
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        if (mCamera != null) {
            if (isPreview) {
                //正在预览
                mCamera!!.stopPreview()
            }
            releaseCamera()
        }
        try {
            onDestroyed?.invoke()
        }catch (e:java.lang.Exception){}

    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        if(!isPreview){
            preview()
        }
    }

    //释放摄像头
    protected fun releaseCamera() {
        synchronized(this) {
            try {
                if (mCamera != null) {
                    mCamera!!.stopPreview()
                    mCamera!!.setPreviewCallback(null)
                    mCamera!!.setPreviewCallbackWithBuffer(null)
                    mCamera!!.release()
                }
                mCamera = null
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    //单帧回调
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        try {
            val callback = frameCallback
            if (data != null && callback != null) {
                callback(
                    YuvImage(
                        data,
                        ImageFormat.NV21,
                        previewSize!!.width,
                        previewSize!!.height,
                        null
                    ),innerFliped
                )
            }

            //截图回调
            try {
                val cb = screenshotCallback
                if (cb != null && data != null) {
                    Logger.log("direction:${direction}")
                    screenshotCallback = null
                    var bitmap = ImageUtils.yuv2Bmp(data,previewSize!!.width,previewSize!!.height)
                    if(direction>0&&bitmap!=null){
                        bitmap = ImageUtils.rotateBitmap(bitmap,direction,false,false)
                    }
                    cb.invoke(bitmap)
                }
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }

            if (null == data) {
                mCamera?.addCallbackBuffer(mBuffer)
            } else {
                mCamera?.addCallbackBuffer(data)
            }
        }catch (e:java.lang.Exception){Logger.log(e)}
    }
}