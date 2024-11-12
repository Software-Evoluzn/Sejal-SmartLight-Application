package com.example.etcdynamiclight.ReceiverClass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.etcdynamiclight.serviceClassFolder.UsbService

class AlarmReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
       val action=intent?.action
        val messege=if(action=="ON_ALARM")
            "T:5:G:G:1"  //Start the device
        else
            "T:5:G:G:0"  //stop the device


        val serviceIntent=Intent(context, UsbService::class.java)
        serviceIntent.action="SEND_DATA"
        serviceIntent.putExtra("message",messege)
        context?.startService(serviceIntent)

        val uiUpdateIntent=Intent("com.example.update_master_switch")
        uiUpdateIntent.putExtra("status",action=="ON_ALARM")
        context?.sendBroadcast(uiUpdateIntent)
    }
}