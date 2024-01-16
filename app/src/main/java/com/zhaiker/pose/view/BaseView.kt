package com.zhaiker.pose.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import java.util.*
import kotlin.collections.HashMap
/**
 * 基础页面类
 */
open class BaseView:LinearLayout {
    var pageId:Int = 0
    set(value){
        this.removeAllViews();
        field = value
        inflate(context,pageId,this)
    }

    var actionListener: ((Any?)->Unit?)?=null
    /**
     * 其他界面传过来的数据
     */
    private var passData:HashMap<String, Any> = java.util.HashMap()

    /**
     * 关闭的时候回调
     */
    var onClosed:((status:Int,data:Any?)->Unit)?=null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * 得到输入框的文字
     * @return
     */
    protected fun getKeywordText(edt: EditText): String {
        return edt.text.toString().trim { it <= ' ' }
    }

    /**
     * 将焦点移到输入框，弹起输入法
     */
    protected fun focusKeywordView(edt: EditText?) {
        if (edt != null) {
            edt.requestFocus()
            edt.setSelection(getKeywordText(edt).length)
            showInputMethod(edt, true, 500)
        }
    }

    /**
     * 弹起输入法
     * @param edit
     * @param delay
     * @param delayTime
     */
    protected fun showInputMethod(edit: EditText, delay: Boolean, delayTime: Long) {
        if (delay) {
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    val imm = context.getSystemService(
                            Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(edit, InputMethodManager.SHOW_FORCED)
                }
            }, delayTime)
        } else {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edit, InputMethodManager.SHOW_FORCED)
        }
    }

    /**
     * 隐藏软键盘
     */
    protected fun hideInputMethod(){
        val imm = context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    companion object {

        /**
         * 设置大小
         * @param v 视图元素
         * @param w 宽度
         * @param h  高度
         */
        fun setSize(v: View, w: Int, h: Int) {
            var params: ViewGroup.LayoutParams? = v.layoutParams
            if (params == null)
                params = ViewGroup.LayoutParams(w, h)
            else {
                if (w >= 0)
                    params.width = w
                if (h >= 0)
                    params.height = h
            }
            v.layoutParams = params
        }

        /**
         * 获取控件大小，无论是否画出来
         * @param v
         * @return
         */
        fun getSize(v: View): Size {
            val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(w, h)
            return Size(v.measuredWidth, v.measuredHeight)
        }
    }

    /**
     * 根据名字获取ID值
     */
    fun getResourceId(id: String,type:String): Int {
        return resources.getIdentifier(id, type, this.context.packageName)
    }


    fun findDrawable(id:Int): Drawable?{
        return ResourcesCompat.getDrawable(resources,id,null)
    }

    fun findTypeface(id:Int): Typeface?{
        return ResourcesCompat.getFont(context,id)
    }

    fun findColor(id:Int):Int?{
        return ResourcesCompat.getColor(resources,id,null)
    }
}