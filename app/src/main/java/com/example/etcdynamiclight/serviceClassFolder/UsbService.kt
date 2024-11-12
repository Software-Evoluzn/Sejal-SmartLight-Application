package com.example.etcdynamiclight.serviceClassFolder


import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.etcdynamiclight.deviceConnectionclass.USBHandler


class UsbService : Service() {
    private lateinit var mUSBHandler: USBHandler

    override fun onCreate() {
        super.onCreate()
        mUSBHandler= USBHandler(applicationContext)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action=="SEND_DATA") {
            val message = intent.getStringExtra("message")
            if (message != null) {
                sendDataToUsbHandler(message)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun sendDataToUsbHandler(message: String) {
        Log.i("UsbService", "Sending message to USB Handler: $message")
        if (mUSBHandler.serial == null) {
            mUSBHandler.startUsbConnection()  // Attempt reconnection if needed
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                mUSBHandler.sendData(message)
            }, 500) // small delay to ensure connection
        } else {
            mUSBHandler.sendData(message)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mUSBHandler.unRegisterReceiver()
    }
}