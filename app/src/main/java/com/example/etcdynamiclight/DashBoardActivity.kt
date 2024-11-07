package com.example.etcdynamiclight

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView

class DashBoardActivity : AppCompatActivity() {


    lateinit var masterSWITCH: SwitchCompat
    lateinit var masterSwitchCardView: CardView
   lateinit var  mUsbHandler:USBHandler
   lateinit var spinnerDay:Spinner

  // lateinit var spinnerDay:Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)
         mUsbHandler = USBHandler(this)
         masterSWITCH = findViewById(R.id.MasterSwitch)
         masterSwitchCardView = findViewById(R.id.cardMasterSwitch)
        spinnerDay=findViewById(R.id.spinnerDay)

        masterSwitchCardView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        masterSWITCH.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mUsbHandler.sendData("T:5:G:G:1")
            } else {
                mUsbHandler.sendData("T:5:G:G:0")
            }
        }

        val arrSpinner= arrayOf("Today","Set Data","Weekly")
        val arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arrSpinner)
        spinnerDay.adapter=arrayAdapter


    }

    override fun onDestroy() {
        super.onDestroy()
        mUsbHandler.unRegisterReceiver()
    }
}