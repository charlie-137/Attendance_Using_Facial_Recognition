package com.example.imageclassificationlivefeed.UserInterface

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel() {

    val itemList = MutableLiveData<ArrayList<ItemModel>>()

    // Add MutableLiveData for Bitmap
    val bitmapLiveData = MutableLiveData<Bitmap?>()

    fun setBitmap(bitmap: Bitmap?) {
        bitmapLiveData.value = bitmap
    }

    fun getBitmap(): LiveData<Bitmap?> {
        return bitmapLiveData
    }
}