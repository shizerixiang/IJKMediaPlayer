package com.beviswang.ijkmediaplayer

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.onClick
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        ijkVideoView.setVideoURI(Uri.parse("http://vod.cntv.lxdns.com/flash/mp4video61/TMS/2017/08/17/63bf8bcc706a46b58ee5c821edaee661_h264818000nero_aac32-5.mp4"))
        ijkVideoView.start()

        mInfoBtn.onClick { ijkVideoView.showMediaInfo() }
    }

    override fun onResume() {
        super.onResume()
        ijkVideoView.resume()
    }

    override fun onStop() {
        super.onStop()
        ijkVideoView.stopPlayback()
        ijkVideoView.release(true)
        ijkVideoView.stopBackgroundPlay()
    }
}
