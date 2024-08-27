package com.example.projectskripsi2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectskripsi2.API.ApiConfig
import com.example.projectskripsi2.RESPONE.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

import retrofit2.Call
import retrofit2.Callback


class MainModel: ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val mToast = MutableLiveData<String>()
    val toast: LiveData<String> get() = mToast

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _tokenId = MutableLiveData<String>()
    val tokenId: LiveData<String> get() = _tokenId

    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String> get() = _orderId

    private val _uploadResponse = MutableLiveData<List<UploadRespone>>()
    val uploadResponse: LiveData<List<UploadRespone>> get() = _uploadResponse

    private val _ordersToday = MutableLiveData<List<OrderToday>>()
    val ordersToday: LiveData<List<OrderToday>> get() = _ordersToday

    private val _orderDetail = MutableLiveData<Order>()
    val orderDetail: LiveData<Order> get() = _orderDetail

    fun loginUser(email: String, password: String) {
        val apiService = ApiConfig.getApiService()
        val user = Login(email, password)
        val loginUserRequest = apiService.login(user)

        loginUserRequest.enqueue(object : retrofit2.Callback<LoginRespone> {
            override fun onResponse(
                call: retrofit2.Call<LoginRespone>,
                response: retrofit2.Response<LoginRespone>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        _isSuccess.value = true
                        _message.value = it.message
                    } ?: run {
                        _isSuccess.value = false
                        mToast.value = "Akun tidak terdaftar, silahkan lakukan registrasi."
                    }
                    Log.d("MainModel", "Login berhasil: ${response.body()}")
                } else {
                    mToast.value = response.message()
                    Log.e("Error:", "$response")
                }
            }

            override fun onFailure(call: Call<LoginRespone>, t: Throwable) {
                Log.e("Error: ", "${t.message}")
            }
        })
    }

    fun UploadImage(imageMultipart: MultipartBody.Part, userId: String) {
        val apiService = ApiConfig.getApiService()
        val userIdRequestBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val uploadImageRequest = apiService.UploadImage(imageMultipart, userIdRequestBody)

        uploadImageRequest.enqueue(object : retrofit2.Callback<Respone> {
            override fun onResponse(
                call: retrofit2.Call<Respone>,
                response: retrofit2.Response<Respone>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        mToast.value = it.status.toString()
                        _uploadResponse.value = it.products
                        _tokenId.value = it.token
                        _orderId.value = it.orderId
                        _isSuccess.value = true
                    } ?: run {
                        _isSuccess.value = false
                    }
                } else {
                    mToast.value = response.message()
                    _isSuccess.value = false
                    Log.e("Error:", "$response")
                }
            }

            override fun onFailure(call: Call<Respone>, t: Throwable) {
                mToast.value = t.message
                Log.e("Error: ", "${t.message}")
            }
        })
    }

    fun getUsernameByUserId(userId: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getUsernameByUserId(userId)
        call.enqueue(object : retrofit2.Callback<UsernameResponse> {
            override fun onResponse(call: retrofit2.Call<UsernameResponse>, response: retrofit2.Response<UsernameResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody.let {
                        if (it != null) {
                            _username.value = it.username
                        }
                        _isSuccess.value = true
                    } ?: run {
                        _isSuccess.value = false
                    }
                } else {
                    mToast.value = response.message()
                    Log.e("Error:", "$response")
                }
            }

            override fun onFailure(call: Call<UsernameResponse>, t: Throwable) {
                Log.e("Error: ", "${t.message}")
            }
        })
    }

    fun postPayment(paymentType: String, orderId: String, grossAmount: Double) {
        val apiService = ApiConfig.getApiService()
        val orderDetails = OrderDetails(orderId, grossAmount)
        val paymentRequest = PaymentRequest(paymentType, orderDetails)
        val call = apiService.postPayment(paymentRequest)

        call.enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                            _isSuccess.value = true
                            _message.value = it.message
                        } ?: run {
                        _isSuccess.value = false
                        _error.value = "Response body is null"
                    }
                } else {
                    _isSuccess.value = false
                    _error.value = "Failed to process payment: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _isSuccess.value = false
                _error.value = t.message
            }
        })
    }

    fun getOrderDetails(orderId: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getOrder(orderId)
        call.enqueue(object : retrofit2.Callback<ResponseOrder> {
            override fun onResponse(call: retrofit2.Call<ResponseOrder>, response: retrofit2.Response<ResponseOrder>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        _orderDetail.value = it.order
                        _isSuccess.value = true
                    } ?: run {
                        _isSuccess.value = false
                        _error.value = "Response body is null"
                    }
                } else {
                    _isSuccess.value = false
                    _error.value = "Failed to fetch order details: ${response.message()}"
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseOrder>, t: Throwable) {
                _isSuccess.value = false
                _error.value = t.message
            }
        })
    }

    fun uploafile(productId: String, productName: String, productPrice: String){
        val apiservice = ApiConfig.getApiService()
        val data = Products(productId,productName,productPrice.toDouble())
        val call = apiservice.insertProduct(data)
        call.enqueue(object : Callback<ApiResponse>{
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>){
                if (response.isSuccessful){
                    val uploadResponse = response.body()
                    uploadResponse?.let {
                        _isSuccess.value = true
                    } ?: run {
                        _isSuccess.value = false
                    }
                } else {
                    mToast.value = response.message()
                    Log.e("Error:", "$response")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Error: ", "${t.message}")
            }
        })
    }

    fun getOrdersToday() {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getOrdersToday()
        call.enqueue(object : retrofit2.Callback<OrderTodayResponse> {
            override fun onResponse(call: retrofit2.Call<OrderTodayResponse>, response: retrofit2.Response<OrderTodayResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        _ordersToday.value = it.orders
                        _isSuccess.value = true
                    } ?: run {
                        _isSuccess.value = false
                        _error.value = "Response body is null"
                    }
                } else {
                    _isSuccess.value = false
                    _error.value = "Failed to fetch today's orders: ${response.message()}"
                }
            }

            override fun onFailure(call: retrofit2.Call<OrderTodayResponse>, t: Throwable) {
                _isSuccess.value = false
                _error.value = t.message
            }
        })
    }
}

