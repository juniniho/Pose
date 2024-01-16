package com.zhaiker.bodyscale.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


class ImageUtils{

    companion object {

        fun yuv420ToNv21(image: ImageProxy):ByteArray {
            val planes = image.planes
            val yBuffer: ByteBuffer = planes[0].buffer
            val uBuffer: ByteBuffer = planes[1].buffer
            val vBuffer: ByteBuffer = planes[2].buffer

            val ySize: Int = yBuffer.remaining()
            val uSize: Int = uBuffer.remaining()
            val vSize: Int = vBuffer.remaining()
            val size = image.width * image.height
            val nv21 = ByteArray(size * 3 / 2)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            val u = ByteArray(uSize)
            uBuffer.get(u)
            //每隔开一位替换V，达到VU交替
            var pos = ySize + 1
            for (i in 0 until uSize) {
                if (i % 2 == 0) {
                    nv21[pos] = u[i]
                    pos += 2
                }

            }
            return nv21
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            // 取 drawable 的长宽
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight
            // 取 drawable 的颜色格式
            val config = if (drawable.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
            else
                Bitmap.Config.RGB_565
            // 建立对应 bitmap
            val bitmap = Bitmap.createBitmap(w, h, config)
            // 建立对应 bitmap 的画布
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, w, h)
            // 把 drawable 内容画到画布中
            drawable.draw(canvas)
            return bitmap
        }

        //保存文件
        fun saveImage(image:Bitmap,format:Bitmap.CompressFormat,path:String):Boolean{
            try{
                val file = File(path)
                if(!file.exists()){
                    file.createNewFile()
                }
                val out = FileOutputStream(file)
                image.compress(format,100,out)
                out.flush()
                out.close()
                return true
            }catch (e:Exception){
                e.printStackTrace()
                return false
            }
        }

        /**
         * bitmap转base64
         */
        fun bitmapToBase64(bitmap: Bitmap?,format: Bitmap.CompressFormat): String? {
            var result: String? = null
            var baos: ByteArrayOutputStream? = null
            try {
                if (bitmap != null) {
                    baos = ByteArrayOutputStream()
                    bitmap.compress(format, 100, baos)
                    baos.flush()
                    baos.close()
                    val bitmapBytes: ByteArray = baos.toByteArray()
                    result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    if (baos != null) {
                        baos.flush()
                        baos.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return result
        }

        /**
         * yuv转bitmap
         */
        fun yuv2Bmp(data: ByteArray?, width: Int, height: Int): Bitmap? {
            val bitmap: Bitmap
            val newOpts = BitmapFactory.Options()
            newOpts.inJustDecodeBounds = true
            val yuvimage = YuvImage(
                data,
                ImageFormat.NV21,
                width,
                height,
                null
            )
            val baos: ByteArrayOutputStream = ByteArrayOutputStream()
            yuvimage.compressToJpeg(Rect(0, 0, width, height), 100, baos)
            val rawImage: ByteArray = baos.toByteArray()
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.size, options)
            return bitmap
        }


        /**
         * 旋转图片
         */
        fun rotateBitmap(
            bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
        ): Bitmap {
            val matrix = Matrix()
            // Rotate the image back to straight.
            matrix.postRotate(rotationDegrees.toFloat())
            // Mirror the image along the X or Y axis.
            matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
            val rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            // Recycle the old bitmap if it has changed.
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            return rotatedBitmap
        }

    }
}