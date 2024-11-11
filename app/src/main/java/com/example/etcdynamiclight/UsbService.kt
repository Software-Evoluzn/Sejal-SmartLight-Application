package com.example.etcdynamiclight

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.logging.Handler

class UsbService : Service() {

   private lateinit var mUSBHandler:USBHandler

//   companion object{
//     const  val CHANNEL_ID="usb_service_channel"
//      const val NOTIFICATION_ID=2
//   }


    override fun onCreate() {
        super.onCreate()


            mUSBHandler= USBHandler(this)



        //createNotificationChannel()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action=="SEND_DATA") {
            println("send data")
            val message = intent.getStringExtra("message")
            println("messege")
            if (message != null) {
                println("messege is not null")
                sendDataToUsbHandler(message)
            }
        }

//        playAlarmSound()
//
//        //show the notification to run the service in the forground
//        startForeground(NOTIFICATION_ID,createNotification())


        //stopSelf()

        return START_STICKY
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotification():Notification{
//        val builder=Notification.Builder(this,CHANNEL_ID)
//            .setSmallIcon(R.drawable.final_logo)
//            .setContentTitle("Evoluzn")
//            .setContentText("Smart Light On")
//            .setPriority(Notification.PRIORITY_LOW)
//            .setOngoing(true)  //keep the notification ongoing while service is running
//
//        return builder.build()
//
//    }

//    fun createNotificationChannel(){
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            val name="USB Communication service"
//            val descriptionText="Show USB communication progress"
//            val importance=NotificationManager.IMPORTANCE_LOW
//            val channel=NotificationChannel(CHANNEL_ID,name,importance).apply {
//                description=descriptionText
//            }
//
//            //register the channel with the system
//            val notificationManager:NotificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    fun playAlarmSound(){
//        val alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//        var ringtone:Ringtone?=RingtoneManager.getRingtone(this,alarmSound)
//
//        //fallback to notification sound if alarm sound is unavailable
//        if(ringtone==null) {
//            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            ringtone = RingtoneManager.getRingtone(this, notificationSound)
//        }
//
//        ringtone?.play()
//    }
//
//

    override fun onBind(intent: Intent?): IBinder? {
        return null

    }

    fun sendDataToUsbHandler(messege:String){
        println("messege is send to UsbHandler class ")
        mUSBHandler.sendData(messege)

    }

    override fun onDestroy() {
        super.onDestroy()
        mUSBHandler.unRegisterReceiver()
    }
}