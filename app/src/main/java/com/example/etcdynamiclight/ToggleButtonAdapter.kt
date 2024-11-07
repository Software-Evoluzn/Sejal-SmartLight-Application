package com.example.etcdynamiclight

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView

class ToggleButtonAdapter(private val mList:ArrayList<ContactModel>,private val mUsbHandler:USBHandler):
    RecyclerView.Adapter<ToggleButtonAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactModel:ContactModel=mList.get(position)
        val sharedPreferences=holder.itemView.context.getSharedPreferences("TogglePrefrences",Context.MODE_PRIVATE)
        val isChecked=sharedPreferences.getBoolean("${contactModel.device_id}_checked",false)
        val textColor=sharedPreferences.getInt("${contactModel.device_id}_color",Color.BLACK)

        holder.toggleButtons.isChecked=isChecked
          holder.toggleButtons.text = contactModel.device_id
        holder.toggleButtons.setTextColor(textColor)


        holder.toggleButtons.setOnCheckedChangeListener { buttonView, isChecked ->

            val newColor=if(isChecked) Color.WHITE else Color.BLACK
            holder.toggleButtons.setTextColor(newColor)



            with(sharedPreferences.edit()){
                putBoolean("${contactModel.device_id}_checked",isChecked)
                putInt("${contactModel.device_id}_color",textColor)
                apply()

            }

            val command=if(isChecked) "1" else "0"
            mUsbHandler.sendData(contactModel.device_id+command)

        }
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    class ViewHolder(ItemView:View): RecyclerView.ViewHolder(ItemView) {
        var toggleButtons:ToggleButton=itemView.findViewById(R.id.toggleButton)


    }


}