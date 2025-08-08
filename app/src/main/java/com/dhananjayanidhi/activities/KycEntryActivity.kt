package com.dhananjayanidhi.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityKycEntryBinding
import com.dhananjayanidhi.databinding.SelectFileLayoutBinding
import com.dhananjayanidhi.models.kycentry.KycEntryModel
import com.dhananjayanidhi.parameters.KycEntryParams
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.RealFileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable

class KycEntryActivity : BaseActivity() {
    private var kycEntryBinding: ActivityKycEntryBinding? = null
    private var selectValueImage: String? = null
    private var addCustomerId: String? = null
    private var selectAadharCardFrontImage: Bitmap? = null
    private var selectAadharCardBackImage: Bitmap? = null
    private var selectPanCardImage: Bitmap? = null
    private var selectCustomerImage: Bitmap? = null
    private var selectSignatureImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        kycEntryBinding = ActivityKycEntryBinding.inflate(layoutInflater)
        setContentView(kycEntryBinding!!.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply insets as margins instead of padding
            val layoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            v.layoutParams = layoutParams

            insets
        }
        kycEntryBinding!!.appLayout.ivMenu.visibility = View.GONE
        kycEntryBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        kycEntryBinding!!.appLayout.ivSearch.visibility = View.GONE
        kycEntryBinding!!.appLayout.tvTitle.text = getString(R.string.kyc_entry)

        addCustomerId = intent.getStringExtra(Constants.customerIdGet)

        kycEntryBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        kycEntryBinding!!.btnUploadKycEntry.setOnClickListener {
            val kycEntryParams = KycEntryParams()
            kycEntryParams.customerId = addCustomerId
            kycEntryParams.aadharNumber =
                kycEntryBinding?.etAddharNumberKycEntry?.text.toString().trim()
            kycEntryParams.panNumber = kycEntryBinding?.etPanCardKycEntry?.text.toString().trim()
            kycEntryParams.aadharFrontImage = selectAadharCardFrontImage.toString()
            kycEntryParams.aadharBackImage = selectAadharCardBackImage.toString()
            kycEntryParams.panImage = selectPanCardImage.toString()
            kycEntryParams.customerPicture = selectCustomerImage.toString()
            kycEntryParams.signature = selectSignatureImage.toString()

            if (TextUtils.isEmpty(kycEntryParams.aadharNumber)) {
                kycEntryBinding?.etAddharNumberKycEntry?.error =
                    getString(R.string.please_enter_aadhar_card_no)
            } else if (TextUtils.isEmpty(kycEntryParams.panNumber)) {
                kycEntryBinding?.etPanCardKycEntry?.error =
                    getString(R.string.please_enter_pan_card_no)
            } else if (TextUtils.isEmpty(kycEntryParams.aadharFrontImage)) {
                CommonFunction.showToastSingle(
                    mContext!!, getString(R.string.please_click_aadhar_card_front_image),
                    0
                )
            } else if (TextUtils.isEmpty(kycEntryParams.aadharBackImage)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_aadhar_card_back_image),
                    0
                )
            } else if (TextUtils.isEmpty(kycEntryParams.panImage)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_pan_card_image),
                    0
                )
            } else if (TextUtils.isEmpty(kycEntryParams.customerPicture)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_aadhar_customer_image),
                    0
                )
            } else if (TextUtils.isEmpty(kycEntryParams.signature)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_signature_image),
                    0
                )
            } else {
                kycEntryApi(kycEntryParams)
            }
