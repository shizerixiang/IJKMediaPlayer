package com.beviswang.ijkmedialib.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MiniWindowService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
}
