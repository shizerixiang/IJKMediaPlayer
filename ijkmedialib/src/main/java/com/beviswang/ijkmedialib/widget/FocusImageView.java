package com.beviswang.ijkmedialib.widget;

/**
 * Created by Administrator on 2018/3/21.
 */
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.beviswang.ijkmedialib.util.ConvertHelper;

public class FocusImageView extends android.support.v7.widget.AppCompatImageView {

    private int co = Color.TRANSPARENT;
    private int borderwidth = (int) ConvertHelper.INSTANCE.dipToPx(getContext(), 5f);
    private AnimatorSet mAnimatorSetZoomIn;
    private AnimatorSet mAnimatorSetZoomOut;

    public FocusImageView(Context context) {
        super(context);
        initView();
    }

    public FocusImageView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public FocusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    void initView() {
        setScaleType(ScaleType.FIT_CENTER);
        setPadding(5, 5, 5, 5);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    //设置颜色
    public void setColour(int color) {
        co = color;
    }

    //设置边框宽度
    public void setBorderWidth(int width) {
        borderwidth = width;
    }

    private Paint paint = new Paint();
    Rect rect = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画边框
        canvas.getClipBounds(rect);
        // Rect rec = canvas.getClipBounds();
        rect.bottom--;
        rect.right--;
        //设置边框颜色
        paint.setColor(co);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(borderwidth);
        canvas.drawRect(rect, paint);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            zoomOut();
            co = Color.BLUE;
            bringToFront();
        } else {
            zoomIn();
            co = Color.TRANSPARENT;
        }
    }

    private void zoomIn() {
        //缩小动画
        if (mAnimatorSetZoomIn == null) {
            mAnimatorSetZoomIn = new AnimatorSet();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1.2f, 1.0f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1.2f, 1.0f);
            animatorX.setDuration(300);
            animatorY.setDuration(300);
            mAnimatorSetZoomIn.playTogether(animatorX, animatorY);
        }
        mAnimatorSetZoomIn.start();
    }

    private void zoomOut() {
        //放大动画
        if (mAnimatorSetZoomOut == null) {
            mAnimatorSetZoomOut = new AnimatorSet();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.2f);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.2f);
            animatorX.setDuration(300);
            animatorY.setDuration(300);
            mAnimatorSetZoomOut.playTogether(animatorX, animatorY);
        }
        mAnimatorSetZoomOut.start();
    }
}