package com.example.imageclassificationlivefeed.BackEnd

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imageclassificationlivefeed.Face_Recognition.FaceClassifier
import com.example.imageclassificationlivefeed.Face_Recognition.FaceClassifier.Recognition
import com.example.imageclassificationlivefeed.UserInterface.ItemModel
import com.example.imageclassificationlivefeed.Utils.Utils
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lte
import io.realm.mongodb.App
import org.bson.Document
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.android.volley.Response
import org.json.JSONException

import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.ImageRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Error
import java.security.MessageDigest

interface ReportCallback {
    fun onReportReady(presentCount: Int, absentCount: Int)
}

interface DatesCallback {
    fun onDatesReady(datesList: List<Int>)
}

interface TimestampsCallback {
    fun onTimestampsReady(timestamps: List<String>)
}

interface UploadUrlCallback {
    fun onUploadUrlReceived(uploadUrl: String?,uploadToken: String?)
}

interface ImageDownloadCallback{
    fun onImageDownloaded(bitmap: Bitmap)
    fun onImageDownloadError(error: String)
}




object DataManager {

    private var registeredHashMap: HashMap<String, FaceClassifier.Recognition> = HashMap()

    val myItemList = ArrayList<ItemModel>()

    private const val SERVER_URL = "http://192.168.1.8/FacialRecognitionCogTech.php/getAllUsersData" // Replace with your localhost URL
    //    private const val WEB_HOST_URL = "https://cognitosoftech.000webhostapp.com/FacialRecognitionCogTech.php?action=getAllUsersData"

    private const val B2_UPLOAD_URL = "http://192.168.1.8/B2UploadURLTwo.php" // Replace with your localhost URL

    private const val B2_AUTH_TOKEN_URL = "http://192.168.1.8/authTokenBackBlaze.php" // Replace with your localhost URL

    //gets refreshed everyday
    var authenticationToken = "4_0054d843a7610950000000002_01b28433_e55be4_acct_PkHKwYS_GH0pX6BbsqusgKUOKEc="

    private var AUTH_TOKEN_B2: String? = null
    private var API_URL_B2: String? = null
    private var BUCKET_ID_B2 = "143d5844733aa71681d00915"
    private var KEY_ID_B2 = "0054d843a7610950000000002"
    private var APPLICATION_KEY_B2 = "K0050NuDxZpLltvXJFZUlQ7cKSbASQ8"

    private var SHA1 = "42:AC:7F:B9:89:49:E3:83:EA:1F:32:23:E6:51:3F:6D:5E:C7:5D:BE"

     private var UPLOAD_URL_B2: String? = null

    private var UPLOAD_TOKEN_B2: String? = null

    private var DOWNLOAD_URL_B2: String? = "https://f005.backblazeb2.com/b2api/v1/b2_download_file_by_id?fileId="


    private var DOWNLOAD_FILE_ID_B2: String? = null

    private var DOWNLOAD_AUTH_TOKEN_B2: String? = "4_z24adb8d4335ac71681d00915_f110d338cb1f425de_d20240220_m105523_c005_v0501002_t0040_u01708426523278"

    var Download_Url =
        "https://f005.backblazeb2.com/b2api/v1/b2_download_file_by_id?fileId=$DOWNLOAD_AUTH_TOKEN_B2"


    //MongoDb
    lateinit var app: App

    var countPresentEmployee: Int = 0

    fun getDownloadAuthToken():String? {
        return DOWNLOAD_AUTH_TOKEN_B2
    }

    fun getDownloadURL():String? {
        return DOWNLOAD_URL_B2
    }

    fun getUploadURl() : String?
    {
        return UPLOAD_URL_B2
    }


    @JvmStatic
    fun initializeRealm(appInstance: App) {
        app = appInstance
    }

    // Function to get the itemList
    @JvmStatic
    fun getRegisteredHashMap(): HashMap<String, Recognition> {
        return registeredHashMap
    }

    //add the recognition to the registeredHasMap
    @JvmStatic
    fun addToRegisteredHashMap(name: String, recognition: FaceClassifier.Recognition) {
        registeredHashMap[name] = recognition
    }



