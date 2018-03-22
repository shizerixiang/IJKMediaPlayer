package com.beviswang.ijkmedialib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.*
import com.beviswang.ijkmedialib.R

/**
 * IjkMediaPlayer 播放器控件
 * Created by shize on 2018/3/21.
 */
class IJKVideoPlayer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0)
    : FrameLayout(context, attrs, def), IVideoPlayer{
    private var isSlidingVolume: Boolean // 是否开启滑动控制音量
    private var isSlidingBrightness: Boolean // 是否开启滑动控制亮度
    private var isSlidingProgress: Boolean // 是否开启滑动控制进度
    private var isVisibilityController: Boolean // 是否控制器可见(播放、暂停、下一个、上一个、可拖动进度条及进度时间显示、返回、标题、设置菜单、全屏)

    private var ijkVideoView: SurfaceView? = null // 播放器
    private var ijkBackBtn: ImageButton? = null // 返回按钮
    private var ijkTitleTv: TextView? = null // 视频标题
    private var ijkMenuBtn: ImageButton? = null // 设置菜单按钮
    private var ijkPlayBtn: ImageButton? = null // 播放暂停按钮
    private var ijkProgressBar: ProgressBar? = null // 播放进度条
    private var ijkCurTimeTv: TextView? = null // 当前播放进度时间
    private var ijkTotalTimeTv: TextView? = null // 播放总进度时间
    private var ijkFullBtn: ImageButton? = null // 全屏播放按钮

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.IJKVideoView, def, 0)
        isSlidingVolume = a.getBoolean(R.styleable.IJKVideoView_slidingVolume, true)
        isSlidingBrightness = a.getBoolean(R.styleable.IJKVideoView_slidingBrightness, true)
        isSlidingProgress = a.getBoolean(R.styleable.IJKVideoView_slidingProgress, true)
        isVisibilityController = a.getBoolean(R.styleable.IJKVideoView_controllerVisibility, true)
        a.recycle()
        LayoutInflater.from(context).inflate(R.layout.view_ijk_video, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ijkVideoView = findViewById(R.id.ijkSurfaceView)
        ijkBackBtn = findViewById(R.id.ijkBack)
        ijkTitleTv = findViewById(R.id.ijkTitle)
        ijkMenuBtn = findViewById(R.id.ijkMenu)
        ijkPlayBtn = findViewById(R.id.ijkPlayOrPause)
        ijkProgressBar = findViewById(R.id.ijkProgress)
        ijkCurTimeTv = findViewById(R.id.ijkCurTime)
        ijkTotalTimeTv = findViewById(R.id.ijkTotalTime)
        ijkFullBtn = findViewById(R.id.ijkFullScreen)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekForward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekBackward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}