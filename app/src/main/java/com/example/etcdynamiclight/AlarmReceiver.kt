package com.example.etcdynamiclight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       val msg= intent?.getStringExtra("messege")?:"Alarm Triggeres"
        println(" alarm triggerd : $msg")
    }
}