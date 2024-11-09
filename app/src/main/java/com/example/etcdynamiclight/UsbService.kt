package com.example.etcdynamiclight

import android.app.Service
import android.content.Intent
import android.os.IBinder

class UsbService : Service() {

   private lateinit var mUSBHandler:USBHandler


    override fun onCreate() {
        super.onCreate()
        mUSBHandler= USBHandler(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message=intent?.getStringExtra("messege")
        if (message != null) {
            mUSBHandler.sendData(message)
        }
        stopSelf()

        return START_NOT_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null

    }

    override fun onDestroy() {
        super.onDestroy()
        mUSBHandler.unRegisterReceiver()
    }
}