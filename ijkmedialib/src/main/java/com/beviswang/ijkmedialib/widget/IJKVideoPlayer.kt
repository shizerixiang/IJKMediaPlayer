package com.beviswang.ijkmedialib.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.beviswang.ijkmedialib.R
import com.beviswang.ijkmedialib.media.IjkVideoView
import com.beviswang.ijkmedialib.util.ConvertHelper
import com.beviswang.ijkmedialib.view.SlidingGestureDetector
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.lang.ref.WeakReference

/**
 * IjkMediaPlayer 播放器控件，基于 ijkPlayer 开源项目
 *
 * 该类提供了可以在 XML 布局中使用 <IJKVideoPlayer /> 标签的功能，以下是使用实例：
 * <com.beviswang.ijkmedialib.widget.IJKVideoPlayer
 * android:id="@+id/IJK_video_player"
 * android:background="#e333"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content" />
 *
 * 额外提供了参数设置及基本的控制器，但并不是全部功能，如果需要更详细的设置请关闭控制器，并自定义控制器：
 * <!-- 滑动控制音量 (右边上下) 默认：true -->
 * <attr name="slidingVolume" format="boolean" />
 * <!-- 滑动控制亮度 (左边上下) 默认：true -->
 * <attr name="slidingBrightness" format="boolean" />
 * <!-- 滑动控制进度 (左右) 默认：true -->
 * <attr name="slidingProgress" format="boolean" />
 * <!-- 默认控制器是否可见 (播放、暂停、下一个、上一个、可拖动进度条及进度时间显示、返回、标题、设置菜单、全屏) 默认：true -->
 * <attr name="controllerVisibility" format="boolean" />
 *
 * 注意：使用该控件需要声明权限
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <!-- 网络权限非必须，但如果需要用到网络请声明 -->
 * <uses-permission android:name="android.permission.INTERNET" />
 *
 * Created by shize on 2018/3/21.
 */
