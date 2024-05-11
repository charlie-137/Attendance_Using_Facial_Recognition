package com.brogrammer.attendanceapp.UserInterface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.brogrammer.attendanceapp.BackEnd.DataManager
import com.brogrammer.attendanceapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// The adapter acts as a bridge between the data source and the 'RecyclerView' UI Component

class MyAdapterTwo(val itemsList:ArrayList<ItemModel>,
                   private val customDialog: CustomDialog
) :RecyclerView.Adapter<MyAdapterTwo.MyViewHolder>() {

    // ViewHolder: holding references to the views for a single
    // item in the 'RecyclerView'
    // itemView: Represents the view for a single item in RV
    inner class MyViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView){

        var empCode: TextView
        var empName: TextView
        var empButton: Button

        init {
            empCode = itemView.findViewById(R.id.empCode)
            empName = itemView.findViewById(R.id.empName)
            empButton = itemView.findViewById(R.id.materialButton)

            // Add click listener for the button
            empButton.setOnClickListener{
                val empCodeValue = itemsList[adapterPosition].empCode
                val empNameValue = itemsList[adapterPosition].empName

//                val newDialog = CustomDialog(itemView.context)
//                newDialog.setEmployeeCode(empCodeValue)
//                newDialog.setEmployeeName(empNameValue)


//                val dialog = CustomDialog(itemView.context as Activity, empCodeValue,empNameValue)

                customDialog.setEmployeeImage(DataManager.getImageBitmap(empCodeValue))
                customDialog.setEmployeeCode(empCodeValue)
                customDialog.setEmployeeName(empNameValue)

//                val url = B2URLAndTOKEN.uploadUrl
//                val token = B2URLAndTOKEN.uploadToken
//
//                val bitmap = DataManager.getImageBitmap(empCodeValue)
//                if(bitmap!=null)
//                {
//                    Log.d("RVB2","URL: $url Token: $token")
//                    Toast.makeText(itemView.context, "URL: $url Token: $token", Toast.LENGTH_SHORT).show()
////                    DataManager.uploadBitmapToB2Cloud(itemView.context, url!!, token!!, bitmap)
//                }

//                val drawableId = R.drawable.ragna // Replace my_image with the name of your drawable
//                val fileName = "anime.jpg" // Name you want to give to the uploaded file
//                var imageFile: File? = drawableToFile(itemView.context, drawableId, fileName)

                //            DataManager.uploadImageToB2Cloud(requireContext(), uploadUrl, uploadToken, imageFile!!)
//                if (url != null) {
//                    if (imageFile != null) {
//                        if (token != null) {
//                            DataManager.uploadImageToB2Cloud(itemView.context, url, token, imageFile)
//                        }else{
//                            Toast.makeText(itemView.context, "TOKEN: Null", Toast.LENGTH_SHORT).show()
//                        }
//                    }else{
//                        Toast.makeText(itemView.context, "IMAGE: Null", Toast.LENGTH_SHORT).show()
//                    }
//                }else{
//                    Toast.makeText(itemView.context, "URL: Null", Toast.LENGTH_SHORT).show()
//                }


                // Show the custom dialog
                customDialog.show()
//                dialog.show()
//                newDialog.show()
            }
        }

    }

    fun drawableToFile(context: Context, @DrawableRes drawableId: Int, fileName: String): File? {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val file = File(context.cacheDir, fileName)
        try {
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
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
            .inflate(R.layout.employee_single_item,parent,false)

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

        // Set background color based on the attendance status
//        when (itemsList[position].attendanceStatus) {
//            "A" -> {
//                holder.empAttendance.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
//                holder.empAttendance.setBackgroundResource(R.drawable.absent_background)
//            }
//            "P" -> holder.empAttendance.setBackgroundResource(R.drawable.present_background)
//            else -> holder.empAttendance.setBackgroundResource(android.R.color.transparent)
//        }
    }
}