package com.example.etcdynamiclight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

class AlarmReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
       val action=intent?.action
        val messege=if(action=="ON_ALARM")
            "T:5:G:G:1"  //Start the device
        else
            "T:5:G:G:0"  //stop the device

        val serviceIntent=Intent(context,UsbService::class.java)
        println("serviceIntent class ")
        serviceIntent.action="SEND_DATA"
        println("action send data")
        serviceIntent.putExtra("message",messege)
        println("messege is send")
        context?.startService(serviceIntent)



    }
}