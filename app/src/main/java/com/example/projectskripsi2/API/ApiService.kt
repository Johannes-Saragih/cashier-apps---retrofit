package com.example.projectskripsi2.API

import com.example.projectskripsi2.RESPONE.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

        @Multipart
        @POST("upload")
        fun UploadImage(@Part file: MultipartBody.Part, @Part("user_id") userId: RequestBody): Call<Respone>

        @POST("charge")
        fun postPayment(@Body chargeRequest: PaymentRequest): Call<ApiResponse>

        @GET("user_id/{user_id}")
        fun getUsernameByUserId(@Path("user_id") userId: String): Call<UsernameResponse>

        @GET("order")
        fun getOrdersToday(): Call<OrderTodayResponse>

        @POST("login")
        fun login(@Body user: Login): Call<LoginRespone>

        @GET("order/{order_id}")
        fun getOrder(@Path("order_id") orderId: String): Call<ResponseOrder>

        @GET("products")
        fun getProducts(): Call<ProductsResponse>

        @POST("/products")
        fun insertProduct(@Body product: Products): Call<ApiResponse>
}