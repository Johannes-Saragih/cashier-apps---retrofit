package com.example.projectskripsi2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectskripsi2.ADAPTER.AdapterRecycleView
import java.text.SimpleDateFormat
import java.util.*
import  com.example.projectskripsi2.RESPONE.UploadRespone
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.corekit.models.*
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import kotlin.collections.ArrayList


class StrukActivity : AppCompatActivity(){

    private lateinit var Waktu: TextView
    private lateinit var totalpayment: TextView
    private lateinit var textpembayaran: TextView

    private lateinit var btntunai: Button
    private lateinit var btnnontunai: Button
    private lateinit var btnselesai: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRecycleView

    private var hasilpembayaran: String = ""

    lateinit var token: String
    lateinit var transactionid: String
    private var transactionResult = TransactionResult()

    private val loadingbar : Long = 3000

    companion object {
        private const val succes = "bb081ace-e24a-4019-8132-3c67278cfedd"
    }


    var userId = ""
    var total = 0.0
    var username = ""

    private val mainModel: MainModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_struk)

        Waktu = findViewById(R.id.date)

        btntunai = findViewById(R.id.btnsudahsesuai)
        btnnontunai = findViewById(R.id.btntidaksesuai)
        recyclerView = findViewById(R.id.recycleviewresult)
        totalpayment = findViewById(R.id.texttotalpembayaran)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterRecycleView(emptyList())
        recyclerView.adapter = adapter

        val myCalendar = Calendar.getInstance()
        Waktu.text = insertDate(myCalendar)

        initMidtransSDK()

        userId = intent.getStringExtra("id").toString()

        token = intent.getStringExtra("token").toString()
        transactionid = intent.getStringExtra("transactionid").toString()
        username = intent.getStringExtra("username").toString()

        val responses: List<UploadRespone>? = intent.getParcelableArrayListExtra("upload")
        responses?.let {
            adapter = AdapterRecycleView(it)
            recyclerView.adapter = adapter
            calculatetotal(it)
        }

        btntunai.setOnClickListener {
            responses?.let {
                goToPaymentTunai(it)
            }
        }

        btnnontunai.setOnClickListener {
            responses?.let {
                goToPaymentNonTunai(it)
            }
        }

        mainModel.isSuccess.observe(this, Observer { success ->
            if(success){
                Toast.makeText(this,"Pembayaran Berhasil",Toast.LENGTH_SHORT)
            }
            else{
                mainModel.error.observe(this, Observer { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                })
            }
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this,BarcodeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun insertDate(myCalendar: Calendar): String{
        val myformat = "dd MMMM yyyy , HH:mm:ss"
        val sdf = SimpleDateFormat(myformat, Locale.UK)
        return sdf.format(myCalendar.time)
    }

    private fun initMidtransSDK() {
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey("your-client-key")
            .setContext(applicationContext)
            .setMerchantBaseUrl("your-base-url")
            .setTransactionFinishedCallback(TransactionFinishedCallback {
                if (TransactionResult.STATUS_SUCCESS == "success") {
                    navigateToMainActivity()
                    Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                } else if (TransactionResult.STATUS_PENDING == "pending") {
                    Toast.makeText(this, "Pending transaction", Toast.LENGTH_LONG).show()
                } else if (TransactionResult.STATUS_FAILED == "failed") {
                    Toast.makeText(this, "Failed ${transactionResult.response.statusMessage}", Toast.LENGTH_LONG).show()
                } else if (transactionResult.status.equals(TransactionResult.STATUS_INVALID, true)
                ){
                    Toast.makeText(this, "Invalid transaction", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failure transaction", Toast.LENGTH_LONG).show()
                }
            })
            .enableLog(true)
            .setLanguage("id")
            .setColorTheme(CustomColorTheme("#FFE51255", "#FF000000", "#FFE51255"))
        sdkUIFlowBuilder.buildSDK()
    }



    fun goToPaymentNonTunai(responses :List<UploadRespone>) {

        hasilpembayaran = "Pembayaran Non-Tunai"

        var total = 0.0
        val itemDetails = ArrayList<ItemDetails>()

        for (response in responses) {
            total += response.price * response.total
            val detail = ItemDetails(
                "Toko Melva-$transactionid", response.price.toDouble(), response.total, "Nama Pembeli: $username"
            )
            itemDetails.add(detail)
        }

        mainModel.postPayment(hasilpembayaran, transactionid, total.toDouble())

        val transactionId = "Payment-Midtrans-${System.currentTimeMillis()}"
        val transactionRequest = TransactionRequest(transactionId, total)

        transactionRequest.itemDetails = itemDetails
        uiKitDetails(transactionRequest)

        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this@StrukActivity)
        MidtransSDK.getInstance().startPaymentUiFlow(this@StrukActivity,token)

        TransactionResult.STATUS_SUCCESS

    }

    @SuppressLint("MissingInflatedId")
    private fun goToPaymentTunai(responses :List<UploadRespone>) {
        hasilpembayaran = "Pembayaran Tunai"

        for (response in responses) {
            total = total + (response.price * response.total)
        }

            val myDialogResult = Dialog(this)
            val dialogBindingResult = layoutInflater.inflate(R.layout.activity_tunai, null)

            btnselesai = dialogBindingResult.findViewById(R.id.btnsudah)
            textpembayaran = dialogBindingResult.findViewById(R.id.textView2)
            textpembayaran.text = """
                Untuk menyelesaikan proses 
                transaksi, mohon berikan uang 
                tunai ke kasir sebesar ${formatToRupiah(total)}. 
                Kemudian, lakukan pemindaian barcode petugas kasir yang menerima uang Anda.
            """.trimIndent()

            btnselesai.setOnClickListener {

                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ){
                    startQRScanner()
                } else {
                    requestPermissions.launch(android.Manifest.permission.CAMERA)
                }
            }

            myDialogResult.setContentView(dialogBindingResult)
            myDialogResult.setCancelable(false)
            myDialogResult.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialogResult.show()

    }

    private fun startQRScanner() {
        IntentIntegrator(this).initiateScan()
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startQRScanner()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents == succes){

            val dialogBinding = layoutInflater.inflate(R.layout.splash, null)

            val myDialog = Dialog(this)

            myDialog.setContentView(dialogBinding)
            myDialog.setCancelable(false)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()

            Handler().postDelayed({
                val intent = Intent(this, SuccessTunaiActivity::class.java)
                startActivity(intent)
                mainModel.postPayment(hasilpembayaran, transactionid, total.toDouble())
                myDialog.dismiss()
            },loadingbar)
        } else {
            Toast.makeText(this,"Detection Failed",Toast.LENGTH_SHORT)
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun calculatetotal(responses: List<UploadRespone>): Double {

        for (response in responses) {
            total += response.price * response.total
        }
        totalpayment.text = formatToRupiah(total)
        return total
    }

    private fun handleTransactionResult(transactionResult: TransactionResult) {
        when {
            transactionResult.response != null -> {
                when (transactionResult.status) {
                    TransactionResult.STATUS_SUCCESS -> {
                        Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                    }
                    TransactionResult.STATUS_PENDING -> {
                        Toast.makeText(this, "Pending transaction", Toast.LENGTH_LONG).show()
                    }
                    TransactionResult.STATUS_FAILED -> {
                        Toast.makeText(this, "Failed ${transactionResult.response.statusMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            transactionResult.isTransactionCanceled -> {
                Toast.makeText(this, "Canceled transaction", Toast.LENGTH_LONG).show()
            }
            else -> {
                if (transactionResult.status.equals(TransactionResult.STATUS_INVALID, true))
                    Toast.makeText(this, "Invalid transaction", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Failure transaction", Toast.LENGTH_LONG).show()
            }
        }
    }
}
