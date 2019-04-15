package com.example.chatsapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycler_list_row.view.*

class RecyclerViewAdapter(var chatMessages: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_row,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val message = chatMessages[position]
        holder.itemView.recycler_textView.text = message
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        val shownMessage: TextView = itemView.findViewById(R.id.recycler_textView)

    }

}