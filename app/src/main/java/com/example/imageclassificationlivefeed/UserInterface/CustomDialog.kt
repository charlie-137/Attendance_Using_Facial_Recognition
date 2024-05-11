package com.example.imageclassificationlivefeed.UserInterface

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import com.example.imageclassificationlivefeed.BackEnd.B2URLAndTOKEN
import com.example.imageclassificationlivefeed.BackEnd.DataManager
import com.example.imageclassificationlivefeed.MainActivity
import com.example.imageclassificationlivefeed.R
import com.example.imageclassificationlivefeed.UserInterface.AddEmployeeFaceFragment.Companion.REQUEST_CODE_MAIN_ACTIVITY

class CustomDialog(private val activity: Activity, private val initialEmployeeCode: String?,private val initialEmployeeName:String? ) : Dialog(activity) {

    // Function to set the imageBitmap in the ImageView
    fun setEmployeeImage(imageBitmap: Bitmap?){
        val imageView: ImageView = findViewById(R.id.capturedImageViewDlg) ?: return
        imageView.setImageBitmap(imageBitmap ?: getDefaultImage())
    }

    // Function to get the default image (replace with your default image logic)
    private fun getDefaultImage(): Bitmap{
        //Replace this with your logic to get the default image
        return BitmapFactory.decodeResource(context.resources, R.drawable.no_image_available)
    }




    fun updateDialog() {
        updateImageFromDataManager(storedEmployeeCode)
    }

    fun handleActivityResult(resultCode:Int, data: Intent?){
        // Handle the result from MainActivity if needed
    }


    private lateinit var employeeCode: TextView
    private lateinit var employeeName: TextView
    private lateinit var openCameraButton: Button
    private lateinit var capturedImageView: ImageView
    private lateinit var saveButton: Button






    //    // Add properties to store employee code and name
//    private var storedEmployeeCode: String? = null
//    private var storedEmployeeName: String? = null
    private var storedEmployeeCode: String? = initialEmployeeCode
    private var storedEmployeeName: String? = initialEmployeeName

    // Flag to check whether the views are initialized
    private var viewsInitialized = false
    var isRegisterFace = true

    fun setEmployeeCode(code: String){
        storedEmployeeCode = code
        if (viewsInitialized){
            employeeCode.text = code
        }
//        if(::employeeCode.isInitialized)
//        {
//            employeeCode.text = code
//        }
    }

    fun setEmployeeName(name: String){
        storedEmployeeName = name

        if(viewsInitialized){
            employeeName.text = name
        }

//        if(::employeeName.isInitialized)
//        {
//            employeeName.text = name
//        }

    }

    fun updateImageFromDataManager(empCode: String?) {
        val imageBitmap = DataManager.getImageBitmap(empCode)
        if (imageBitmap != null) {
            // Update your ImageView or any other UI component with the retrieved bitmap
            capturedImageView.setImageBitmap(imageBitmap)
        }
    }

    fun setCapturedImage(bitmap: Bitmap) {
        // Update your ImageView or any other UI component with the received bitmap
        Log.d("CustomDialog", "Setting captured image in dialog")
        capturedImageView.setImageBitmap(bitmap)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_add_employee)

        employeeCode = findViewById(R.id.employeeCode)
        employeeName = findViewById(R.id.employeeName)
        openCameraButton = findViewById(R.id.openCameraButton)
        capturedImageView = findViewById(R.id.capturedImageViewDlg)
        saveButton = findViewById(R.id.saveButton)

        // set initial values
        employeeCode.text = storedEmployeeCode
        employeeName.text = storedEmployeeName


//        openCameraButton.setOnClickListener {
//            // Start the activity here
//            val intent = Intent(context, MainActivity::class.java)
//
//            // Pass the employeeName as an extra to the intent
//            intent.putExtra("employeeName", storedEmployeeName)
//            intent.putExtra("isRegisterFace", isRegisterFace)
//
//            // Use the context as an Activity to launch for result
//            (context as? Activity)?.let {
//                (it as? FragmentActivity)?.let { fragmentActivity ->
//                    val startForResult =
//                        fragmentActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                            if (result.resultCode == Activity.RESULT_OK) {
//                                // Handle the result here if needed
////                                dismiss()
//                            }
//                        }
//                    startForResult.launch(intent)
//                }
//            }
//        }








        openCameraButton.setOnClickListener {
            // Start the activity here
            val intent = Intent(activity, MainActivity::class.java)

            // Pass the employeeName as an extra to the intent
            intent.putExtra("employeeName", storedEmployeeName)
            intent.putExtra("employeeCode", storedEmployeeCode)
            intent.putExtra("isRegisterFace",isRegisterFace)

            activity.startActivityForResult(intent, REQUEST_CODE_MAIN_ACTIVITY)

//            context.startActivity(intent)

            // If you want to close the dialog after opening the activity, uncomment the line below
            // dismiss()
        }

        updateImageFromDataManager(storedEmployeeCode)


        saveButton.setOnClickListener {

            val url = B2URLAndTOKEN.uploadUrl
            val token = B2URLAndTOKEN.uploadToken

            val bitmap = DataManager.getImageBitmap(storedEmployeeCode)

            val name = storedEmployeeName
            val code = storedEmployeeCode
            val imgName = "${code}_$name"


            if(bitmap!=null)
            {
                Log.d("RVB2","URL: $url Token: $token")
                Toast.makeText(context, "URL: $url Token: $token", Toast.LENGTH_SHORT).show()
                DataManager.uploadBitmapToB2Cloud(context, url!!, token!!, bitmap, imgName)
            }
            dismiss()

        }


        val closeButton = findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        val closeIcon = findViewById<ImageButton>(R.id.closeIcon)
        closeIcon.setOnClickListener{
            dismiss()
        }

        // Set the window attributes to achieve desired width and height
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        // Set the flag to true after views are initialized
        viewsInitialized = true


        // Disable closing the dialog on touch outside
        setCanceledOnTouchOutside(false)


        // Add any additional logic or listeners as needed
    }

    // Add methods to set/get data from UI elements, e.g., getEmployeeCode(), setEmployeeName(), etc.
}