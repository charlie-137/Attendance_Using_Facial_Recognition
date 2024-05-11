package com.example.imageclassificationlivefeed.UserInterface

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.imageclassificationlivefeed.BackEnd.DataManager
import com.example.imageclassificationlivefeed.BackEnd.TimestampsCallback
import com.example.imageclassificationlivefeed.R
import com.example.imageclassificationlivefeed.UserInterface.LoadingDialog.CustomDialogTimeStampsReport

// The adapter acts as a bridge between the data source and the 'RecyclerView' UI Component

class MyAdapter(
    val itemsList: ArrayList<ItemModel>,
    private val customDialogTimeStampsReport: CustomDialogTimeStampsReport,
    private val selectedDateDlg: String?,
    private val selectedDateForTimeStamps: Int
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder: holding references to the views for a single
    // item in the 'RecyclerView'
    // itemView: Represents the view for a single item in RV
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var empAttendance: TextView = itemView.findViewById(R.id.empAttendance)
        var empCode: TextView = itemView.findViewById(R.id.empCode)
        var empName: TextView = itemView.findViewById(R.id.empName)
        var empButton: Button = itemView.findViewById(R.id.materialButton)

        init {
            empButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemsList[position]
                    updateDialog(item)
                }
            }
        }

        private fun updateDialog(item: ItemModel) {
            val empCodeValue = item.empCode
            val empNameValue = item.empName

            // val selectedDateReport
            DataManager.fetchAttendanceTimestamps(
                empCodeValue,
                selectedDateForTimeStamps,
                object : TimestampsCallback {
                    override fun onTimestampsReady(timestamps: List<String>) {

                        // Pass timestamps to the dialog adapter and notify data change
                        customDialogTimeStampsReport.setTimestamps(timestamps)
                        customDialogTimeStampsReport.show()

                        for (timestamp in timestamps) {
                            Log.d(
                                "TimestampEmployee",
                                "Employee: $empNameValue, Timestamps: $timestamp"
                            )
                        }


                    }
                })

            // Update the dialog content using the CustomDialogTimeStampsReport instance
            customDialogTimeStampsReport.setEmployeeCode(empCodeValue)
            customDialogTimeStampsReport.setEmployeeName(empNameValue)
            customDialogTimeStampsReport.setEmployeeDate(selectedDateDlg)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // called when RV needs a new Viewholder Instance
        // inflating the layout for a single item and returning a new VH
        // viewGroup: is the parent view that the new view will be attached to
        //            after it's bound to it's data
        // viewType: in many cases you might have only one type of view
        //           used to distinguish between different view types

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        // returns the total number of items in the data set
        return itemsList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // bind data to a ViewHolder at a specific position

        holder.empCode.text = itemsList[position].empCode
        holder.empName.text = itemsList[position].empName
        holder.empAttendance.text = itemsList[position].attendanceStatus

        // Set background color based on the attendance status
        when (itemsList[position].attendanceStatus) {
            "A" -> {
                holder.empAttendance.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        android.R.color.white
                    )
                )
                holder.empAttendance.setBackgroundResource(R.drawable.absent_background)
            }

            "P" -> holder.empAttendance.setBackgroundResource(R.drawable.present_background)
            else -> holder.empAttendance.setBackgroundResource(android.R.color.transparent)
        }
    }
}