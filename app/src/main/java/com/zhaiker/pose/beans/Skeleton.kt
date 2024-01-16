package com.zhaiker.pose.beans

import android.graphics.PointF
import android.graphics.RectF
import androidx.core.graphics.minus
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs

/**
 * 人体骨骼点
 */
class Skeleton(pose:Pose,targetWidth:Float,targetHeight:Float) {
    //图像宽度
    val imgWidth:Float = targetWidth
    //图像高度
    val imgHeight:Float = targetHeight

    val centerX:Float
        get() {
            return imgWidth/2f
        }

    val centerY:Float
        get() {
            return imgHeight/2f
        }

    val poses:Pose = pose

    val nose:PoseLandmark
        get() {return poses.getPoseLandmark(PoseLandmark.NOSE)!!}
    val leftEyeInner:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)!!}
    val leftEye:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_EYE)!!}
    val leftEyeOuter:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)!!}
    val rightEyeInner:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)!!}
    val rightEye:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_EYE)!!}
    val rightEyeOuter:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)!!}
    val leftEar:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_EAR)!!}
    val rightEar:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_EAR)!!}
    val leftMouth:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_MOUTH)!!}
    val rightMouth:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)!!}

    val leftShoulder:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!}
    val rightShoulder:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!}
    val leftElbow:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!}
    val rightElbow:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!}
    val leftWrist:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_WRIST)!!}
    val rightWrist:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_WRIST)!!}
    val leftHip:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_HIP)!!}
    val rightHip:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_HIP)!!}
    val leftKnee:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_KNEE)!!}
    val rightKnee:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_KNEE)!!}
    val leftAnkle:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_ANKLE)!!}
    val rightAnkle:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)!!}

    val leftPinky:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_PINKY)!!}
    val rightPinky:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_PINKY)!!}
    val leftIndex:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_INDEX)!!}
    val rightIndex:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_INDEX)!!}
    val leftThumb:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_THUMB)!!}
    val rightThumb:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_THUMB)!!}
    val leftHeel:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_HEEL)!!}
    val rightHeel:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_HEEL)!!}
    val leftFootIndex:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)!!}
    val rightFootIndex:PoseLandmark
        get() {return  poses.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)!!}

    //肩宽
    val shoulderWidth:Float
        get() {
            return leftShoulder.position.minus(rightShoulder.position).length()
        }
    //臀宽
    val hipWidth:Float
        get() {
            return leftHip.position.minus(rightHip.position).length()
        }
    //是否是侧面人体
    val isSideBody:Boolean
        get() {
            return (nose.position.x - leftShoulder.position.x)*(nose.position.x - rightShoulder.position.x)>0
        }

    //侧面人体类别，1表示左面人体，0 表示正面  -1 表示右边， 2表示背面
    val sideBodyType:Int
        get() {
            val nl = (nose.position.x - leftShoulder.position.x)
            val nr = (nose.position.x - rightShoulder.position.x)
            return if(nl*nr>0){  //侧面
                if(nl>0)
                    -1
                else
                    1
            }else{
                    0
            }
        }

    private var cfd:Float = -1f

    //可信度
    val likelihood:Float
        get() {
            if(cfd==-1f){
                var sumcfd = 0f
                for (p in poses.allPoseLandmarks){
                    sumcfd+= p.inFrameLikelihood
                }
                cfd=sumcfd/poses.allPoseLandmarks.size
            }
            return cfd
        }

    //获取骨骼点矩形框
    val bodyRect:RectF
        get() {
            var top:Float = Float.MAX_VALUE
            var left:Float = Float.MAX_VALUE
            var right:Float = Float.MIN_VALUE
            var bottom:Float = Float.MIN_VALUE
            for (lmk in poses.allPoseLandmarks){
                if(top>lmk.position.y){
                    top = lmk.position.y
                }
                if(left>lmk.position.x){
                    left = lmk.position.x
                }
                if(right<lmk.position.x){
                    right = lmk.position.x
                }
                if(bottom<lmk.position.y){
                    bottom = lmk.position.y
                }
            }
            val topOffset = abs(nose.position.y - (leftShoulder.position.y+rightShoulder.position.y)/2f)
            return if(isSideBody){
                RectF(
                    left - topOffset * 2f,
                    top - topOffset,
                    right + topOffset * 2f,
                    bottom + topOffset / 5f
                )
            }else {
                RectF(
                    left - topOffset,
                    top - topOffset,
                    right + topOffset,
                    bottom + topOffset / 5f
                )
            }
        }

    //头部区域
    val headRect:RectF
        get() {
            val np = Point(nose.position)
            val le = Point(leftEar.position)
            val re = Point(rightEar.position)
            val ls = Point(leftShoulder.position)
            val rs = Point(rightShoulder.position)
            val cs = (ls+rs)/2f
            val ce = (re+le)/2f
            return if(sideBodyType==-1){  //右侧面
                val hw = abs(re.x - np.x)
                val hh = abs(re.y-rs.y)/2f
                RectF(ce.x-hw,ce.y-hh,ce.x+hw,ce.y+hh)
            }else if(sideBodyType==1){ //左边
                val hw = abs(le.x - np.x)
                val hh = abs(le.y-ls.y)/2f
                RectF(ce.x-hw,ce.y-hh,ce.x+hw,ce.y+hh)
            }else{
                val hw = abs(le.x - re.x)*1.3f
                val hh = abs(np.y-cs.y)/2f
                RectF(ce.x-hw,ce.y-hh,ce.x+hw,ce.y+hh)
            }
        }
}