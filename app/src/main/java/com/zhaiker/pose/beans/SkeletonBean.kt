package com.zhaiker.pose.beans

import com.google.mlkit.vision.pose.PoseLandmark

/**
 * 骨骼点运算对象
 */
class SkeletonBean {

    constructor(){}

    constructor(sk:Skeleton){
        setSkeleton(sk)
    }

    //图像宽度
    var imgWidth:Float = 0f
    //图像高度
    var imgHeight:Float = 0f

    val centerX:Float
        get() {
            return imgWidth/2f
        }

    val centerY:Float
        get() {
            return imgHeight/2f
        }
    
    var nose: Point = Point()
    var leftEyeInner: Point = Point()
    var leftEye: Point = Point()
    var leftEyeOuter: Point = Point()
    var rightEyeInner: Point = Point()
    var rightEye: Point = Point()
    var rightEyeOuter: Point = Point()
    var leftEar: Point = Point()
    var rightEar: Point = Point()
    var leftMouth: Point = Point()
    var rightMouth: Point = Point()
    var leftShoulder: Point = Point()
    var rightShoulder: Point = Point()
    var leftElbow: Point = Point()
    var rightElbow: Point = Point()
    var leftWrist: Point = Point()
    var rightWrist: Point = Point()
    var leftHip: Point = Point()
    var rightHip: Point = Point()
    var leftKnee: Point = Point()
    var rightKnee: Point = Point()
    var leftAnkle: Point = Point()
    var rightAnkle: Point = Point()
    var leftPinky: Point = Point()
    var rightPinky: Point = Point()
    var leftIndex: Point = Point()
    var rightIndex: Point = Point()
    var leftThumb: Point = Point()
    var rightThumb: Point = Point()
    var leftHeel: Point = Point()
    var rightHeel: Point = Point()
    var leftFootIndex: Point = Point()
    var rightFootIndex: Point = Point()

    //赋值骨骼点
    fun setSkeleton(sk:Skeleton){
        imgHeight = sk.imgHeight
        imgWidth = sk.imgWidth
        cvt(nose, sk.nose)
        cvt(leftEyeInner, sk.leftEyeInner)
        cvt(leftEye, sk.leftEye)
        cvt(leftEyeOuter, sk.leftEyeOuter)
        cvt(rightEyeInner, sk.rightEyeInner)
        cvt(rightEye, sk.rightEye)
        cvt(rightEyeOuter, sk.rightEyeOuter)
        cvt(leftEar, sk.leftEar)
        cvt(rightEar, sk.rightEar)
        cvt(leftMouth, sk.leftMouth)
        cvt(rightMouth, sk.rightMouth)
        cvt(leftShoulder, sk.leftShoulder)
        cvt(rightShoulder, sk.rightShoulder)
        cvt(leftElbow, sk.leftElbow)
        cvt(rightElbow, sk.rightElbow)
        cvt(leftWrist, sk.leftWrist)
        cvt(rightWrist, sk.rightWrist)
        cvt(leftHip, sk.leftHip)
        cvt(rightHip, sk.rightHip)
        cvt(leftKnee, sk.leftKnee)
        cvt(rightKnee, sk.rightKnee)
        cvt(leftAnkle, sk.leftAnkle)
        cvt(rightAnkle, sk.rightAnkle)
        cvt(leftPinky, sk.leftPinky)
        cvt(rightPinky, sk.rightPinky)
        cvt(leftIndex, sk.leftIndex)
        cvt(rightIndex, sk.rightIndex)
        cvt(leftThumb, sk.leftThumb)
        cvt(rightThumb, sk.rightThumb)
        cvt(leftHeel, sk.leftHeel)
        cvt(rightHeel, sk.rightHeel)
        cvt(leftFootIndex, sk.leftFootIndex)
        cvt(rightFootIndex, sk.rightFootIndex)
    }

    private fun cvt(p:Point,lm:PoseLandmark){
        p.x = lm.position3D.x
        p.y = lm.position3D.y
    }

}