    fun getAuthToken(): String?{
        return AUTH_TOKEN_B2
    }

    fun getApiURL(): String?{
        return API_URL_B2
    }


    // Function to initialize data in the singleton (for example, during app startup)
    fun initializeData() {
        myItemList.add(ItemModel(1, "E001", "SARTAJ", "A", null, 1, 1))
        myItemList.add(ItemModel(2, "E002", "ABHINAV", "A", null, 1, 1))
        myItemList.add(ItemModel(3, "E003", "JUSTIN", "A", null, 1, 1))
        // ... add more items as needed
    }

    private fun resetAttendanceStatus() {
        // Reset attendance status to "Absent" for all employees
        for (employee in myItemList) {
            employee.attendanceStatus = "A"
        }
    }

    // Function to get the itemList
    fun getItemList(): ArrayList<ItemModel> {
        return myItemList
    }

    // Function to get the imageBitmap of a particular employee
    @JvmStatic
    fun getImageBitmap(empCode: String?): Bitmap? {
        val item = myItemList.find { it.empCode == empCode }
        return item?.faceImgId
    }

    // Function to update the itemList
    fun updateItemList(newItemList: List<ItemModel>) {
        myItemList.clear()
        myItemList.addAll(newItemList)
    }

    @JvmStatic
    // Function to update the attendance status of a particular employee
    fun updateAttendance(empCode: String, newAttendanceStatus: String) {
        val itemToUpdate = myItemList.find { it.empCode == empCode }
        itemToUpdate?.let {
            // Update the attendance status
            it.attendanceStatus = newAttendanceStatus
        }
    }


    @JvmStatic
    // Function to update the attendance status of a particular employee
    fun updateAttendanceReport(empCode: String) {
        val itemToUpdate = myItemList.find { it.empCode == empCode }
        itemToUpdate?.let {
            // Update the attendance status
            it.attendanceStatus = "P"
        }
    }


    // Function to update the imageBitmap of a particular employee
    @JvmStatic
    fun updateImageBitmap(empCode: String, newImageBitmap: Bitmap?) {
        val itemToUpdate = myItemList.find { it.empCode == empCode }
        itemToUpdate?.let {
            // Update the imageBitmap if provided
            it.faceImgId = newImageBitmap
        }
    }

    @JvmStatic
    fun getEmployeeId(empCode: String): Int? {
        val item = myItemList.find { it.empCode == empCode }
        return item?.id
    }

    @JvmStatic
    fun getEmployeeName(empCode: String): String? {
        val item = myItemList.find { it.empCode == empCode }
        return item?.empName
    }


    //     Employee wise attendance report between the given date range
    fun employeeWiseAttendanceReportFromMongo(
        selectedEmployeeId: String, startDate: Int, endDate: Int, callback: DatesCallback
    ) {
        val defaultJavaCodecRegistry = MongoClientSettings.getDefaultCodecRegistry()
        val user = app.currentUser()
        val mongoClient = user?.getMongoClient("mongodb-atlas")
        val mongoDatabase = mongoClient?.getDatabase("DemoDataDB")
        val mongoCollection = mongoDatabase?.getCollection("AttendanceHistory")
            ?.withCodecRegistry(defaultJavaCodecRegistry)



        val query = and(
            eq("EmployeeId", selectedEmployeeId),
            gte("Date", startDate),
            lte("Date", endDate),
            eq("Status", "P")
        )


        val resultTask = mongoCollection?.find(query)?.iterator()

        resultTask?.getAsync { cursor ->
            Log.v("LogDebug", "Async operation started")
            if (cursor.isSuccess) {
                Log.v("LogDebug", "Async operation success")
                val mongoCursor = cursor.get()
                try {
                    Log.v("LogDebug", "Inside try block")
                    val datesList = mutableListOf<Int>()
                    while (mongoCursor.hasNext()) {
                        val foundDocument = mongoCursor.next()
                        if (foundDocument != null) {
                            val dateFromMongo = foundDocument.getInteger("Date")
                            Log.v("EmployeeWiseReport", "Date: $dateFromMongo")
                            datesList.add(dateFromMongo)
                        } else {
                            Log.v("DataInsert", "No document found for the provided filter")
                        }
                    }
                    Log.v("LogDebug", "Dates list size: ${datesList.size}")

                    // Notify the callback with the list of dates
                    callback.onDatesReady(datesList)
                } finally {
                    Log.v("LogDebug", "Closing cursor")
                    mongoCursor.close()
                }
            } else {
                Log.v("LogDebug", "Async operation failed: ${cursor.error}")
            }
            Log.v("LogDebug", "Async operation completed")
        }

    }


