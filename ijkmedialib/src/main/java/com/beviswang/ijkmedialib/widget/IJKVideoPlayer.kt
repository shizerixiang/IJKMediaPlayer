package com.beviswang.ijkmedialib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.*
import com.beviswang.ijkmedialib.R
import com.beviswang.ijkmedialib.media.IjkVideoView
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * IjkMediaPlayer 播放器控件
 * Created by shize on 2018/3/21.
 */
class IJKVideoPlayer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0)
    : FrameLayout(context, attrs, def), IVideoPlayer {
    private var isSlidingVolume: Boolean // 是否开启滑动控制音量
    private var isSlidingBrightness: Boolean // 是否开启滑动控制亮度
    private var isSlidingProgress: Boolean // 是否开启滑动控制进度
    private var isVisibilityController: Boolean // 是否控制器可见(播放、暂停、下一个、上一个、可拖动进度条及进度时间显示、返回、标题、设置菜单、全屏)

    private var ijkListener: PlayerClickListener? = null // 播放状态监听器

    private var ijkVideoView: IjkVideoView? = null // 播放器
    private var ijkBackBtn: ImageButton? = null // 返回按钮
    private var ijkTitleTv: TextView? = null // 视频标题
    private var ijkMenuBtn: ImageButton? = null // 设置菜单按钮
    private var ijkPlayBtn: ImageButton? = null // 播放暂停按钮
    private var ijkProgressBar: ProgressBar? = null // 播放进度条
    private var ijkCurTimeTv: TextView? = null // 当前播放进度时间
    private var ijkTotalTimeTv: TextView? = null // 播放总进度时间
    private var ijkFullBtn: ImageButton? = null // 全屏播放按钮

    init {
        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        val a = context.obtainStyledAttributes(attrs, R.styleable.IJKVideoPlayer, def, 0)
        isSlidingVolume = a.getBoolean(R.styleable.IJKVideoPlayer_slidingVolume, true)
        isSlidingBrightness = a.getBoolean(R.styleable.IJKVideoPlayer_slidingBrightness, true)
        isSlidingProgress = a.getBoolean(R.styleable.IJKVideoPlayer_slidingProgress, true)
        isVisibilityController = a.getBoolean(R.styleable.IJKVideoPlayer_controllerVisibility, true)
        a.recycle()
        LayoutInflater.from(context).inflate(R.layout.view_ijk_video, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // 播放器
        ijkVideoView = findViewById(R.id.ijkVideoView)

        // 返回按钮
        ijkBackBtn = findViewById(R.id.ijkBack)
        ijkBackBtn?.setOnClickListener { ijkListener?.onBackClick() }

        // 标题
        ijkTitleTv = findViewById(R.id.ijkTitle)

        // 菜单按钮
        ijkMenuBtn = findViewById(R.id.ijkMenu)
        ijkMenuBtn?.setOnClickListener { ijkListener?.onMenuClick() }

        // 播放按钮
        ijkPlayBtn = findViewById(R.id.ijkPlayOrPause)
        ijkPlayBtn?.setOnClickListener {
            if (isPlaying) pause() else start()
            ijkListener?.onPlayClick()
        }

        // 进度条
        ijkProgressBar = findViewById(R.id.ijkProgress)
        ijkProgressBar?.max = duration
        ijkProgressBar?.progress = 0

        // 当前时间
        ijkCurTimeTv = findViewById(R.id.ijkCurTime)

        // 总时间
        ijkTotalTimeTv = findViewById(R.id.ijkTotalTime)
        ijkTotalTimeTv?.text

        // 全屏按钮
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

    override fun setVideoTitle(title: String) {
        ijkTitleTv?.text = title
    }

    override fun setPlayerClickListener(listener: PlayerClickListener) {
        ijkListener = listener
    }

    override fun setDataSource(path: String) {
        ijkVideoView?.setVideoPath(path)
    }

    override fun setDataSource(uri: Uri) {
        ijkVideoView?.setVideoURI(uri)
    }

    override fun start() {
        ijkVideoView?.start()
    }

    override fun pause() {
        ijkVideoView?.pause()
    }

    override fun seekTo(pos: Int) {
        ijkVideoView?.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return ijkVideoView?.isPlaying ?: false
    }

    override fun canPause(): Boolean {
        return ijkVideoView?.canPause() ?: false
    }

    override fun canSeekForward(): Boolean {
        return ijkVideoView?.canSeekForward() ?: false
    }

    override fun canSeekBackward(): Boolean {
        return ijkVideoView?.canSeekBackward() ?: false
    }

    override fun getDuration(): Int {
        return ijkVideoView?.duration ?: 0
    }

    override fun getCurrentPosition(): Int {
        return ijkVideoView?.currentPosition ?: 0
    }

    override fun getBufferPercentage(): Int {
        return ijkVideoView?.bufferPercentage ?: 0
    }

    override fun getAudioSessionId(): Int {
        return ijkVideoView?.audioSessionId ?: 0
    }

    interface PlayerClickListener {
        /**
         * 返回按钮点击事件
         */
        fun onBackClick()

        /**
         * 设置菜单按钮点击事件
         */
        fun onMenuClick()

        /**
         * 点击开始按钮事件
         */
        fun onPlayClick()
    }
}