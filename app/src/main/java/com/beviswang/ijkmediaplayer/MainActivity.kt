package com.beviswang.ijkmediaplayer

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.beviswang.capturelib.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*

import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PermissionHelper.requestPermissions(this, REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET)) initData() else
            Toast.makeText(this, "请设置权限！", Toast.LENGTH_SHORT).show()
        initData()

    }

    /**
     * 初始化数据
     */
    private fun initData() {
        mPlayButton.setOnClickListener {
            startActivity<PlayerActivity>()
        }
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