    // All the employee attendance report of any given date
    fun attendanceReportCurrentDateFromMongo(selectedDate: Int?, callback: ReportCallback) {

        var empPresentHashSet: HashSet<String> = HashSet()
        var countPresent = 0

        // Reset attendance status to "Absent" for all employees
        resetAttendanceStatus()

        val user = app.currentUser()
        val mongoClient = user?.getMongoClient("mongodb-atlas")
        val mongoDatabase = mongoClient?.getDatabase("DemoDataDB")
        val mongoCollection = mongoDatabase?.getCollection("AttendanceHistory")
        val obj = Document()
//        obj.append("Name", name)
//        obj.append("Embeddings", tempResult)
//        obj.append("userid", user?.id)
//        Log.v("DataInsert", obj.toString())

        // TODO Fetching all the data from the MongoDB
        val resultTask = mongoCollection?.find()?.iterator()

        // Now, let's try to extract the cursor from the result task
        resultTask?.getAsync { cursor ->
            if (cursor.isSuccess) {
                val mongoCursor = cursor.get() // Extracting the cursor

                try {
                    while (mongoCursor.hasNext()) {
                        val foundDocument = mongoCursor.next()

                        if (foundDocument != null) {
                            val dateFromMongo = foundDocument.getInteger("Date")
                            if (selectedDate == dateFromMongo) {
                                Log.d("DateLogMongo", dateFromMongo.toString())
                                val idFromMongo = foundDocument.getInteger("Id")
                                val empId = foundDocument.getString("EmployeeId")
                                if (empId != null) {
                                    var empName = getEmployeeName(empId)
                                    if (empName != null) {
                                        Log.v(
                                            "TheNameLog",
                                            "$empName was present on the selected date $selectedDate"
                                        )
                                        countPresent++
                                        empPresentHashSet.add(empId)
                                        updateAttendanceReport(empId)

                                    }
                                }
                            }


                        } else {
                            Log.v("DataInsert", "No document found for the provided filter")
                        }
//                        setPresentCount(empPresentHashSet.size)
//                        //To Log the number of employee found present on any selected date
//                        Log.v("TheNameLog", "Count of employee present on $selectedDate are "+getPresentCount())
//                        Log.v("TheNameLog", "Count of employee present on $selectedDate are "+empPresentHashSet.size)

                        // Your processing logic here
                    }
                    setPresentCount(empPresentHashSet.size)
                    Log.v(
                        "TheNameLog",
                        "Count of employee present on $selectedDate are " + getPresentCount()
                    )
                    Log.v(
                        "TheNameLog",
                        "Count of employee Absent on $selectedDate are " + getAbsentCount(
                            getPresentCount()
                        )
                    )
                    // Notify the callback with the results
                    callback.onReportReady(getPresentCount(), getAbsentCount(getPresentCount()))


                } finally {
                    Log.d("DataReadyTrack", "Data Ready value: ")
                    mongoCursor.close() // Always close the cursor
                }
            } else {
                Log.v("DataInsert", cursor.error.toString())
            }
        }
    }


