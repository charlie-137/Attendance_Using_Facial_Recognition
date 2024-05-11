package com.example.imageclassificationlivefeed.UserInterface

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.imageclassificationlivefeed.BackEnd.B2URLAndTOKEN
import com.example.imageclassificationlivefeed.BackEnd.DataManager
import com.example.imageclassificationlivefeed.BackEnd.ImageDownloadCallback
import com.example.imageclassificationlivefeed.MainActivity
import com.example.imageclassificationlivefeed.R
//import io.realm.gradle.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.Realm
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import com.example.imageclassificationlivefeed.BackEnd.UploadUrlCallback

private lateinit var app: App
private val appId = "application-0-wnbyu"

var authToken = "4_0054d843a7610950000000002_01b2d2b4_4e223a_acct_SE852uZ9e8SPqsPSdNuQD5oDw5E="
var apiUrl = "https://api005.backblazeb2.com"

var upload_Url:String?=null
var upload_Token:String?=null


var imageFile: File? = null

var token_header:String? = null

//private lateinit var imageViewB2: ImageView


class DashboardFragment : Fragment(),UploadUrlCallback, ImageDownloadCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // TODO MongoDB Code
        Realm.init(requireContext())
        app = App(AppConfiguration.Builder(appId).build())



        // Pass the Realm app instance to the Data Manager
        DataManager.initializeRealm(app)



        context?.let {
            DataManager.getAllUsersData(it)
            DataManager.getFaceDataFromMongo(it)
//            DataManager.getAuthTokenBackBlaze(it)
            DataManager.getUploadURLB2Cloud(it,authToken, apiUrl,this)

            // Assuming you want to upload an image when the fragment is created
            val drawableId = R.drawable.ragna // Replace my_image with the name of your drawable
            val fileName = "anime.jpg" // Name you want to give to the uploaded file
            imageFile = drawableToFile(it, drawableId, fileName)
        }






        val addFaceBtn: CardView = view.findViewById(R.id.addFaceBtn)
        val takeAttendanceBtn: CardView = view.findViewById(R.id.takeAttendanceBtn)
        val empAttendanceReportBtn: CardView = view.findViewById(R.id.empAttendanceReportBtn)
        val employeeWiseAttendanceReport: CardView = view.findViewById(R.id.employeeWiseAttendanceReport)

//        val showImage: CardView =  view.findViewById(R.id.showImageBtn)
//        imageViewB2 = view.findViewById(R.id.imageViewFromBB)

//        showImage.setOnClickListener {
//            val baseUrl = DataManager.getDownloadURL()
//            val imageId = DataManager.getDownloadAuthToken()
////            val DOWNLOAD_URL = baseUrl+imageId
////            val downloadURL = DataManager.getDownloadURL()+DataManager.getDownloadAuthToken()
//            val downloadAuthToken = DataManager.authenticationToken
//            if(downloadAuthToken!=null)
//            {
//                DataManager.downloadImageFromB2Cloud(requireContext(),DataManager.Download_Url,downloadAuthToken,this)
//            }
//
//        }
//        val dataAndTime: TextView = view.findViewById(R.id.dateAndTime)

//        printDataTimeBtn.setOnClickListener {
//            // Get the current date and time
//            val currentDateTime = LocalDateTime.now()
//
//
//            //S
//            //S
//            val dateNtime = Date().time.toString()
//
//            // Format the date and time
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            val formattedDateTime = currentDateTime.format(formatter)
//
//            // Print the formatted date and time
//            dataAndTime.text = dateNtime.toString()
//        }


        var isRegisterFace = false


        addFaceBtn.setOnClickListener {
            val fragment = AddEmployeeFaceFragment()
            replaceFragment(fragment)
            showToast("U-URL"+DataManager.getUploadURl().toString())
//            Toast.makeText(activity, "Add Face Button Clicked", Toast.LENGTH_SHORT).show()
        }



        takeAttendanceBtn.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)

            // Pass the isRegisterFace as an extra to the intent
            intent.putExtra("isRegisterFace", isRegisterFace)



            context?.startActivity(intent)

            // If you want to close the dialog after opening the activity, uncomment the line below
            // dismiss()
//            Toast.makeText(activity, "Take Attendance Button Clicked", Toast.LENGTH_SHORT).show()
        }

        empAttendanceReportBtn.setOnClickListener {
            val fragment = AttendanceReportFragment()
            replaceFragment(fragment)
//            Toast.makeText(activity, "Employee Attendance Report Button Clicked", Toast.LENGTH_SHORT).show()
        }

        employeeWiseAttendanceReport.setOnClickListener {
            val fragment = AttendanceReportEmployeeWiseFragment()
            replaceFragment(fragment)
//            Toast.makeText(activity, "Employee Wise Attendance Report Button Clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }


    override fun onUploadUrlReceived(uploadUrl: String?, uploadToken: String?) {
        // Handle the received upload URL
        if (uploadUrl != null && imageFile != null && uploadToken != null) {
            showToast("U-URL- $uploadUrl authToken - $uploadToken")
            Log.d("MYURLB2",uploadUrl)
            Log.d("MYURLB2", uploadToken)

            B2URLAndTOKEN.uploadUrl = uploadUrl
            B2URLAndTOKEN.uploadToken = uploadToken

            upload_Url = uploadUrl
            upload_Token = uploadToken
//            DataManager.uploadImageToB2Cloud(requireContext(), uploadUrl, uploadToken, imageFile!!)
        } else {
            showToast("Upload URL, image file, or auth token is null.")
        }
    }

    override fun onImageDownloaded(bitmap: Bitmap) {
//        imageViewB2.setImageBitmap(bitmap)
    }

    override fun onImageDownloadError(error: String) {
        Log.e("DownloadImgB2","Error downloading or setting image: $error")
    }



    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
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




    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}