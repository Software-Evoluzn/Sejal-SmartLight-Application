package com.example.etcdynamiclight

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
        holder.toggleButtons.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mUsbHandler.sendData(contactModel.device_id+"1")
            }else{
                mUsbHandler.sendData(contactModel.device_id+"0")
            }
        }
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    class ViewHolder(ItemView:View): RecyclerView.ViewHolder(ItemView) {
        var toggleButtons:ToggleButton=itemView.findViewById(R.id.toggleButton)


    }


}