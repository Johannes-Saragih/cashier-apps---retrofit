package com.example.projectskripsi2.RESPONE

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Respone(
    @field:SerializedName("status") val status: String,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("order_id") val orderId: String,
    @field:SerializedName("token") val token: String,
    @field:SerializedName("products") val products: List<UploadRespone>
)

@Parcelize
data class UsernameResponse(
    @field:SerializedName("status") val status: String,
    @field:SerializedName("username") val username: String,
    @field:SerializedName("message") val message: String
):  Parcelable

data class Register(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class Login(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginRespone(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("username") val username: String
)

data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class ResponseOrder(
    @SerializedName("status") val status: String,
    @SerializedName("order") val order: Order
)

data class OrderTodayResponse(
    @SerializedName("status") val status: String,
    @SerializedName("orders") val orders: List<OrderToday>,
    @SerializedName("message") val message: String?
)

@Parcelize
data class OrderToday(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("time") val time: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("user_id") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("payment") val payment: Payment
) : Parcelable

@Parcelize
data class Order(
    @field:SerializedName("order_id") val orderId: String,
    @field:SerializedName("time") val time: String,
    @field:SerializedName("total_price") val totalPrice: Double,
    @field:SerializedName("user_id") val userId: String,
    @field:SerializedName("items") val items: List<OrderItem>,
    @field:SerializedName("payment") val payment: Payment?
):  Parcelable

@Parcelize
data class OrderItem(
    @field:SerializedName("product_id") val productId: String?,
    @field:SerializedName("product_name") val productName: String?,
    @field:SerializedName("qty") val qty: Int,
    @field:SerializedName("price") val price: Double
):  Parcelable

@Parcelize
data class Payment(
    @field:SerializedName("order_id") val orderId: String,
    @field:SerializedName("payment_method") val paymentMethod: String?,
    @field:SerializedName("payment_status") val paymentStatus: String?
):  Parcelable

data class PaymentRequest(
    @SerializedName("payment_type") val paymentType: String,
    @SerializedName("order_details") val orderDetails: OrderDetails
)

data class OrderDetails(
    @field:SerializedName("order_id") val orderId: String,
    @field:SerializedName("gross_amount") val grossAmount: Double
)

data class WebhookRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("transaction_status") val transactionStatus: String
)

data class ProductsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("products") val products: List<Product>
)

data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("name") val product: String,
    @SerializedName("price") val price: Int,
    @SerializedName("description") val description: String
)

data class Products(
    val product_id: String,
    val product_name: String,
    val price: Double
)



