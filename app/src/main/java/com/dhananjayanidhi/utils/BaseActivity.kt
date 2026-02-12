package com.dhananjayanidhi.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import android.content.res.Configuration
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.SelectFileLayoutBinding
import com.dhananjayanidhi.utils.interfacef.UploadImageInterface
import com.dhananjayanidhi.utils.loader.ArcConfiguration
import com.dhananjayanidhi.utils.loader.SimpleArcDialog
import com.yalantis.ucrop.UCrop
import androidx.core.graphics.drawable.toDrawable
import java.io.File

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener, UploadImageInterface {
    protected var mContext: Activity? = null

    private var REQUEST_CODE = Constants.PICK_IMAGE_CAMERA
    private var mUploadImageInterface: UploadImageInterface? = null
    private var cameraImageUri: Uri? = null

    fun isConnectingToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    override fun onClick(p0: View?) {
        p0?.let { view ->
            mContext?.let { context ->
                CommonFunction.hideKeyboardFrom(context, view)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force light mode - disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        mContext = this
        mUploadImageInterface = this
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Force light mode even if system changes
        if (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Ensure status bar is configured after edge-to-edge
        // Use post to ensure it runs after layout
        window?.decorView?.post {
            configureStatusBar()
        }
    }
    
    /**
     * Configure status bar with proper icon visibility.
     * Automatically determines icon color based on background color luminance.
     * Call this after enableEdgeToEdge() in activity's onCreate.
     * 
     * @param statusBarColor The color for status bar background (default: white)
     */
    protected fun configureStatusBar(statusBarColor: Int = getColor(R.color.white)) {
        window?.let { window ->
            // CRITICAL: Ensure status bar is visible and drawn first
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Clear any flags that might hide the status bar
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                
                // Ensure system bar backgrounds are drawn (CRITICAL for edge-to-edge)
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            
            // Set status bar background color explicitly to white (opaque)
            // Use Color.WHITE to ensure it's fully opaque, not transparent
            window.statusBarColor = Color.WHITE
            
            // Configure status bar icon appearance using WindowInsetsController
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            
            // For white background: use dark icons (isAppearanceLightStatusBars = false)
            // This makes icons dark/black so they're visible on white background
            // IMPORTANT: false = dark icons, true = light icons
            windowInsetsController?.isAppearanceLightStatusBars = true
            
            // Force the window to update
            window.decorView.requestLayout()
        }
    }
    
    /**
     * Determines if a color is light (bright) or dark.
     * Uses luminance calculation to determine appropriate icon color.
     * 
     * @param color The color to check
     * @return true if color is light, false if dark
     */
    private fun isColorLight(color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        
        // Calculate relative luminance (perceived brightness)
        // Formula: 0.299*R + 0.587*G + 0.114*B
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0
        
        // If luminance > 0.5, color is considered light (use dark icons)
        return luminance > 0.5
    }

    private var mDialog: SimpleArcDialog? = null

    open fun showProgressDialog() {
        if (mDialog == null && mContext != null) {
            mDialog = SimpleArcDialog(mContext)
        }
        mDialog?.let { dialog ->
            mContext?.let { context ->
                dialog.setConfiguration(ArcConfiguration(context))
                dialog.setCancelable(false)
                if (!dialog.isShowing) {
                    dialog.show()
                }
            }
        }
    }

    open fun hideProgressDialog() {
        mDialog?.dismiss()
        mDialog = null
    }

    override fun onDestroy() {
        hideProgressDialog()
        mContext = null
        mUploadImageInterface = null
        super.onDestroy()
    }

    fun showPictureDialog() {
        val context = mContext ?: return
        val dialog = Dialog(context, R.style.CustomAlertDialogStylePopup)
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(Gravity.BOTTOM)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        val binding: SelectFileLayoutBinding =
            SelectFileLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        dialog.setContentView(binding.root)

        binding.tvCameraSelectFile.setOnClickListener {
            mContext?.let { ctx ->
                // Create a file URI for full-resolution camera capture
                val imageFile = File(ctx.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                cameraImageUri = FileProvider.getUriForFile(
                    ctx,
                    "${ctx.packageName}.provider",
                    imageFile
                )
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                }
                resultLauncher.launch(cameraIntent)
            }
            dialog.dismiss()
        }
        binding.tvGallerySelectFile.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            dialog.dismiss()
        }
        binding.tvCancelSelectFile.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private val cropResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val croppedUri = UCrop.getOutput(result.data!!)
                croppedUri?.let { uri ->
                    mContext?.let { context ->
                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            bitmap?.let {
                                mUploadImageInterface?.onUploadImage(it)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("BaseActivity", "Error loading cropped image", e)
                        }
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR && result.data != null) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
                Log.e("BaseActivity", "Crop error: ${cropError?.message}")
            }
        }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
        }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { selectedUri ->
                Log.d("PhotoPicker", "Selected URI: $selectedUri")
                startCrop(selectedUri)
            } ?: run {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun startCrop(sourceUri: Uri) {
        mContext?.let { context ->
            val destinationUri = CommonFunction.getOutputUri(context)
            val uCrop = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1000, 1000)

            cropResultLauncher.launch(uCrop.getIntent(context))
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        currentFocus?.let { view ->
            mContext?.let { context ->
                CommonFunction.hideKeyboardFrom(context, view)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onUploadImage(imageUrl: Bitmap) {

    }
}