package com.brogrammer.attendanceapp.BackEnd

private const val B2_UPLOAD_URL = "http://192.168.1.13/B2UploadURLTwo.php" // Replace with your localhost URL

private var DOWNLOAD_FILE_ID_B2: String? = null
private var DOWNLOAD_URL_B2: String? = "https://f005.backblazeb2.com/b2api/v1/b2_download_file_by_id?fileId="

private var UPLOAD_URL_B2: String? = null
private var UPLOAD_TOKEN_B2: String? = null






object BackBlaze {


//    fun uploadImageToB2Cloud(context: Context, uploadUrl: String, authorizationToken: String, fileToUpload: File) {
//        val requestQueue = Volley.newRequestQueue(context)
//
//        try {
//            // Calculate SHA1 hash of the file
//            val sha1OfFile = DataManager.calculateSHA1(fileToUpload)
//
//            // Get MIME type of the file (automatically determined by Backblaze B2)
//            val mimeType = "image/jpeg"
//
//            // Get file name
//            val fileName = fileToUpload.name
//
//            val uploadRequest = object : StringRequest(Method.POST, uploadUrl,
//                Response.Listener<String> { response ->
//
//                    val jsonResponse = JSONObject(response)
//                    DOWNLOAD_FILE_ID_B2 = jsonResponse.getString("fileId")
//                    DOWNLOAD_URL_B2 += DOWNLOAD_FILE_ID_B2
//                    Log.d("DownloadURLB2","D_URL: $DOWNLOAD_URL_B2")
//
//                    Log.d("ResponseB2",response.toString())
//                    Log.d("Upload", "File uploaded successfully: $response")
//                },
//                Response.ErrorListener { error ->
//                    Log.e("Upload", "Error uploading file: $error")
//                }) {
//                override fun getHeaders(): MutableMap<String, String> {
//                    val headers = HashMap<String, String>()
//                    headers["Authorization"] = authorizationToken
//                    headers["X-Bz-File-Name"] = fileName
//                    headers["Content-Type"] = mimeType
//                    headers["X-Bz-Content-Sha1"] = sha1OfFile
//                    headers["X-Bz-Info-Author"] = "unknown" // Optional header for additional file info
//                    return headers
//                }
//
//                override fun getBody(): ByteArray {
//                    return fileToUpload.readBytes()
//                }
//
//                override fun getBodyContentType(): String {
//                    return mimeType
//                }
//
//                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
//                    // Handle the response here if needed
//                    return super.parseNetworkResponse(response)
//                }
//            }
//
//            requestQueue.add(uploadRequest)
//        } catch (e: IOException) {
//            Log.e("Upload", "Error reading file: $e")
//        }
//    }


//    fun getUploadURLB2Cloud(context: Context, authToken: String?, apiUrl: String?, callback: UploadUrlCallback) {
//
////        Log.d("uploadURLToken", "appUrl: $apiUrl, \n authToken: $authToken")
//
//        val requestQueue = Volley.newRequestQueue(context)
//
////        Log.d("uploadURLToken", "appUrl: $apiUrl, authToken: $authToken")
//
//        val stringRequest = object : StringRequest(Method.POST, B2_UPLOAD_URL,
//            Response.Listener { response ->
//                // Handle the response here
//                try {
////                    Log.d("uploadURLToken", "appUrl: $apiUrl, authToken: $authToken")
////                    Log.d("uploadURLToken", "respons: $response")
//
//
//
//                    val jsonResponse = JSONObject(response)
////                    Log.d("uploadURLToken", "respons: $jsonResponse")
//                    val status = jsonResponse.getInt("tp")
//
//                    if(status == 1){
////                        val result = jsonResponse.getJSONObject("response")
//                        val token = jsonResponse.getString("token")
//
//                        //upload URL
//                        val url = jsonResponse.getString("url")
//                        // Now you have the token and URL, you can proceed with uploading your file
//                        UPLOAD_URL_B2 = url
//                        UPLOAD_TOKEN_B2 = token
//
//                        callback.onUploadUrlReceived(
//                            UPLOAD_URL_B2,
//                            UPLOAD_TOKEN_B2
//                        )
//
//                        Log.d("UploadURLLogg", UPLOAD_URL_B2.toString())
//
//                        Log.d("uploadURLToken", "Token: $token, URL: $url")
//                    }else{
//                        //Handle error case
//                        Log.e("uploadURLToken","Failed to get upload URL: ${jsonResponse.getString("errcode")}")
//                    }
//
//                } catch (e: JSONException) {
//                    Log.e("uploadURLToken", "Error parsing JSON: ${e.message}")
//                }
//            },
//            Response.ErrorListener { error ->
//                // Handle errors here
//                Log.e("uploadB2Cloud", "Error in request: $error")
//            }) {
//            // Override the getParams() method to pass parameters
//            override fun getParams(): MutableMap<String, String> {
//                val params = HashMap<String, String>()
//                params["authToken"] = authToken ?: ""
//                params["apiUrl"] = apiUrl ?: ""
//                return params
//            }
//        }
//
//        // Add the request to the request queue
//        requestQueue.add(stringRequest)
//    }


//    fun uploadImageToB2Cloud(context: Context, uploadUrl: String, authorizationToken: String, bitmap: Bitmap) {
//        val requestQueue = Volley.newRequestQueue(context)
//
//        try {
//            // Convert bitmap to byte array
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//            val byteArray = byteArrayOutputStream.toByteArray()
//
//            // Calculate SHA1 hash of the byte array
//            val sha1OfFile = calculateSHA1(byteArray)
//
//            // Get MIME type of the file (image/jpeg for JPEG images)
//            val mimeType = "image/jpeg"
//
//            val uploadRequest = object : StringRequest(Method.POST, uploadUrl,
//                Response.Listener<String> { response ->
//
//                    val jsonResponse = JSONObject(response)
//                    DOWNLOAD_FILE_ID_B2 = jsonResponse.getString("fileId")
//                    DOWNLOAD_URL_B2 += DOWNLOAD_FILE_ID_B2
//                    Log.d("DownloadURLB2","D_URL: $DOWNLOAD_URL_B2")
//
//                    Log.d("ResponseB2", response.toString())
//                    Log.d("Upload", "File uploaded successfully: $response")
//                },
//                Response.ErrorListener { error ->
//                    Log.e("Upload", "Error uploading file: $error")
//                }) {
//                override fun getHeaders(): MutableMap<String, String> {
//                    val headers = HashMap<String, String>()
//                    headers["Authorization"] = authorizationToken
//                    headers["Content-Type"] = mimeType
//                    headers["X-Bz-Content-Sha1"] = sha1OfFile
//                    headers["X-Bz-Info-Author"] = "unknown" // Optional header for additional file info
//                    return headers
//                }
//
//                override fun getBody(): ByteArray {
//                    return byteArray
//                }
//
//                override fun getBodyContentType(): String {
//                    return mimeType
//                }
//
//                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
//                    // Handle the response here if needed
//                    return super.parseNetworkResponse(response)
//                }
//            }
//
//            requestQueue.add(uploadRequest)
//        } catch (e: IOException) {
//            Log.e("Upload", "Error converting bitmap to byte array: $e")
//        }
//    }

//    @Throws(IOException::class)
//    fun calculateSHA1(byteArray: ByteArray): String {
//        try {
//            val digest = MessageDigest.getInstance("SHA-1")
//            val hash = digest.digest(byteArray)
//            return hash.joinToString("") { "%02x".format(it) }
//        } catch (e: Exception) {
//            Log.e("SHA1 Calculation", "Error calculating SHA1: ${e.message}")
//            throw IOException("Error calculating SHA1", e)
//        }
//    }

}