    // All the employee attendance report of any given date
    fun attendanceReportFromMongo(selectedDate: Int?, callback: ReportCallback) {

        var empPresentHashSet: HashSet<String> = HashSet()
        var countPresent = 0

        // Reset attendance status to "Absent" for all employees
        resetAttendanceStatus()

        val user = app.currentUser()
        val mongoClient = user?.getMongoClient("mongodb-atlas")
        val mongoDatabase = mongoClient?.getDatabase("DemoDataDB")
        val mongoCollection = mongoDatabase?.getCollection("AttendanceHistory")
        val obj = Document()
//        obj.append("Name", name)
//        obj.append("Embeddings", tempResult)
//        obj.append("userid", user?.id)
//        Log.v("DataInsert", obj.toString())

        // TODO Fetching all the data from the MongoDB
        val resultTask = mongoCollection?.find()?.iterator()

        // Now, let's try to extract the cursor from the result task
        resultTask?.getAsync { cursor ->
            if (cursor.isSuccess) {
                val mongoCursor = cursor.get() // Extracting the cursor

                try {
                    while (mongoCursor.hasNext()) {
                        val foundDocument = mongoCursor.next()

                        if (foundDocument != null) {
                            val dateFromMongo = foundDocument.getInteger("Date")
                            if (selectedDate == dateFromMongo) {
                                Log.d("DateLogMongo", dateFromMongo.toString())
                                val idFromMongo = foundDocument.getInteger("Id")
                                val empId = foundDocument.getString("EmployeeId")
                                if (empId != null) {
                                    var empName = getEmployeeName(empId)
                                    if (empName != null) {
                                        Log.v(
                                            "TheNameLog",
                                            "$empName was present on the selected date $selectedDate"
                                        )
                                        countPresent++
                                        empPresentHashSet.add(empId)
                                        updateAttendanceReport(empId)

                                    }
                                }
                            }


                        } else {
                            Log.v("DataInsert", "No document found for the provided filter")
                        }
//                        setPresentCount(empPresentHashSet.size)
//                        //To Log the number of employee found present on any selected date
//                        Log.v("TheNameLog", "Count of employee present on $selectedDate are "+getPresentCount())
//                        Log.v("TheNameLog", "Count of employee present on $selectedDate are "+empPresentHashSet.size)

                        // Your processing logic here
                    }
                    setPresentCount(empPresentHashSet.size)
                    Log.v(
                        "TheNameLog",
                        "Count of employee present on $selectedDate are " + getPresentCount()
                    )
                    Log.v(
                        "TheNameLog",
                        "Count of employee Absent on $selectedDate are " + getAbsentCount(
                            getPresentCount()
                        )
                    )
                    // Notify the callback with the results
                    callback.onReportReady(getPresentCount(), getAbsentCount(getPresentCount()))


                } finally {
                    Log.d("DataReadyTrack", "Data Ready value: ")
                    mongoCursor.close() // Always close the cursor
                }
            } else {
                Log.v("DataInsert", cursor.error.toString())
            }
        }
    }


    fun setPresentCount(countPresent: Int) {
        countPresentEmployee = 0
        countPresentEmployee = countPresent
    }


    fun getPresentCount(): Int {
        return countPresentEmployee
    }


    fun getAbsentCount(presentCount: Int): Int {
        var absentCount = 0
        absentCount = (myItemList.size) - presentCount
        return absentCount
    }



    // Function to fetch all timestamps for a specific employee on a given date
    fun fetchAttendanceTimestamps(employeeId: String, date: Int, callback: TimestampsCallback) {

        val defaultJavaCodecRegistry = MongoClientSettings.getDefaultCodecRegistry()
        val user = app.currentUser()
        val mongoClient = user?.getMongoClient("mongodb-atlas")
        val mongoDatabase = mongoClient?.getDatabase("DemoDataDB")
        val mongoCollection = mongoDatabase?.getCollection("AttendanceHistory")
            ?.withCodecRegistry(defaultJavaCodecRegistry)

        val query = and(
            eq("EmployeeId", employeeId),
            eq("Date", date)
        )

        val resultTask = mongoCollection?.find(query)?.iterator()

        resultTask?.getAsync { cursor ->
            if (cursor.isSuccess) {
                val timestamps = mutableListOf<String>()
                val mongoCursor = cursor.get()

                try {
                    while (mongoCursor.hasNext()) {
                        val document = mongoCursor.next()
                        val timestamp = document.getString("TimeStamp")
                        timestamps.add(timestamp)
                    }

                    val timestampsReport = convertTimestamps(timestamps)

                    callback.onTimestampsReady(timestampsReport)
                } finally {
                    mongoCursor.close()
                }
            } else {
                // Handle error
            }
        }
    }




