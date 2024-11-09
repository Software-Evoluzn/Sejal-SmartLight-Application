package com.example.etcdynamiclight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver() {
    lateinit var mUSBHandler:USBHandler
    override fun onReceive(context: Context?, intent: Intent?) {
        mUSBHandler= context?.let { USBHandler(it) }!!
       val msg= intent?.getStringExtra("messege")?:"Alarm Triggeres"
        println(" alarm triggerd : $msg")
        if(intent?.action.equals("ON_ALARM")){
            mUSBHandler.sendData("T:5:G:G:1")

        }else{
            mUSBHandler.sendData("T:5:G:G:0")

        }

    }
}