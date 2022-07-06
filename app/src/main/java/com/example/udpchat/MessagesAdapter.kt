package com.example.udpchat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Msg(var name:String,var msg:String,var sent:Int)


class MessagesAdapter(var msgList: ArrayList<Msg>,var context:Context):RecyclerView.Adapter<ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return msgList[position].sent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 1){
            val view=LayoutInflater.from(context).inflate(R.layout.sent_msg_layout,parent,false)
            return ViewHolder(view)
        }
        else{
            val view=LayoutInflater.from(context).inflate(R.layout.rec_msg_layout,parent,false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message=msgList[position]
        holder.name.setText(message.name)
        holder.message.setText(message.msg)
    }
    override fun getItemCount(): Int {
        return msgList.size
    }
}


class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    var name: TextView = itemView.findViewById(R.id.senderName)
    var message: TextView = itemView.findViewById(R.id.senderMsg)
}