    // Fetching the all users data from the localhost database
    fun getAllUsersData(context: Context) {
        if (myItemList.size == 0) {
            Utils.showToast(context, "Data Fetched From Local Host")

            val requestQueue = Volley.newRequestQueue(context)

            val stringRequest = StringRequest(Request.Method.POST, SERVER_URL, { response ->
                Log.d("Charlie_1371", response)
                if (response == "fail") {
                    Log.d("Charlie_137", "Request failed")
                } else {
                    var id: Int    // Stores the unique auto incremented Id for each employee
                    var employeeName: String  // Stores the name of the employee
                    var employeeCode: String  // Stores the employee code of the employee
                    var attendanceEligibility: Int // Stores whether we have to take the attendance for the particular employee
                    var employmentStatus: Int  // Stores the current employment status of the employee
                    try {
                        val jsonObject = JSONObject(response)
                        val jsonArray = jsonObject.getJSONArray("response_obj")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            id = jsonObject1.optInt("id")
                            employeeName = jsonObject1.optString("empName")
                            employeeCode = jsonObject1.optString("empCode")
                            attendanceEligibility = jsonObject1.optInt("attnStatus")
                            employmentStatus = jsonObject1.optInt("employmentStatus")

                            myItemList.add(
                                ItemModel(
                                    id,
                                    employeeCode,
                                    employeeName,
                                    "A",
                                    null,
                                    employmentStatus,
                                    attendanceEligibility
                                )
                            )


                            val toastText =
                                "ID: $id, Name: $employeeName, Code: $employeeCode, Status: $attendanceEligibility, Employment: $employmentStatus"
                            Log.d("ToastTextTAG", toastText)
//                            Toast.makeText(context, employeeName, Toast.LENGTH_SHORT).show()

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, { error ->
                Log.e("Charlie_137", "Error in request: $error")
            })
            requestQueue.add(stringRequest)
            Log.d("Charlie_137", "Request added to queue")

        }

    }


    @JvmStatic
    fun getFaceDataFromMongo(context: Context) {

        if (registeredHashMap.size == 0) {

            Utils.showToast(context, "Data Fetched From MongoDB")

            val user = app.currentUser()
            val mongoClient = user?.getMongoClient("mongodb-atlas")
            val mongoDatabase = mongoClient?.getDatabase("DemoDataDB")
            val mongoCollection = mongoDatabase?.getCollection("TestData")
            val Obj = Document()
//        Obj.append("Name", name)
//        Obj.append("Embeddings", tempResult)
//        Obj.append("userid", user.id)
//        Log.v("DataInsert", Obj.toString())
            //Document queryFilter = new Document("userid", user.getId());
            // Document queryFilter = new Document();


            //TODO Fetching all the data from the MongoDB
            val resultTask = mongoCollection?.find()?.iterator()

            // Now, let's try to extract the cursor from the result task
            resultTask?.getAsync { cursor ->
                if (cursor.isSuccess) {
                    val mongoCursor = cursor.get() // Extracting the cursor
                    try {
                        while (mongoCursor.hasNext()) {
                            val foundDocument = mongoCursor.next()
                            if (foundDocument != null) {
                                val name = foundDocument.getString("Name")
                                val embeddings = foundDocument.getString("Embeddings")
                                val empCode = foundDocument.getString("EmployeeID")
                                if (name != null && embeddings != null && empCode != null) {
                                    val stringArray = embeddings.split(",")
                                    val knownEmb = Array(1) { FloatArray(stringArray.size) }

                                    for (i in stringArray.indices) {
                                        Log.d("MYTag", "Results : ${stringArray[i]}")
                                        knownEmb[0][i] = stringArray[i].toFloat()
                                    }

                                    // Create a new instance of FaceClassifier.Recognition
                                    val rec = FaceClassifier.Recognition(
                                        empCode, // Generate a unique ID for each recognition
                                        name,
                                        null, // You might want to set distance to null initially
                                        null // Location can be null initially or set as needed
                                    )

                                    // Set the embedding for the recognition
                                    rec.setEmbeeding(knownEmb)
                                    Log.d("MAJID", rec.getId())

                                    // Store the recognition in the registered hashmap with name as key
                                    registeredHashMap[name] = rec
                                } else {
                                    Log.v("RetrievedData", "One or both fields are null")
                                }
                            } else {
                                Log.v(
                                    "DataInsert", "No document found for the provided filter"
                                )
                            }

                            // Your processing logic here
                        }
                    } finally {
                        Log.d("DataReadyTrack", "Data Ready value: ")
                        mongoCursor.close() // Always close the cursor
                    }
                } else {
                    Log.v("DataInsert", cursor.error.toString())
                    //                Toast.makeText(MainActivity.this, cursor.getError().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }



//    fun uploadB2Cloud(context: Context) {
//
//            val requestQueue = Volley.newRequestQueue(context)
//
//            val stringRequest = StringRequest(Request.Method.POST, B2_UPLOAD_URL, { response ->
//                if (response == "fail") {
//                } else {
//
//                }
//            }, { error ->
//                Log.e("Charlie_137", "Error in request: $error")
//            })
//            requestQueue.add(stringRequest)
//
//    }

    fun getUploadURLB2Cloud(context: Context, authToken: String?, apiUrl: String?, callback: UploadUrlCallback) {

        Log.d("uploadURLToken", "appUrl: $apiUrl, \n authToken: $authToken")

        val requestQueue = Volley.newRequestQueue(context)

//        Log.d("uploadURLToken", "appUrl: $apiUrl, authToken: $authToken")

        val stringRequest = object : StringRequest(Method.POST, B2_UPLOAD_URL,
            Response.Listener { response ->
                // Handle the response here
                try {
//                    Log.d("uploadURLToken", "appUrl: $apiUrl, authToken: $authToken")
//                    Log.d("uploadURLToken", "respons: $response")



                    val jsonResponse = JSONObject(response)
                    Log.d("uploadURLResponse", "respons: $jsonResponse")
                    val status = jsonResponse.getInt("tp")

                    if(status == 1){
//                        val result = jsonResponse.getJSONObject("response")
                        val token = jsonResponse.getString("token")

                        //upload URL
                        val url = jsonResponse.getString("url")
                        // Now you have the token and URL, you can proceed with uploading your file
                        UPLOAD_URL_B2 = url
                        UPLOAD_TOKEN_B2 = token

                        callback.onUploadUrlReceived(UPLOAD_URL_B2, UPLOAD_TOKEN_B2)

                        Log.d("UploadURLLogg", UPLOAD_URL_B2.toString())

                        Log.d("uploadURLToken", "Token: $token, URL: $url")
                    }else{
                        //Handle error case
                        val error = jsonResponse.getString("errcode")
                        Log.e("uploadURLToken","Failed to get upload URL: ${jsonResponse.getString("errcode")}")
                    }

                } catch (e: JSONException) {
                    Log.e("uploadURLToken", "Error parsing JSON: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Handle errors here
                Log.e("uploadB2Cloud", "Error in request: $error")
            }) {
            // Override the getParams() method to pass parameters
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["authToken"] = authToken ?: ""
                params["apiUrl"] = apiUrl ?: ""
                return params
            }
        }

        // Add the request to the request queue
        requestQueue.add(stringRequest)
    }




    fun getAuthTokenBackBlaze(context: Context) {

        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.POST, B2_AUTH_TOKEN_URL,
            { response ->
                // Handle the response here
                try {

                    //parse the JSON response
                    val jsonResponse = JSONObject(response)
                    Log.d("jsonResponse","Response: $response")

                    // Extract the authorizationToken
                    AUTH_TOKEN_B2 = jsonResponse.getString("authorizationToken")
                    API_URL_B2 = jsonResponse.getString("apiUrl")

                    // Use the token as needed
                    Log.d("authTokenB2", "auth_token: $AUTH_TOKEN_B2 and api_Url: $API_URL_B2")

//                    getUploadURLB2Cloud(context, AUTH_TOKEN_B2, API_URL_B2)

                } catch (e: Exception) {
                    Log.e("Charlie_137", "Error parsing response: ${e.message}")
                }
            },
            { error ->
                // Handle errors here
                Log.e("Charlie_137", "Error in request: $error")
            })

        requestQueue.add(stringRequest)
    }



    fun uploadImageToB2Cloud(context: Context, uploadUrl: String, authorizationToken: String, fileToUpload: File) {
        val requestQueue = Volley.newRequestQueue(context)

        try {
            // Calculate SHA1 hash of the file
            val sha1OfFile = calculateSHA1(fileToUpload)

            // Get MIME type of the file (automatically determined by Backblaze B2)
            val mimeType = "image/jpeg"

            // Get file name
            val fileName = fileToUpload.name

            val uploadRequest = object : StringRequest(Method.POST, uploadUrl,
                Response.Listener<String> { response ->

                    val jsonResponse = JSONObject(response)
                    DOWNLOAD_FILE_ID_B2 = jsonResponse.getString("fileId")
                    DOWNLOAD_URL_B2+= DOWNLOAD_FILE_ID_B2
                    Log.d("DownloadURLB2","D_URL: $DOWNLOAD_URL_B2")

                    Log.d("ResponseB2",response.toString())
                    Log.d("UploadBB2", "File uploaded successfully: $response")
                },
                Response.ErrorListener { error ->
                    Log.e("UploadBB2", "Error uploading file: $error")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = authorizationToken
                    headers["X-Bz-File-Name"] = fileName
                    headers["Content-Type"] = mimeType
                    headers["X-Bz-Content-Sha1"] = sha1OfFile
                    headers["X-Bz-Info-Author"] = "unknown" // Optional header for additional file info
                    return headers
                }

                override fun getBody(): ByteArray {
                    return fileToUpload.readBytes()
                }

                override fun getBodyContentType(): String {
                    return mimeType
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    // Handle the response here if needed
                    return super.parseNetworkResponse(response)
                }
            }

            requestQueue.add(uploadRequest)
        } catch (e: IOException) {
            Log.e("Upload", "Error reading file: $e")
        }
    }

    fun uploadBitmapToB2Cloud(context: Context, uploadUrl: String, authorizationToken: String, bitmap: Bitmap, fileName: String) {
        Log.d("DownloadURLB2","HelloWorld1")
        val requestQueue = Volley.newRequestQueue(context)
        Log.d("DownloadURLB2","HelloWorld2")
        try {
            Log.d("DownloadURLB2","HelloWorld3")
            // Convert bitmap to byte array
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Log.d("DownloadURLB2","HelloWorld4")

            // Calculate SHA1 hash of the byte array
            val sha1OfFile = calculateSHA1(byteArray)
            Log.d("DownloadURLB2","HelloWorld5")
            // Get MIME type of the file (image/jpeg for JPEG images)
            val mimeType = "image/jpeg"


//            val fileName = "face_image.jpg"

            val uploadRequest = object : StringRequest(Method.POST, uploadUrl,
                Response.Listener<String> { response ->

                    Log.d("DownloadURLB2","HelloWorld6")
                    val jsonResponse = JSONObject(response)
                    Log.d("DownloadURLB2","HelloWorld7")
                    DOWNLOAD_FILE_ID_B2 = jsonResponse.getString("fileId")
                    DOWNLOAD_URL_B2 += DOWNLOAD_FILE_ID_B2
                    Log.d("DownloadURLB2","D_URL: $DOWNLOAD_URL_B2")

                    Log.d("DownloadURLB2", response.toString())
                    Log.d("DownloadURLB2", "File uploaded successfully: $response")
                },
                Response.ErrorListener { error ->
                    Log.e("DownloadURLB2", "Error uploading file: $error")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = authorizationToken
                    headers["X-Bz-File-Name"] = fileName
                    headers["Content-Type"] = mimeType
                    headers["X-Bz-Content-Sha1"] = sha1OfFile
                    headers["X-Bz-Info-Author"] = "unknown" // Optional header for additional file info
                    return headers
                }

                override fun getBody(): ByteArray {
                    return byteArray
                }

                override fun getBodyContentType(): String {
                    return mimeType
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    // Handle the response here if needed
                    return super.parseNetworkResponse(response)
                }
            }

            requestQueue.add(uploadRequest)
        } catch (e: IOException) {
            Log.e("Upload", "Error converting bitmap to byte array: $e")
        }
    }


//    fun downloadImageFromB2Cloud(context: Context, downloadUrl: String, authorizationToken: String, callback: ImageDownloadCallback) {
//        val header = authorizationToken
//        val requestQueue = Volley.newRequestQueue(context)
//
//        val downloadRequest = object: ByteArrayRequest(
//            Request.Method.GET, downloadUrl,
//            Response.Listener<ByteArray> { response ->
//                Log.d("responseLogB2", "Response:- $response")
//                // Handle the downloaded image response here
//                try {
//
//                    val bitmap = BitmapFactory.decodeByteArray(response, 0, response.size)
////                    val  byteArray = response.toByteArray()
////                    val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
//                    callback.onImageDownloaded(bitmap)// Notify callback when image downloaded successfully
//
//                    Log.d("DownloadB2", "Image downloaded successfully")
//                } catch (e: Exception) {
//                    Log.e("DownloadB2", "Error parsing image response from catch: $e")
//                }
//            },
//            Response.ErrorListener { error ->
//                callback.onImageDownloadError("Error parsing image response from error listener: $error")
//                Log.e("DownloadB2", "Error downloading image: $error")
//            }) {
//
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["Authorization"] = authorizationToken
//                return headers
//            }
//        }
//
//        requestQueue.add(downloadRequest)
//    }



    fun downloadImageFromB2Cloud(context: Context, downloadUrl: String, authorizationToken: String, callback: ImageDownloadCallback) {
        val requestQueue = Volley.newRequestQueue(context)

//        val url = "https://f005.backblazeb2.com/b2api/v1/b2_download_file_by_id?fileId=4_z24adb8d4335ac71681d00915_f110d276df59cc773_d20240220_m091801_c005_v0521006_t0012_u01708420681882"
//
//        val header = "4_0054d843a7610950000000002_01b28433_e55be4_acct_PkHKwYS_GH0pX6BbsqusgKUOKEc="

        val downloadRequest = object : ByteArrayRequest(
            Request.Method.GET, downloadUrl,
            Response.Listener<ByteArray> { response ->
                Log.d("responseLogB2", "Response:- $response")
                // Handle the downloaded image response here
                try {
//                    Log.d("CognitoB2",response)
                    val bitmap = BitmapFactory.decodeByteArray(response, 0, response.size)
//                    val  byteArray = response.toByteArray()
//                    val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                    callback.onImageDownloaded(bitmap)// Notify callback when image downloaded successfully

                    Log.d("DownloadB2", "Image downloaded successfully")
                } catch (e: Exception) {
                    Log.e("DownloadB2", "Error parsing image response from catch: $e")
                }
            },
            Response.ErrorListener { error ->
                callback.onImageDownloadError("Error parsing image response from error listener: $error")
                Log.e("DownloadB2", "Error downloading image: $error")
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = authorizationToken
                return headers
            }
        }

        requestQueue.add(downloadRequest)
    }




    @Throws(IOException::class)
    fun calculateSHA1(file: File): String {
        val inputStream = FileInputStream(file)
        val digest = MessageDigest.getInstance("SHA-1")
        val buffer = ByteArray(8192)
        var read: Int
        while (inputStream.read(buffer).also { read = it } > 0) {
            digest.update(buffer, 0, read)
        }
        val hash = digest.digest()
        return hash.joinToString("") { "%02x".format(it) }
    }

    @Throws(IOException::class)
    fun calculateSHA1(byteArray: ByteArray): String {
        try {
            val digest = MessageDigest.getInstance("SHA-1")
            val hash = digest.digest(byteArray)
            return hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("SHA1 Calculation", "Error calculating SHA1: ${e.message}")
            throw IOException("Error calculating SHA1", e)
        }
    }




    fun convertTimestamps(timestamps: List<String>): List<String> {
        val formattedTimestamps = mutableListOf<String>()
        val inputFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm:ss a", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm:ss a", Locale.getDefault())

        for (timestamp in timestamps) {
            try {
                val date = Date(timestamp.toLong())
                val formattedDate = outputFormat.format(date)
                formattedTimestamps.add(formattedDate)
            } catch (e: Exception) {
                // Handle invalid timestamps
                formattedTimestamps.add("Invalid timestamp")
            }
        }

        return formattedTimestamps
    }


}