package com.example.etcdynamiclight


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class DashBoardActivity : AppCompatActivity() {


    lateinit var masterSWITCH: SwitchCompat
    lateinit var masterSwitchCardView: CardView
    lateinit var  mUsbHandler:USBHandler
    lateinit var mUsbService:UsbService
    lateinit var spinnerDay:Spinner
    lateinit var timeAndDatePicker:ShowingDataAndTimePicker
    lateinit var OnTime:String
    lateinit var OffTime:String
    lateinit var startDate:String
    lateinit var endDate:String
    lateinit var mDbHelpher:DBHelpher
    lateinit var setAlarmFromDatabase:SetAlarmFromDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)
         mUsbHandler = USBHandler(this)
         mDbHelpher=DBHelpher(this,null)
         setAlarmFromDatabase= SetAlarmFromDatabase()
         masterSWITCH = findViewById(R.id.MasterSwitch)
         masterSwitchCardView = findViewById(R.id.cardMasterSwitch)
         spinnerDay=findViewById(R.id.spinnerDay)
         timeAndDatePicker=ShowingDataAndTimePicker(this,supportFragmentManager)
         mUsbService=UsbService()

         masterSwitchCardView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val sharePreference=getSharedPreferences("switchSharePreference", MODE_PRIVATE)
        masterSWITCH.isChecked=sharePreference.getBoolean("status",false)

        val filter=IntentFilter("com.example.update_master_switch")

        val masterSwitchReceiver=object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val status = intent?.getBooleanExtra("status", false) ?: false
                masterSWITCH.isChecked = status
            }

        }
        registerReceiver(masterSwitchReceiver,filter)


        masterSWITCH.setOnCheckedChangeListener { buttonView, isChecked ->
            sharePreference.edit().putBoolean("status",isChecked).apply()

            val serviceIntent=Intent(this,UsbService::class.java)
            serviceIntent.action="SEND_DATA"
            if (isChecked) {
                serviceIntent.putExtra("message","T:5:G:G:1")
            } else {
                serviceIntent.putExtra("message","T:5:G:G:0")
            }
             startService(serviceIntent)

        }

        val arrSpinner= arrayOf("Select item","Today","Set Date")
        val arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arrSpinner)
        spinnerDay.adapter=arrayAdapter



        spinnerDay.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectedItem = spinnerDay.getItemAtPosition(position)
                when (selectedItem) {

                    "Today" -> {
                        val current = LocalDate.now()
                        startDate = current.toString()
                        endDate = current.toString()
                        val setDateRange: TextView = findViewById(R.id.setDateRange)
                        setDateRange.text = startDate + " to " + endDate

                    }

                    "Set Date" -> {
                       val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select Start Date and End Date")
                            .build()

                        // Show the picker
                        dateRangePicker.show(supportFragmentManager, "date_range_picker")

                        // Handle positive button click (when dates are selected)
                        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
                            // Retrieve start and end dates in milliseconds
                            val startDateMillis = datePicked.first
                            val endDateMillis = datePicked.second

                            // Format dates as strings
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            startDate = dateFormat.format(Date(startDateMillis))
                            endDate = dateFormat.format(Date(endDateMillis))

                            // Display the selected date range
                            val setDateRange: TextView = findViewById(R.id.setDateRange)
                            setDateRange.text = "$startDate to $endDate"
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val OnTimeButton:AppCompatButton=findViewById(R.id.selectOnTime)
        OnTimeButton.setOnClickListener{
            timeAndDatePicker.OpenTimePickerDialog { selectTime->
                OnTime=selectTime
                OnTimeButton.setText(OnTime)

            }

        }

        val OffTimeButton:AppCompatButton=findViewById(R.id.SelectOfTime)
        OffTimeButton.setOnClickListener{
            timeAndDatePicker.OpenTimePickerDialog {selectTime->
                OffTime=selectTime
                OffTimeButton.setText(OffTime)


            }

        }

        val scheduleButton:AppCompatButton=findViewById(R.id.ScheduleButton)

        scheduleButton.setOnClickListener{
            if(startDate.isNotEmpty() && endDate.isNotEmpty()) {
                if(OnTime.isNotEmpty() && OffTime.isNotEmpty()) {
                    mDbHelpher.ScheduleRegistration("123", startDate, endDate, OnTime, OffTime, "1")
                }else{
                    Toast.makeText(this@DashBoardActivity,"please select On time and Off time",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@DashBoardActivity,"select today",Toast.LENGTH_SHORT).show()
            }
            val fetchingScheduleData=mDbHelpher.fetchSchedulingData()
            for( i in fetchingScheduleData) {
                println("sch_everyday: ${i.sch_everyDay}")
                println("startdate: ${i.sch_sDate}")
                println("enddate: ${i.sch_eDate}")
                println("startTime: ${i.sch_sTime}")
                println("endTime: ${i.sch_eTime}")
                println("value: ${i.sch_value}")
                println("---------------------------------")

            }

            setAlarmFromDatabase.fetchDataFromDataBase(fetchingScheduleData,this)
            val alertDialog=AlertDialog.Builder(this)
            alertDialog.setIcon(R.drawable.logos1)
            alertDialog.setTitle("Evoluzn")
            alertDialog.setMessage("Alarm Schedule!")

            alertDialog.show()








        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mUsbHandler.unRegisterReceiver()


    }

}