package com.example.projectskripsi2.RESPONE
//import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadRespone(
    @field:SerializedName("product_id")
    val id: String,

    @field:SerializedName("product")
    val name: String,

    @field:SerializedName("price")
    val price: Double,

    @field:SerializedName("total")
    val total: Int,

) :  Parcelable