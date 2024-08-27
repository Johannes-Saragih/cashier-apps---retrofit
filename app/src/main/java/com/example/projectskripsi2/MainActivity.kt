package com.example.projectskripsi2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.lifecycle.Observer
import com.example.projectskripsi2.RESPONE.UploadRespone

class MainActivity : AppCompatActivity() {

    private lateinit var buttoncamera: CardView
    private lateinit var buttonupload: CardView
    private lateinit var buttonRiwayat: CardView
    private lateinit var buttonKeluar: CardView

    private lateinit var tvusername: TextView

    private lateinit var buttonSend: Button
    private lateinit var buttonReload: Button

    private lateinit var buttonpaham: Button
    private lateinit var buttonyes: Button
    private lateinit var buttonno: Button

    private lateinit var imageResult: ImageView

    private val requestcode = 123

    private val loadingbar : Long = 3000

    private val mainModel by viewModels<MainModel>()

    private lateinit var sharedPreferences: SharedPreferences
    private val WELCOME_DIALOG_INTERVAL = 10 * 60 * 1000L

    var userId =""
    var username =""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        tvusername = findViewById(R.id.tvusername)
        buttoncamera = findViewById(R.id.detectionbtn)
        buttonupload = findViewById(R.id.upload)

        username = intent.getStringExtra("username").toString()
        userId = intent.getStringExtra("userid").toString()



        tvusername.text = " Hi, Welcome "+ username +"!"

        if (shouldShowWelcomeDialog()) {
            showWelcomedialog()
        }

        buttoncamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ){
                takePicturePreview.launch(null)
            } else {
                requestPermissions.launch(android.Manifest.permission.CAMERA)
            }
        }

        buttonupload.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ){
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"

                val types = arrayOf("image/jpeg", "image/png", "image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, types)
                intent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                onresult.launch(intent)
            } else {
                requestPermissions.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showSuccessDialog(response: List<UploadRespone>, token : String, transactionid : String) {
        val intent = Intent(this, StrukActivity::class.java).apply {
            putParcelableArrayListExtra("upload", ArrayList(response))
            putExtra("token",token)
            putExtra("transactionid",transactionid)
            putExtra("username",username)
        }
        startActivity(intent)
    }

    private fun shouldShowWelcomeDialog(): Boolean {
        val lastShownTime = sharedPreferences.getLong("dialogwelcome", 0L)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastShownTime) >= WELCOME_DIALOG_INTERVAL
    }

    private fun setWelcomeDialogShown() {
        with(sharedPreferences.edit()) {
            putLong("dialogwelcome", System.currentTimeMillis())
            apply()
        }
    }

    private fun showWelcomedialog() {
        val dialoginformation = Dialog(this)
        val dialog = layoutInflater.inflate(R.layout.activity_usinginformation, null)

        buttonpaham= dialog.findViewById(R.id.btnpaham)

        buttonpaham.setOnClickListener {
            dialoginformation.dismiss()
            setWelcomeDialogShown()
        }

        dialoginformation.setContentView(dialog)
        dialoginformation.setCancelable(false)
        dialoginformation.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialoginformation.show()
    }

    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                showImageResultDialog(bitmap)
            }
        }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePicturePreview.launch(null)
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val onresult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i("TAG", "This is result")
            onResultReceived(requestcode, result)
        }

    private fun onResultReceived(request: Int, result: ActivityResult?) {
        when (request) {
            requestcode -> {
                if (result?.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        Log.i("Tag", "This is result ")
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        showImageResultDialog(bitmap)
                    }
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showImageResultDialog(bitmap: Bitmap?) {
        val myDialogResult = Dialog(this)
        val dialogBindingResult = layoutInflater.inflate(R.layout.activity_imageresult, null)

        buttonSend = dialogBindingResult.findViewById(R.id.send)
        buttonReload = dialogBindingResult.findViewById(R.id.reload)
        imageResult = dialogBindingResult.findViewById(R.id.tvimage)

        imageResult.setImageBitmap(bitmap)

        buttonSend.setOnClickListener {

            observeUploadProcess()

            val file = bitmapToFile(bitmap)

            if (file != null) {
                val reducedFile = reduceFileImage(file)
                val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file",
                    reducedFile.name,
                    requestImageFile
                )
                    mainModel.UploadImage(imageMultipart,userId)

            } else {
                Toast.makeText(this, "Gambar tidak tersedia, silahkan upload gambar", Toast.LENGTH_SHORT).show()
            }
        }
        buttonReload.setOnClickListener {
            myDialogResult.dismiss()
        }
        myDialogResult.setContentView(dialogBindingResult)
        myDialogResult.setCancelable(false)
        myDialogResult.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialogResult.show()
    }

    private fun observeUploadProcess() {
        mainModel.isSuccess.observe(this, Observer { success ->

            var res_token = ""
            var order_Id = ""

            val dialogBinding = layoutInflater.inflate(R.layout.splash,null)

            val myDialog = Dialog (this)

            myDialog.setContentView(dialogBinding)
            myDialog.setCancelable(false)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()

            Handler().postDelayed({
                if (success) {
                    mainModel.tokenId.observe(this , Observer {
                            token -> token?.let {
                        res_token = token
                    }
                    })
                    mainModel.orderId.observe(this , Observer {
                            orderId -> orderId?.let {
                        order_Id = orderId
                    }
                    })
                    mainModel.uploadResponse.observe(this, Observer { response ->
                        response?.let {
                            showSuccessDialog(it,res_token,order_Id)
                            myDialog.dismiss()
                        }
                    })
                }
                else {
                    Toast.makeText(this, "Detection Failed", Toast.LENGTH_SHORT).show()
                    myDialog.dismiss()
                }
            },loadingbar)
        })
    }

    private fun bitmapToFile(bitmap: Bitmap?): File? {
            val filesDir = applicationContext.filesDir
            val file = File(filesDir, "image.jpg")
            val bos = ByteArrayOutputStream()
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            }
            val bitmapData = bos.toByteArray()
            var fos: FileOutputStream? = null

            return try {
                fos = FileOutputStream(file)
                fos.write(bitmapData)
                fos.flush()
                file
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        private fun reduceFileImage(file: File): File {
            return file
        }
}