package com.zhaiker.pose

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.Surface
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.zhaiker.pose.beans.Skeleton
import com.zhaiker.pose.databinding.ActivityMainBinding
import com.zhaiker.pose.utils.Logger
import com.zhaiker.pose.view.draw.PersonDraw


class MainActivity : AppCompatActivity() {

    private var LAST_ROTATION = Surface.ROTATION_0

    val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()

    val poseDetector = PoseDetection.getClient(options)

    val pd = PersonDraw()


    private lateinit var here: ActivityMainBinding

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        here = ActivityMainBinding.inflate(layoutInflater)
        setContentView(here.root)

        here.drawView.addDraw(pd)

//        val mediaController = MediaController(this)
//
//        //绑定mediaController
//        here.videoView.setMediaController(mediaController)

        here.videoView.setVideoURI(Uri.parse("https://zhaiker.oss-cn-hangzhou.aliyuncs.com/video/hupx.mp4"))

        here.videoView.start()
        here.videoView.setOnCompletionListener {
            here.videoView.start()
        }


        //选择后置摄像头
        val cameraSelector = CameraSelector.Builder().build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraProvider = cameraProviderFuture.get()
        //创建预览
        val preview = Preview.Builder().build()

        if(here.previewView.viewPort!=null){
            LAST_ROTATION = here.previewView.viewPort!!.rotation
        }

        //图片分析器
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(480,640))
            .setTargetRotation(LAST_ROTATION)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        //设置处理内容
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imgProxy ->
            val mediaImage = imgProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imgProxy.imageInfo.rotationDegrees)
                try {
                    //骨骼点识别
                    poseDetector.process(image)
                        .addOnSuccessListener { poses ->
                            try {
                                if (poses.allPoseLandmarks.isNotEmpty()) {
                                    val sk = Skeleton(poses,image.width.toFloat(),image.height.toFloat())
                                    pd.skeleton = sk
                                }
                            } catch (e: Exception) {
                                Logger.log("人体骨骼点识别回调时发生异常：")
                                Logger.log(e)
                            }
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                        .addOnCompleteListener {
                            //Logger.log("###########")
                            imgProxy.close()
                        }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

        }
        //将生命周期,选择摄像头,预览,绑定到相机
        cameraProvider?.bindToLifecycle(this, cameraSelector,imageAnalysis,preview)

        preview.setSurfaceProvider(here.previewView.surfaceProvider)
    }
}