package com.dhananjayanidhi.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dhananjayanidhi.utils.loader.ArcConfiguration
import com.dhananjayanidhi.utils.loader.SimpleArcDialog

abstract class BaseFragment : Fragment(), View.OnClickListener {
    var mContext: Activity? = null


    override fun onClick(p0: View?) {
        AppController.context?.let { CommonFunction.hideKeyboardFrom(it, p0!!) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
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

    override fun onResume() {
        super.onResume()
     }

    fun isConnectingToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
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
}