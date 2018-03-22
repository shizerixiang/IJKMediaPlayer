package com.beviswang.ijkmediaplayer

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.SurfaceHolder
import android.widget.Toast
import com.beviswang.capturelib.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File

class MainActivity : AppCompatActivity() {
//    private lateinit var mHolder: SurfaceHolder
//    private lateinit var mIjkPlayer: IjkMediaPlayer // ijk 的媒体播放器

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PermissionHelper.requestPermissions(this, REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) initData() else
            Toast.makeText(this, "请设置权限！", Toast.LENGTH_SHORT).show()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
//        mHolder = mSurfaceView.holder
//        mHolder.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder?) {
//
//            }
//
//            override fun surfaceCreated(holder: SurfaceHolder?) {
//                // 连接
//                mIjkPlayer.setDisplay(holder)
//                // 开启异步准备
//                mIjkPlayer.prepareAsync()
//            }
//        })
//        mIjkPlayer = IjkMediaPlayer()
//        try {
////            mIjkPlayer.dataSource = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Movies/0001.mp4"
//            mIjkPlayer.dataSource = "http://pic.ibaotu.com/00/55/67/63Y888piCK2k.mp4"
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        mIjkPlayer.setOnPreparedListener { iMediaPlayer -> iMediaPlayer.start() }
//        mIjkPlayer.setOnCompletionListener { iMediaPlayer ->
//            iMediaPlayer.seekTo(0)
//            iMediaPlayer.start()
//        }
//        mIjkPlayer.setOnBufferingUpdateListener { _, _ -> }
        mPlayButton.setOnClickListener {
            startActivity<Main2Activity>()
//            mIjkPlayer.start()
        }
//        mStopButton.setOnClickListener { mIjkPlayer.pause() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults,
                object : PermissionHelper.OnRequestPermissionsResultCallbacks {
                    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?,
                                                     isAllDenied: Boolean) {
                        finish()
                    }

                    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?,
                                                      isAllGranted: Boolean) {
                        initData()
                    }
                })
    }

    companion object {
        private val REQUEST_CODE = 0x19
    }
}
