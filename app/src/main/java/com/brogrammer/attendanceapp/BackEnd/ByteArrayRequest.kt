package com.brogrammer.attendanceapp.BackEnd


import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

//class to fetch the image from the BackBlaze and then convert it into ByteArray

open class ByteArrayRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<ByteArray>,
    errorListener: Response.ErrorListener
) : Request<ByteArray>(method, url, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ByteArray) {
        listener.onResponse(response)
    }
}
