package com.brogrammer.attendanceapp.UserInterface.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brogrammer.attendanceapp.R

class TimestampAdapter(private val timestamps: List<String>) : RecyclerView.Adapter<TimestampAdapter.TimestampViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimestampViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timestamp, parent, false)
        return TimestampViewHolder(view)
    }

    override fun getItemCount(): Int {
        return timestamps.size
    }

    override fun onBindViewHolder(holder: TimestampViewHolder, position: Int) {
        holder.bind(timestamps[position])
    }

    inner class TimestampViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTV)

        fun bind(timestamp: String) {
            timestampTextView.text = timestamp
        }
    }
}
