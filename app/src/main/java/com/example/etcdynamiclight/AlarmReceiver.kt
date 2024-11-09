package com.example.etcdynamiclight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
       val action=intent?.action
        val messege=if(action=="ON_ALARM")
            "T:5:G:G:1"  //Start the device
        else
            "T:5:G:G:0"  //stop the device

        val serviceIntent=Intent(context,UsbService::class.java)
        serviceIntent.putExtra("messege",messege)
        context?.startService(serviceIntent)



    }
}