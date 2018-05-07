package com.beviswang.ijkmediaplayer

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.beviswang.ijkmedialib.widget.IJKVideoPlayer
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.onClick

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        IJK_video_player.setDataSource(Uri.parse("http://vod.cntv.lxdns.com/flash/mp4video61/TMS/2017/08/17/63bf8bcc706a46b58ee5c821edaee661_h264818000nero_aac32-5.mp4"))
        IJK_video_player.setVideoTitle("动物世界——兽王之路篇")

        mInfoBtn.onClick { IJK_video_player.showMediaInfo() }

        IJK_video_player.setControllerCallback(object :IJKVideoPlayer.ControllerCallback{
            override fun onBackClick(view: View) {
                finish()
            }

            override fun onMenuClick(view: View) {
                IJK_video_player.showMediaInfo()
            }

            override fun onPlayClick(view: View, isPlaying: Boolean) {
            }

            override fun onFullClick(view: View) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        IJK_video_player.resume()
    }

    override fun onStop() {
        super.onStop()
        IJK_video_player.stopPlayback()
        IJK_video_player.release(true)
        IJK_video_player.stopBackgroundPlay()
    }
}