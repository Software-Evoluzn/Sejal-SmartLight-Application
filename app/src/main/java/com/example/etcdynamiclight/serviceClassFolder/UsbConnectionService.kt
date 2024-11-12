package com.example.etcdynamiclight.serviceClassFolder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.etcdynamiclight.deviceConnectionclass.USBHandler

class UsbConnectionService:Service() {

    lateinit var mUsbHandler: USBHandler

    override fun onCreate() {
        super.onCreate()
         mUsbHandler= USBHandler(applicationContext)
        mUsbHandler.startUsbConnection()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        mUsbHandler.unRegisterReceiver()
        mUsbHandler.disconnect()
        super.onDestroy()
    }
}