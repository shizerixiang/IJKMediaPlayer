package com.beviswang.ijkmedialib.widget

import android.net.Uri
import android.widget.MediaController

/**
 * 视频播放器控件接口
 * Created by shize on 2018/3/22.
 */
interface IVideoPlayer : MediaController.MediaPlayerControl {
    /** 设置全屏状态 */
    fun setFullScreen()

    /** @param listener 设置播放器点击监听器 */
    fun setControllerCallback(listener: IJKVideoPlayer.ControllerCallback)

    /** @param title 设置视频标题 */
    fun setVideoTitle(title: String)

    /** @param path 设置数据源路径 */
    fun setDataSource(path: String)

    /** @param uri 设置数据源地址 */
    fun setDataSource(uri: Uri)

    /** 弹出媒体信息 */
    fun showMediaInfo()

    /** 启动播放 */
    fun resume()

    fun stopPlayback()

    fun release(boolean: Boolean)

    fun stopBackgroundPlay()
}