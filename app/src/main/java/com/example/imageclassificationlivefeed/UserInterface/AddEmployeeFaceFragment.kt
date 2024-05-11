package com.example.imageclassificationlivefeed.UserInterface

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageclassificationlivefeed.BackEnd.B2URLAndTOKEN
import com.example.imageclassificationlivefeed.BackEnd.DataManager
import com.example.imageclassificationlivefeed.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//private var upload_Bitmap:Bitmap?=null
//
//var imageFileToUpload: File? = null


class AddEmployeeFaceFragment : Fragment() {

    override fun onResume() {
        super.onResume()

        // Update the dialog here
        customDialog.updateDialog()
    }

    val itemListTwo = ArrayList<ItemModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // Declare CustomDialog instance
    private val customDialog by lazy { CustomDialog(requireActivity(),"","") }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_MAIN_ACTIVITY){
            customDialog.handleActivityResult(resultCode, data)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_employee_face,container,false)



        // Sample data for the RV
//        itemListTwo.add(ItemModel("E001", "ANIL KUMAR","P"))
//        itemListTwo.add(ItemModel("E002", "RAVI SINHA","P"))
//        itemListTwo.add(ItemModel("E003", "VIVEK SINGH","A"))
//        itemListTwo.add(ItemModel("E004", "ASIF ALI","P"))
//        itemListTwo.add(ItemModel("E005", "MD QUTUBUDDIN","P"))
//        itemListTwo.add(ItemModel("E006", "DIBYENDU KUMAR","P"))
//        itemListTwo.add(ItemModel("E007", "ANIL KUMAR","P"))
//        itemListTwo.add(ItemModel("E008", "RAVI SINHA","P"))
//        itemListTwo.add(ItemModel("E009", "VIVEK SINGH","A"))
//        itemListTwo.add(ItemModel("E010", "ASIF ALI","P"))
//        itemListTwo.add(ItemModel("E011", "MD QUTUBUDDIN","P"))
//        itemListTwo.add(ItemModel("E012", "DIBYENDU KUMAR","P"))
//        itemListTwo.add(ItemModel("E013", "ANIL KUMAR","P"))
//        itemListTwo.add(ItemModel("E014", "RAVI SINHA","P"))
//        itemListTwo.add(ItemModel("E015", "VIVEK SINGH","A"))
//        itemListTwo.add(ItemModel("E016", "ASIF ALI","P"))
//        itemListTwo.add(ItemModel("E017", "MD QUTUBUDDIN","P"))
//        itemListTwo.add(ItemModel("E018", "DIBYENDU KUMAR","P"))


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)


        if (DataManager.getItemList().isEmpty()) {
            DataManager.initializeData()
        }

        val sigletonItemsList = DataManager.getItemList()

        val newList = sharedViewModel.itemList.value ?: ArrayList()


//        val adapter = MyAdapterTwo(newList)

        //Add the data source into the adapter and also handle the onlick event of the button.
        val adapter = MyAdapterTwo(sigletonItemsList, customDialog)


        recyclerView.adapter = adapter

        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                divider?.let { setDrawable(it) }
            }
        )



        sharedViewModel.getBitmap().observe(viewLifecycleOwner) { bitmap ->
            Log.d("ObserverTag", "Observer triggered with bitmap: $bitmap")
            // Handle the received bitmap, e.g., update UI in your fragment's dialog
            if(bitmap == null)
            {
                Log.d("HandleBitmap", "Bitmap is null")
            }
            if (bitmap != null) {
//                upload_Bitmap = bitmap
////                val fileName = "face_image.jpg"
////                imageFileToUpload = bitmapToFile(requireContext(), upload_Bitmap!!,fileName)
//
//
//                Log.d("HandleBitmap", "Image changed")
//                Log.d("faceB2","Upload_URL: $upload_Url and Upload_TOKEN: $upload_Token")
//
////                if(imageFileToUpload!=null)
////                {
////                    DataManager.uploadImageToB2Cloud(requireContext(), upload_Url!!, upload_Token!!, imageFileToUpload!!)
////                    showToast("Uploaded To BB")
////                }else{
////                    showToast("Error Creating Image File")
////                }
//
////                DataManager.uploadBitmapToB2Cloud(requireContext(), upload_Url!!, upload_Token!!, upload_Bitmap!!)
//                // Update the UI in your custom dialog with the received bitmap
//                val url = B2URLAndTOKEN.uploadUrl
//                val token = B2URLAndTOKEN.uploadToken
//                DataManager.uploadBitmapToB2Cloud(requireContext(), url!!, token!!, bitmap)

                customDialog.setCapturedImage(bitmap)
            }
        }


        return view
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

//    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
//        val file = File(context.cacheDir, fileName)
//        try {
//            file.createNewFile()
//            val outputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//            return file
//        } catch (e: IOException) {
//            e.printStackTrace()
//            return null
//        }
//    }



    companion object {
        const val REQUEST_CODE_MAIN_ACTIVITY = 123
    }
}