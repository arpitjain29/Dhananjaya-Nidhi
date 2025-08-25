package com.dhananjayanidhi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dhananjayanidhi.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


object CommonFunction {

    /*Common date format*/
    var MMM_yyyy = SimpleDateFormat("MMM, yyyy")
    var yyyy_MM_dd = SimpleDateFormat("yyyy-MM-dd")
    var dd_MMM_yyyy = SimpleDateFormat("dd MMM yyyy")
    var MMM_dd_yyyy = SimpleDateFormat("MMM dd yyyy")
    var dd_MM_yyyy = SimpleDateFormat("dd-MM-yyyy")
    val _24HourSDF = SimpleDateFormat("HH:mm:ss")
    val _12HourSDF = SimpleDateFormat("hh:mm a")
    val yyyy_mm_dd_z = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val yyyy_mm_dd_hh_mm_ss = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val month_date = SimpleDateFormat("MMM")
    val mmmm_name = SimpleDateFormat("MMMM")
    var dd = SimpleDateFormat("dd")
    var yyyy_mm_dd: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    var EEE = SimpleDateFormat("EEE")
    var numberFormate: DecimalFormat = DecimalFormat("#,###,###")

    /*Screen size*/
    fun screenSize(mActivity: Activity): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mActivity.display!!.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            mActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics
    }

    /*hide keyboard*/
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm =
            (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun changeDateFormatFromAnother(date: String?): String {
        @SuppressLint("SimpleDateFormat")
        val inputFormat: DateFormat =
            SimpleDateFormat("yyyy-MM-dd")
        @SuppressLint("SimpleDateFormat")
        val outputFormat: DateFormat =
            SimpleDateFormat("dd MMM yyyy")
        var resultDate = ""
        try {
            resultDate = outputFormat.format(inputFormat.parse(date))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return resultDate
    }

    /**
     * Validate email with regular expression
     *
     * @param email password for validation
     * @return true valid email, false invalid email
     */
    fun emailValidator(email: String?): Boolean {
        val pattern: Pattern
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher: Matcher = pattern.matcher(email!!)
        return !matcher.matches()
    }


    /**
     * Show single toast when multiple click
     *
     * @param context activity
     * @param text    message
     */
    @SuppressLint("ShowToast")
    fun showToastSingle(context: Context?, text: String?, toastType: Int) {
        if (!TextUtils.isEmpty(text)) {
            try {
                /*when (toastType) {
                    Constant.ToastMethod.SUCCESS -> {
                        Toasty.success(context!!, text!!, Toast.LENGTH_SHORT, true).show()
                    }
                    Constant.ToastMethod.ERROR -> {
                        Toasty.error(context!!, text!!, Toast.LENGTH_SHORT, true).show()
                    }
                    else -> {
                        Toasty.normal(context!!, text!!, Toast.LENGTH_SHORT).show()
                    }
                }*/
                Toast.makeText(context,text,Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getRealPathFromGallery(path: String?): Bitmap? {
        var bm: Bitmap? = null
        val file = File(path!!)
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        try {
            var fis = FileInputStream(file)
            BitmapFactory.decodeStream(fis, null, o)
            fis.close()
            // The new size we want to scale to
            val REQUIRED_SIZE = 1000

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            fis = FileInputStream(file)
            bm = BitmapFactory.decodeStream(fis, null, o2)
            fis.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bm

    }

    fun persistImage(bitmap: Bitmap, mContext: Context): File {
        val filesDir: File = mContext.cacheDir
        val imageFile = File(filesDir, System.currentTimeMillis().toString() + ".jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, os)
            os.flush()
            os.close()
        } catch (e: java.lang.Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile
    }

    fun getDeviceId(context: Context): String {
        val id: String = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return id
    }

    fun loadImageViaGlide(
        context: Context?,
        url: String,
        imageView: ImageView?,
        errorDrawable: Int
    ) {
        Log.i("Url", "_________$url")
        Glide.with(context!!)
            .load(url)
            .apply(
                RequestOptions()
                    .skipMemoryCache(false)
                    .placeholder(errorDrawable)
                    .error(errorDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            ).into(imageView!!)
    }

    fun urlToDrawable(
        context: Context,
        url: String,
    ) : Drawable? {
        var drawable : Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_app_image)
        Glide.with(context).load(url).apply(RequestOptions().fitCenter()).into(
            object : CustomTarget<Drawable>(50,50){
                override fun onLoadCleared(placeholder: Drawable?) {
                    drawable = placeholder
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    drawable = resource
                }
            }
        )
        return drawable
    }

}