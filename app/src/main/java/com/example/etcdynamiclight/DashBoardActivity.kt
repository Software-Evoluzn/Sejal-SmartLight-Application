package com.example.etcdynamiclight

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView

class DashBoardActivity : AppCompatActivity() {


    lateinit var masterSWITCH: SwitchCompat
    lateinit var masterSwitchCardView: CardView
    lateinit var  mUsbHandler:USBHandler
    lateinit var spinnerDay:Spinner
     lateinit var timeAndDatePicker:ShowingDataAndTimePicker
     lateinit var OnTime:String
     lateinit var OffTime:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)
         mUsbHandler = USBHandler(this)
         masterSWITCH = findViewById(R.id.MasterSwitch)
         masterSwitchCardView = findViewById(R.id.cardMasterSwitch)
        spinnerDay=findViewById(R.id.spinnerDay)
        timeAndDatePicker=ShowingDataAndTimePicker(this,supportFragmentManager)

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

        val arrSpinner= arrayOf("Select item","Today","Set Date","Weekly")
        val arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arrSpinner)
        spinnerDay.adapter=arrayAdapter



        spinnerDay.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                  var selectedItem=spinnerDay.getItemAtPosition(position)
                when(selectedItem){
                    "Today"->{
                        val alertDialog=AlertDialog.Builder(this@DashBoardActivity)
                        alertDialog.setTitle("Select Time")
                        alertDialog.setMessage("Select On Time and Off Time for Smart Lights")
                        alertDialog.setIcon(R.drawable.evoluznlogo)
                        alertDialog.setPositiveButton("Yes"){dialogInterface,which->

                            alertDialog.setCancelable(true)
                        }

                        val dialogShowing:AlertDialog= alertDialog.create()
                        dialogShowing.show()

                    }

                    "Set Date"->{
                        Toast.makeText(this@DashBoardActivity,"Set Date",Toast.LENGTH_SHORT).show()
                    }

                    "Weekly"->{
                        Toast.makeText(this@DashBoardActivity,"Weekly",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        val OnTimeButton:AppCompatButton=findViewById(R.id.selectOnTime)
        OnTimeButton.setOnClickListener{
           OnTime= timeAndDatePicker.OpenTimePickerDialog().toString()
            Log.i("On time","$OnTime")

        }

        val OffTimeButton:AppCompatButton=findViewById(R.id.SelectOfTime)
        OffTimeButton.setOnClickListener{
           OffTime= timeAndDatePicker.OpenTimePickerDialog().toString()
            Log.i("Off time","$OffTime")

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mUsbHandler.unRegisterReceiver()
    }
}