package com.example.etcdynamiclight.activityFolder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.etcdynamiclight.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
         Handler(Looper.getMainLooper()).postDelayed(Runnable {
                val intent= Intent(this@SplashScreenActivity,DashBoardActivity::class.java)
                startActivity(intent)

         },3000)


    }
}