//            startActivity(Intent(mContext, AddressEntryActivity::class.java))
        }

        kycEntryBinding!!.llUploadPanCard.setOnClickListener {
            selectPictureDialog("1")
        }

        kycEntryBinding!!.llUploadAadharCardFront.setOnClickListener {
            selectPictureDialog("2")
        }

        kycEntryBinding!!.llUploadAadharCardBack.setOnClickListener {
            selectPictureDialog("3")
        }

        kycEntryBinding!!.llUploadCustomerImage.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultCustomerImageLauncher.launch(cameraIntent)
        }

        kycEntryBinding!!.llUploadSignature.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultImageLauncher.launch(cameraIntent)
        }
    }

    private fun kycEntryApi(kycEntryParams: KycEntryParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val coverImageFileFront =
                selectAadharCardFrontImage?.let { CommonFunction.persistImage(it, mContext!!) }
            val requestFileFront =
                coverImageFileFront?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsAadharFront = requestFileFront?.let {
                MultipartBody.Part.createFormData(
                    Constants.aadhar_front_image, coverImageFileFront.name,
                    it
                )
            }

            val coverImageFileBack =
                selectAadharCardBackImage?.let { CommonFunction.persistImage(it, mContext!!) }
            val requestFileBack =
                coverImageFileBack?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsAadharBack = requestFileBack?.let {
                MultipartBody.Part.createFormData(
                    Constants.aadhar_back_image, coverImageFileBack.name,
                    it
                )
            }

            val coverImageFilePanCard =
                selectPanCardImage?.let { CommonFunction.persistImage(it, mContext!!) }
            val requestFilePanCard =
                coverImageFilePanCard?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsPanCard = requestFilePanCard?.let {
                MultipartBody.Part.createFormData(
                    Constants.pan_image, coverImageFilePanCard.name,
                    it
                )
            }

            val coverImageFileCustomer =
                selectCustomerImage?.let { CommonFunction.persistImage(it, mContext!!) }
            val requestFileCustomer =
                coverImageFileCustomer?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsCustomer = requestFileCustomer?.let {
                MultipartBody.Part.createFormData(
                    Constants.customer_picture, coverImageFileCustomer.name,
                    it
                )
            }

            val coverImageFileSignature =
                selectPanCardImage?.let { CommonFunction.persistImage(it, mContext!!) }
            val requestFileSignature =
                coverImageFileSignature?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsSignature = requestFileSignature?.let {
                MultipartBody.Part.createFormData(
                    Constants.signature, coverImageFileSignature.name,
                    it
                )
            }

            val partMap: MutableMap<String, RequestBody> = HashMap()
            partMap[Constants.customer_id] =
                addCustomerId!!.toRequestBody(MultipartBody.FORM)
            partMap[Constants.aadhar_number] =
                kycEntryParams.aadharNumber!!.toRequestBody(MultipartBody.FORM)
            partMap[Constants.pan_number] =
                kycEntryParams.panNumber!!.toRequestBody(MultipartBody.FORM)

            val call = ApiClient.buildService(mContext)
                .kycEntryApi(
                    mediaFilePartsAadharFront,
                    mediaFilePartsAadharBack,
                    mediaFilePartsPanCard,
                    mediaFilePartsCustomer,
                    mediaFilePartsSignature,
                    partMap
                )
            call?.enqueue(object : Callback<KycEntryModel?> {
                override fun onResponse(
                    call: Call<KycEntryModel?>, response: Response<KycEntryModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val kycEntryModel: KycEntryModel? = response.body()
                        if (kycEntryModel != null) {
                            if (kycEntryModel.success == true) {
                                startActivity(
                                    Intent(
                                        mContext!!,
                                        AccountOpenActivity::class.java
                                    ).putExtra(Constants.customerIdGet, addCustomerId)
                                )
                            } else {
                                CommonFunction.showToastSingle(mContext!!, kycEntryModel.message, 0)
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorJson = JSONObject(errorBody)
                                val errorArray = errorJson.getJSONArray("error")
                                val errorMessage = errorArray.getJSONObject(0).getString("message")
                                CommonFunction.showToastSingle(mContext, errorMessage, 0)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                CommonFunction.showToastSingle(
                                    mContext,
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<KycEntryModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                    if (throwable is HttpException) {
                        throwable.printStackTrace()
                    }
                }
            })
        } else {
            CommonFunction.showToastSingle(
                mContext, resources.getString(R.string.net_connection), 0
            )
        }
    }

//    override fun onUploadImage(imageUrl: Bitmap) {
//        super.onUploadImage(imageUrl)
//        kycEntryBinding!!.tvPanCardUpload.visibility = View.GONE
//        kycEntryBinding!!.ivPanCardImage.visibility = View.VISIBLE
//        kycEntryBinding!!.ivPanCardImage.setImageBitmap(imageUrl)
//
//        kycEntryBinding!!.ivFrontImage.setImageBitmap(imageUrl)
//
//        kycEntryBinding!!.ivBackImage.setImageBitmap(imageUrl)
//    }

    private fun selectPictureDialog(imageValue: String) {
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
            selectValueImage = imageValue
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
            dialog.dismiss()
        }
        binding.tvGallerySelectFile.setOnClickListener {
            selectValueImage = imageValue
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                )
            )
            dialog.dismiss()
        }
        binding.tvCancelSelectFile.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private val resultImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    selectSignatureImage = bitmap
                    kycEntryBinding!!.tvUploadSignature.visibility = View.GONE
                    kycEntryBinding!!.ivSignatureImage.visibility = View.VISIBLE
                    kycEntryBinding!!.ivSignatureImage.setImageBitmap(bitmap)
                }
            }
        }

    private val resultCustomerImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    selectCustomerImage = bitmap
                    kycEntryBinding!!.tvUploadCustomerImage.visibility = View.GONE
                    kycEntryBinding!!.ivCustomerImage.visibility = View.VISIBLE
                    kycEntryBinding!!.ivCustomerImage.setImageBitmap(bitmap)
                }
            }
        }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    when (selectValueImage) {
                        "1" -> {
                            selectPanCardImage = bitmap
                            kycEntryBinding!!.tvPanCardUpload.visibility = View.GONE
                            kycEntryBinding!!.ivPanCardImage.visibility = View.VISIBLE
                            kycEntryBinding!!.ivPanCardImage.setImageBitmap(bitmap)
                        }

                        "2" -> {
                            selectAadharCardFrontImage = bitmap
                            kycEntryBinding!!.tvUploadAadharCardFront.visibility = View.GONE
                            kycEntryBinding!!.ivUploadAadharCardFront.visibility = View.VISIBLE
                            kycEntryBinding!!.ivUploadAadharCardFront.setImageBitmap(bitmap)
                        }

                        "3" -> {
                            selectAadharCardBackImage = bitmap
                            kycEntryBinding!!.tvUploadAadharCardBack.visibility = View.GONE
                            kycEntryBinding!!.ivUploadAadharCardBack.visibility = View.VISIBLE
                            kycEntryBinding!!.ivUploadAadharCardBack.setImageBitmap(bitmap)
                        }
                    }
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
                when (selectValueImage) {
                    "1" -> {
                        selectPanCardImage = imageBitmap
                        kycEntryBinding!!.tvPanCardUpload.visibility = View.GONE
                        kycEntryBinding!!.ivPanCardImage.visibility = View.VISIBLE
                        kycEntryBinding!!.ivPanCardImage.setImageBitmap(imageBitmap)
                    }

                    "2" -> {
                        selectAadharCardFrontImage = imageBitmap
                        kycEntryBinding!!.tvUploadAadharCardFront.visibility = View.GONE
                        kycEntryBinding!!.ivUploadAadharCardFront.visibility = View.VISIBLE
                        kycEntryBinding!!.ivUploadAadharCardFront.setImageBitmap(imageBitmap)
                    }

                    "3" -> {
                        selectAadharCardBackImage = imageBitmap
                        kycEntryBinding!!.tvUploadAadharCardBack.visibility = View.GONE
                        kycEntryBinding!!.ivUploadAadharCardBack.visibility = View.VISIBLE
                        kycEntryBinding!!.ivUploadAadharCardBack.setImageBitmap(imageBitmap)
                    }
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
}