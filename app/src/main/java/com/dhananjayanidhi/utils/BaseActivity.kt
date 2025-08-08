package com.dhananjayanidhi.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.SelectFileLayoutBinding
import com.dhananjayanidhi.utils.interfacef.UploadImageInterface
import com.dhananjayanidhi.utils.loader.ArcConfiguration
import com.dhananjayanidhi.utils.loader.SimpleArcDialog
import androidx.core.graphics.drawable.toDrawable

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener, UploadImageInterface {
    var mContext: Activity? = null

    //    private var isCameraClick: Boolean = false
    private var REQUEST_CODE = Constants.PICK_IMAGE_CAMERA
    private var mUploadImageInterface: UploadImageInterface? = null

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
        CommonFunction.hideKeyboardFrom(mContext!!, p0!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mUploadImageInterface = this
    }

    var mDialog: SimpleArcDialog? = null

    open fun showProgressDialog() {
        if (mDialog == null) {
            mDialog = SimpleArcDialog(mContext)
        }
        mDialog!!.setConfiguration(ArcConfiguration(mContext))
        mDialog!!.setCancelable(false)
        mDialog!!.show()
    }

    open fun hideProgressDialog() {
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showPictureDialog() {
        val dialog = Dialog(mContext!!, R.style.CustomAlertDialogStylePopup)
        if (dialog.window != null) {
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setGravity(Gravity.BOTTOM)
        }
        val binding: SelectFileLayoutBinding =
            SelectFileLayoutBinding.inflate(LayoutInflater.from(mContext), null, false)
        dialog.setContentView(binding.root)
        if (dialog.window != null) {
            dialog.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        binding.tvCameraSelectFile.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
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

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    mUploadImageInterface!!.onUploadImage(bitmap)
                }
            }
        }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val imageBitmap =
                    CommonFunction.getRealPathFromGallery(
                        RealFileUtils.newInstance(mContext!!)
                        !!.getPath(uri)
                    )
                mUploadImageInterface!!.onUploadImage(imageBitmap!!)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            mContext?.let { CommonFunction.hideKeyboardFrom(it, currentFocus!!) }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onUploadImage(imageUrl: Bitmap) {

    }
}