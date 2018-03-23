package com.beviswang.ijkmedialib.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.beviswang.ijkmedialib.util.ConvertHelper

/**
 * Created by shize on 2018/3/22.
 */
class ImageItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0)
    : FrameLayout(context, attrs, def) {
    private var mLayout: ViewGroup? = null // 布局
    private var mImageView: ImageView? = null // ImageView

    private var co = Color.TRANSPARENT
    private var borderwidth = ConvertHelper.dipToPx(getContext(), 5f).toInt()
    private var mAnimatorSetZoomIn: AnimatorSet? = null
    private var mAnimatorSetZoomOut: AnimatorSet? = null

    private val paint = Paint()
    private var rect = Rect()

    private var mCallback: OnAnimCallback? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        // TODO 记得布局第一个为 ImageView
        mImageView = mLayout?.getChildAt(0) as ImageView?
        mImageView?.scaleType = ImageView.ScaleType.FIT_CENTER
        mImageView?.setPadding(5, 5, 5, 5)
        isFocusable = true
        isFocusableInTouchMode = true
    }

    // 设置布局
    fun setLayout(view: View?) {
        mLayout = view as ViewGroup?
        addView(mLayout)
    }

    // 设置布局 id
    fun setLayoutResourse(res: Int) {
        setLayout(LayoutInflater.from(context).inflate(res, null) as ViewGroup?)
    }

    // 获取布局
    fun getLayout():ViewGroup?{
        return mLayout
    }

    // 设置回调
    fun setCallback(callback: OnAnimCallback) {
        mCallback = callback
    }

    //设置颜色
    fun setColour(color: Int) {
        co = color
    }

    //设置边框宽度
    fun setBorderWidth(width: Int) {
        borderwidth = width
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 画边框
        canvas?.getClipBounds(rect)
        // Rect rec = canvas.getClipBounds();
        rect.bottom--
        rect.right--
        //设置边框颜色
        paint.color = co
        paint.style = Paint.Style.STROKE
        //设置边框宽度
        paint.strokeWidth = borderwidth.toFloat()
        canvas?.drawRect(rect, paint)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (gainFocus) {
            zoomOut()
            co = Color.BLUE
            bringToFront()
        } else {
            zoomIn()
            co = Color.TRANSPARENT
        }
    }

    private fun zoomIn() {
        //缩小动画
        if (mAnimatorSetZoomIn == null) {
            mAnimatorSetZoomIn = AnimatorSet()
            val animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1.2f, 1.0f)
            val animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1.2f, 1.0f)
            animatorX.duration = 300
            animatorY.duration = 300
            mAnimatorSetZoomIn?.playTogether(animatorX, animatorY)
        }
        mAnimatorSetZoomIn?.start()
        mCallback?.onNarrow()
    }

    private fun zoomOut() {
        //放大动画
        if (mAnimatorSetZoomOut == null) {
            mAnimatorSetZoomOut = AnimatorSet()
            val animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.2f)
            val animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.2f)
            animatorX.duration = 300
            animatorY.duration = 300
            mAnimatorSetZoomOut?.playTogether(animatorX, animatorY)
        }
        mAnimatorSetZoomOut?.start()
        mCallback?.onEnlarge()
    }

    /**
     * 放大缩小回调
     */
    interface OnAnimCallback {
        /** 放大 */
        fun onEnlarge()

        /** 缩小 */
        fun onNarrow()
    }
}