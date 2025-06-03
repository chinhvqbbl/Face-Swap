package com.demo.faceswap

import android.os.Bundle
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import okhttp3.*
import java.io.File
import java.io.IOException
import android.graphics.BitmapFactory
import android.content.Intent
import android.provider.MediaStore
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import android.app.ProgressDialog
import android.util.Log
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import android.graphics.drawable.ColorDrawable

class MainActivity : AppCompatActivity() {
    private lateinit var sourceImageView: ImageView
    private lateinit var targetImageView: ImageView
    private lateinit var resultImageView: ImageView
    private var sourceImageUri: Uri? = null
    private var targetImageUri: Uri? = null
    
    // Configure OkHttpClient with timeout
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // private val API_URL = "http://10.0.2.2:5000/swap-face"  // For emulator
    private val API_URL = "http://192.168.1.2:5000/swap-face"  // Replace x with your PC's IP last number
    
    private val TAG = "FaceSwapApp"
    
    private val getSourceImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            sourceImageUri = it
            sourceImageView.setImageURI(it)
        }
    }
    
    private val getTargetImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            targetImageUri = it
            targetImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        sourceImageView = findViewById(R.id.sourceImageView)
        targetImageView = findViewById(R.id.targetImageView)
        resultImageView = findViewById(R.id.resultImageView)
        
        // Set click listeners for image preview
        sourceImageView.setOnClickListener { showFullScreenImage(sourceImageView) }
        targetImageView.setOnClickListener { showFullScreenImage(targetImageView) }
        resultImageView.setOnClickListener { showFullScreenImage(resultImageView) }
        
        findViewById<Button>(R.id.btnSelectSource).setOnClickListener {
            getSourceImage.launch("image/*")
        }
        
        findViewById<Button>(R.id.btnSelectTarget).setOnClickListener {
            getTargetImage.launch("image/*")
        }
        
        findViewById<Button>(R.id.btnSwapFaces).setOnClickListener {
            if (sourceImageUri != null && targetImageUri != null) {
                swapFaces()
            } else {
                Toast.makeText(this, "Please select both images first", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showFullScreenImage(imageView: ImageView) {
        if (imageView.drawable == null) return

        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        // Create a new ImageView for the dialog
        val fullScreenImageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageDrawable(imageView.drawable)
            
            // Click to dismiss
            setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setContentView(fullScreenImageView)
        dialog.show()
    }

    private fun swapFaces() {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Processing images...")
            setCancelable(false)
            show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Starting face swap process")
                
                // Convert URIs to files
                Log.d(TAG, "Converting source URI to file: ${sourceImageUri}")
                val sourceFile = getFileFromUri(sourceImageUri!!)
                Log.d(TAG, "Source file created: ${sourceFile.absolutePath}, size: ${sourceFile.length()} bytes")
                
                Log.d(TAG, "Converting target URI to file: ${targetImageUri}")
                val targetFile = getFileFromUri(targetImageUri!!)
                Log.d(TAG, "Target file created: ${targetFile.absolutePath}, size: ${targetFile.length()} bytes")

                // Create multipart request
                Log.d(TAG, "Creating multipart request")
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "source_face",
                        "source.jpg",
                        sourceFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart(
                        "target_image",
                        "target.jpg",
                        targetFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .build()

                Log.d(TAG, "Sending request to: ${API_URL}")
                
                try {
                    client.newCall(request).execute().use { response ->
                        Log.d(TAG, "Response received: ${response.code}")
                        
                        if (!response.isSuccessful) {
                            val errorBody = response.body?.string() ?: "No error body"
                            Log.e(TAG, "Server error response: $errorBody")
                            throw IOException("Server error: ${response.code} - $errorBody")
                        }

                        // Convert response to bitmap and display
                        val responseBody = response.body?.bytes() ?: throw IOException("Empty response")
                        Log.d(TAG, "Response size: ${responseBody.size} bytes")
                        
                        val bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.size)
                        if (bitmap == null) {
                            Log.e(TAG, "Failed to decode response as bitmap")
                            throw IOException("Invalid image response")
                        }
                        
                        withContext(Dispatchers.Main) {
                            resultImageView.setImageBitmap(bitmap)
                            progressDialog.dismiss()
                            Toast.makeText(this@MainActivity, "Face swap completed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    Log.e(TAG, "Network timeout", e)
                    throw IOException("Network timeout - Check your connection")
                } catch (e: UnknownHostException) {
                    Log.e(TAG, "Cannot reach server", e)
                    throw IOException("Cannot reach server - Check server address and make sure it's running")
                } catch (e: IOException) {
                    Log.e(TAG, "Network error", e)
                    throw e
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Face swap failed", e)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    val errorMessage = when (e) {
                        is SecurityException -> "Permission denied to read images"
                        is IOException -> e.message ?: "Network error"
                        else -> "Error: ${e.message}"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        try {
            val inputStream = contentResolver.openInputStream(uri)
                ?: throw IOException("Cannot open input stream for URI: $uri")
            
            val file = File(cacheDir, "temp_${System.currentTimeMillis()}.jpg")
            Log.d(TAG, "Creating temporary file: ${file.absolutePath}")
            
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            if (!file.exists() || file.length() == 0L) {
                throw IOException("Failed to create file or file is empty")
            }
            
            return file
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to file", e)
            throw e
        }
    }
}