class IJKVideoPlayer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0)
    : FrameLayout(context, attrs, def), IVideoPlayer, SeekBar.OnSeekBarChangeListener, SlidingGestureDetector.OnGestureListener {
    private var mPlayerHandler: PlayerHandler = PlayerHandler(this)                          // 进度更新Handler
    private var ijkListener: ControllerCallback? = null                                                     // 播放状态监听器
    private var ijkGestureDetector: SlidingGestureDetector? = null                                          // 手势监听器
    private var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager// 音量管理
    private val activity = context as Activity                                                              // activity

    private val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    private var curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    private var isSlidingVolume: Boolean                            // 是否开启滑动控制音量
    private var isSlidingBrightness: Boolean                        // 是否开启滑动控制亮度
    private var isSlidingProgress: Boolean                          // 是否开启滑动控制进度
    private var isVisibilityController: Boolean                     // 是否控制器可见(播放、暂停、下一个、上一个、可拖动进度条及进度时间显示、返回、标题、设置菜单、全屏)

    private var ijkVideoView: IjkVideoView? = null                  // 播放器
    private var ijkTitleBar: ViewGroup? = null                      // 标题栏
    private var ijkControlBar: ViewGroup? = null                    // 控制条
    private var ijkBackBtn: ImageButton? = null                     // 返回按钮
    private var ijkTitleTv: TextView? = null                        // 视频标题
    private var ijkMenuBtn: ImageButton? = null                     // 设置菜单按钮
    private var ijkPlayBtn: ImageButton? = null                     // 播放暂停按钮
    private var ijkProgressBar: SeekBar? = null                     // 播放进度条
    private var ijkCurTimeTv: TextView? = null                      // 当前播放进度时间
    private var ijkTotalTimeTv: TextView? = null                    // 播放总进度时间
    private var ijkFullBtn: ImageButton? = null                     // 全屏播放按钮
    private var ijkVolumeBar: RelativeLayout? = null                // 音量工具条
    private var ijkVolumeProgress: ProgressBar? = null              // 音量进度条
    private var ijkBrightnessBar: RelativeLayout? = null            // 亮度工具条
    private var ijkBrightnessProgress: ProgressBar? = null          // 亮度进度条

    private var seekProgress: Int = -1                              // 手指滑动的进度
    private var isTouchProgress: Boolean = false                    // 用户是否正在拖动进度条
    private var isShowController: Boolean = false                   // 播放器的控制器是否为显示状态
    private var isShowVolumeBar: Boolean = false                    // 是否显示音量工具条
    private var isShowBrightnessBar: Boolean = false                // 是否显示亮度工具条
    private var isFullScreen: Boolean = false                       // 是否全屏

    var autoHideDuration: Long = 5000L                              // 自动隐藏控制器间隔

    private var slideDistance: Float = 0f                           // 当前滑动的进度
    private var startPosition: Float = 0f                           // 起始位置
    private var slideDirection = SlidingGestureDetector.HORIZONTAL  // 滑动方向

    private var recodeDistance: Float = 0f                          // 记录的距离，在没有达到触发值之前的记录值，触发的同时更新值

    private var recodeHeight: Int = 0                               // 记录全屏前的高度
    private var recodeWidth: Int = 0                                // 记录全屏前的宽度

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
        ijkGestureDetector = SlidingGestureDetector(context, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initController()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 检测用户是否选择开启滑动调整参数
        if (!isSlidingBrightness && !isSlidingVolume && !isSlidingProgress) return super.onTouchEvent(event)
        return ijkGestureDetector?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    override fun onHorizontalSlide(distance: Float, hx: Float) {
        if (!isSlidingProgress) return
        slideDirection = SlidingGestureDetector.HORIZONTAL
        slideDistance = distance
        startPosition = hx
    }

    override fun onVerticalSlide(distance: Float, vx: Float) {
        if (!isSlidingBrightness && !isSlidingVolume) return
        slideDirection = SlidingGestureDetector.VERTICAL
        // 判断为第一次滑动，记录起始点Y轴坐标
        val moveDistance = distance - recodeDistance // 移动的距离
        if (vx > width / 2) {
            if (!isSlidingVolume) return
            setVolume(moveDistance, distance)
        } else {
            if (!isSlidingBrightness) return
            setBrightness(moveDistance, distance)
        }
    }

    override fun onRelease(e: MotionEvent?) {
        if (isSlidingVolume && isShowVolumeBar) {
            isShowVolumeBar = false
            refreshVolumeBar()
        }
        if (isSlidingBrightness && isShowBrightnessBar) {
            isShowBrightnessBar = false
            refreshBrightnessBar()
        }
        if (slideDistance == 0f && startPosition == 0f) return
        if (slideDirection == SlidingGestureDetector.HORIZONTAL) updateHorizontal()
        slideDistance = 0f
        startPosition = 0f
        recodeDistance = 0f
    }

    /**
     * 设置亮度
     * @param moveDistance 距上次记录移动的距离
     * @param distance 当前移动的距离
     */
    private fun setBrightness(moveDistance: Float, distance: Float) {
        if (Math.abs(moveDistance) > height / 51f) {
            if (!isShowBrightnessBar) {
                isShowBrightnessBar = true
                refreshBrightnessBar()
            }
            if (moveDistance > 0) addBrightness() else subBrightness()
            recodeDistance = distance
        }
    }

    /** 增加亮度 */
    private fun addBrightness() {
        var curBrightness = activity.window.attributes.screenBrightness * 255f
        curBrightness += 5
        if (curBrightness > 255) curBrightness = 255f
        // 调整音量
        updateBrightnessProgress(curBrightness)
    }

    /** 减小亮度 */
    private fun subBrightness() {
        var curBrightness = activity.window.attributes.screenBrightness * 255f
        curBrightness -= 5
        if (curBrightness < 0) curBrightness = 0f
        // 调整音量
        updateBrightnessProgress(curBrightness)
    }

    /**
     * 设置音量
     * @param moveDistance 距上次记录移动的距离
     * @param distance 当前移动的距离
     */
    private fun setVolume(moveDistance: Float, distance: Float) {
        if (Math.abs(moveDistance) > height / 15f) {
            if (!isShowVolumeBar) {
                isShowVolumeBar = true
                refreshVolumeBar()
            }
            if (moveDistance > 0) addVolume() else subVolume()
            recodeDistance = distance
        }
    }

    /** 增加音量 */
    private fun addVolume() {
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        curVolume += maxVolume / 15
        if (curVolume > maxVolume) curVolume = maxVolume
        // 调整音量
        updateVolumeProgress(curVolume)
    }

    /** 减小音量 */
    private fun subVolume() {
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        curVolume -= maxVolume / 15
        if (curVolume < 0) curVolume = 0
        // 调整音量
        updateVolumeProgress(curVolume)
    }

    /** 水平更新值 */
    private fun updateHorizontal() {
        var slideRatio = 0 - (slideDistance / width)
        if (startPosition - slideDistance < 0) slideRatio = 0f
        if (startPosition - slideDistance > width) slideRatio = 1f
        val slideCurPos = (duration * slideRatio + currentPosition).toInt()
        seekTo(slideCurPos)
        updateProgress(slideCurPos, duration)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        if (isShowController) hideController() else showController()
        return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        onPlayButtonClick(ijkPlayBtn)
        return false
    }

    // ********************************** 控制器参数 **********************************

    /** 初始化控制器 */
    private fun initController() {
        ijkVideoView = findViewById(R.id.ijkVideoView)                  // 播放器
        ijkTitleBar = findViewById(R.id.ijkTitleBar)                    // 标题栏
        ijkControlBar = findViewById(R.id.ijkControlBar)                // 控制栏
        ijkTitleTv = findViewById(R.id.ijkTitle)                        // 标题
        ijkCurTimeTv = findViewById(R.id.ijkCurTime)                    // 当前时间
        ijkTotalTimeTv = findViewById(R.id.ijkTotalTime)                // 总时间
        ijkProgressBar = findViewById(R.id.ijkProgress)                 // 进度条
        ijkProgressBar?.setOnSeekBarChangeListener(this)
        mPlayerHandler.sendEmptyMessageDelayed(WHAT_PROGRESS_UPDATE, 1000)
        ijkBackBtn = findViewById(R.id.ijkBack)                         // 返回按钮
        ijkBackBtn?.setOnClickListener { v ->
            handlerOperation(v, doSth = { view ->
                if (isFullScreen) {
                    cancelFullScreen()
                    return@handlerOperation
                }
                ijkListener?.onBackClick(view!!)
            })
        }
        ijkMenuBtn = findViewById(R.id.ijkMenu)                         // 菜单按钮
        ijkMenuBtn?.setOnClickListener { v -> handlerOperation(v, doSth = { view -> ijkListener?.onMenuClick(view!!) }) }
        ijkFullBtn = findViewById(R.id.ijkFullScreen)                   // 全屏按钮
        ijkFullBtn?.setOnClickListener { v ->
            handlerOperation(v, doSth = { view ->
                ijkListener?.onFullClick(view!!)
                setFullScreen()
            })
        }
        ijkPlayBtn = findViewById(R.id.ijkPlayOrPause)                  // 播放按钮
        ijkPlayBtn?.setOnClickListener { v ->
            onPlayButtonClick(v)
        }
        ijkVolumeBar = findViewById(R.id.ijkVolumeBar)                  // 音量工具条
        ijkVolumeProgress = findViewById(R.id.ijkVolumeProgress)        // 音量进度条
        ijkVolumeProgress?.max = 15
        refreshVolumeBar()                                              // 更新是否显示音量工具条
        ijkBrightnessBar = findViewById(R.id.ijkBrightnessBar)          // 亮度工具条
        ijkBrightnessProgress = findViewById(R.id.ijkBrightnessProgress)// 亮度进度条
        ijkBrightnessProgress?.max = 255
        refreshBrightnessBar()                                          // 更新是否显示亮度工具条
        if (isVisibilityController) showController()                    // 判断是否显示控制器
    }

    /**
     * 播放按钮点击事件
     * @param v 按钮控件
     */
    private fun onPlayButtonClick(v: View?) {
        handlerOperation(v, doSth = { view ->
            if (isPlaying) {
                pause()
                (view as ImageButton).setImageResource(R.drawable.ic_play)
            } else {
                start()
                (view as ImageButton).setImageResource(R.drawable.ic_pause)
            }
            ijkListener?.onPlayClick(view, isPlaying)
        })
    }

    /** 设置全屏 */
    private fun fullScreen() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        recodeHeight = height
        recodeWidth = width
        val lp = layoutParams
        lp.height = LayoutParams.MATCH_PARENT
        lp.width = LayoutParams.MATCH_PARENT
        layoutParams = lp
        isFullScreen = true
    }

    /** 取消全屏 */
    private fun cancelFullScreen() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity.window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val lp = layoutParams
        lp.height = recodeHeight
        lp.width = recodeWidth
        layoutParams = lp
        isFullScreen = false
    }

    /**
     * 处理控制器的操作
     *
     * @param view 控制器的 view
     * @param doSth 需要执行的方法
     */
    private fun handlerOperation(view: View?, doSth: (v: View?) -> Unit) {
        if (mPlayerHandler.hasMessages(WHAT_AUTO_HIDE_CONTROLLER))
            mPlayerHandler.removeMessages(WHAT_AUTO_HIDE_CONTROLLER)
        doSth(view)
        mPlayerHandler.sendEmptyMessageDelayed(WHAT_AUTO_HIDE_CONTROLLER, autoHideDuration)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        seekProgress = progress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isTouchProgress = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekProgress == -1) return
        ijkVideoView?.seekTo(seekProgress)
        seekBar?.progress = seekProgress
        seekProgress = -1
        isTouchProgress = false
    }

    /** 显示控制器 */
    private fun showController() {
        if (isShowController) return
        isShowController = true
        ijkTitleBar?.visibility = View.VISIBLE
        ijkControlBar?.visibility = View.VISIBLE
        if (mPlayerHandler.hasMessages(WHAT_AUTO_HIDE_CONTROLLER))
            mPlayerHandler.removeMessages(WHAT_AUTO_HIDE_CONTROLLER)
        mPlayerHandler.sendEmptyMessageDelayed(WHAT_AUTO_HIDE_CONTROLLER, autoHideDuration)
    }

    /** 隐藏控制器 */
    private fun hideController() {
        if (!isShowController) return
        isShowController = false
        ijkTitleBar?.visibility = View.GONE
        ijkControlBar?.visibility = View.GONE
    }

    /** 更新音量进度条 */
    private fun updateBrightnessProgress(value: Float) {
        ijkBrightnessProgress?.progress = value.toInt()
        val lp = activity.window.attributes
        lp.screenBrightness = value / 255f
        activity.window.attributes = lp
    }

    /** 更新音量进度条 */
    private fun updateVolumeProgress(value: Int) {
        ijkVolumeProgress?.progress = value
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_PLAY_SOUND)
    }

    /** 更新是否显示亮度工具条 */
    private fun refreshBrightnessBar() {
        if (isShowBrightnessBar) ijkBrightnessBar?.visibility = View.VISIBLE
        else ijkBrightnessBar?.visibility = View.GONE
    }

    /** 更新是否显示音量工具条 */
    private fun refreshVolumeBar() {
        if (isShowVolumeBar) ijkVolumeBar?.visibility = View.VISIBLE
        else ijkVolumeBar?.visibility = View.GONE
    }

    /**
     * 更新当前进度及总进度
     *
     * @param cur 当前进度
     * @param total 总进度
     */
    private fun updateProgress(cur: Int, total: Int) {
        var curTime = cur
        if (cur > total) curTime = total
        if (cur < 0) curTime = 0
        ijkCurTimeTv?.text = ConvertHelper.getDisplayTimeByMsec(curTime.toLong())
        ijkTotalTimeTv?.text = ConvertHelper.getDisplayTimeByMsec(total.toLong())
        // 加载缓冲条
        ijkProgressBar?.secondaryProgress = ((ijkVideoView?.bufferPercentage ?: 0) * duration) / 100
        if (isTouchProgress) return
        ijkProgressBar?.progress = curTime
        ijkProgressBar?.max = total
    }

    override fun setVideoTitle(title: String) {
        ijkTitleTv?.text = title
    }

    override fun setControllerCallback(listener: ControllerCallback) {
        ijkListener = listener
    }

    // ********************************** 播放器参数 **********************************

    /** 设置全屏状态 */
    override fun setFullScreen() = if (!isFullScreen) fullScreen() else cancelFullScreen()

    override fun showMediaInfo() {
        ijkVideoView?.showMediaInfo()
    }

    override fun resume() {
        ijkVideoView?.resume()
    }

    override fun stopPlayback() {
        ijkVideoView?.stopPlayback()
    }

    override fun release(boolean: Boolean) {
        ijkVideoView?.release(boolean)
    }

    override fun stopBackgroundPlay() {
        ijkVideoView?.stopBackgroundPlay()
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

    /** 控制器点击事件回调接口 */
    interface ControllerCallback {
        /** 返回按钮点击事件 */
        fun onBackClick(view: View)

        /** 设置菜单按钮点击事件 */
        fun onMenuClick(view: View)

        /**
         * 点击开始按钮事件
         * 该方法在当前点击事件结束后调用，故：
         * isPlaying 参数为当前点击后的状态
         * @param isPlaying 是否在播放
         */
        fun onPlayClick(view: View, isPlaying: Boolean)

        /** 全屏按钮点击事件 */
        fun onFullClick(view: View)
    }

    /** 进度条更新 Handler */
    class PlayerHandler(ijkVideoPlayer: IJKVideoPlayer) : Handler() {
        private val weakIjk = WeakReference<IJKVideoPlayer>(ijkVideoPlayer)

        override fun handleMessage(msg: Message?) {
            val ijkVideoPlayer = weakIjk.get() ?: return
            when (msg?.what) {
                WHAT_PROGRESS_UPDATE -> {           // 进度更新
                    val cur = ijkVideoPlayer.currentPosition
                    val total = ijkVideoPlayer.duration
                    // 判断为资源未预加载完，继续循环等待
                    if (total < 1) sendEmptyMessageDelayed(WHAT_PROGRESS_UPDATE, 1000)
                    if (cur > total) return
                    ijkVideoPlayer.updateProgress(cur, total)
                    sendEmptyMessageDelayed(WHAT_PROGRESS_UPDATE, 1000)
                }
                WHAT_AUTO_HIDE_CONTROLLER -> {      // 自动隐藏控制器
                    if (!ijkVideoPlayer.isShowController) return
                    ijkVideoPlayer.hideController()
                }
            }
        }
    }

    companion object {
        val WHAT_PROGRESS_UPDATE = 0x01             // 进度更新
        val WHAT_AUTO_HIDE_CONTROLLER = 0x02        // 自动隐藏控制器
    }
}