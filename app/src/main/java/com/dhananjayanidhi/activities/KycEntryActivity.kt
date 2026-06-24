package com.dhananjayanidhi.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import com.dhananjayanidhi.R
import com.yalantis.ucrop.UCrop
import java.io.File
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityReKycEntryBinding
import com.dhananjayanidhi.databinding.SelectFileLayoutBinding
import com.dhananjayanidhi.models.kycentry.KycEntryModel
import com.dhananjayanidhi.models.memberdocumentinfo.MemberDocumentInfoModel
import com.dhananjayanidhi.parameters.KycEntryParams
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.MemberFlowManager
import com.dhananjayanidhi.utils.RealFileUtils
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KycEntryActivity : BaseActivity() {
    private var kycEntryBinding: ActivityReKycEntryBinding? = null
    private var selectValueImage: String? = null
    private var addCustomerId: String? = null
    private var selectAadharCardFrontImage: Bitmap? = null
    private var selectAadharCardBackImage: Bitmap? = null
    private var selectPanCardImage: Bitmap? = null
    private var selectCustomerImage: Bitmap? = null
    private var selectSignatureImage: Bitmap? = null
    private var isSubmitting = false
    private var cameraImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kycEntryBinding = ActivityReKycEntryBinding.inflate(layoutInflater)
        setContentView(kycEntryBinding!!.root)
        kycEntryBinding?.appLayout?.ivMenu?.visibility = View.GONE
        kycEntryBinding?.appLayout?.ivBackArrow?.visibility = View.VISIBLE
        kycEntryBinding?.appLayout?.ivSearch?.visibility = View.GONE
        kycEntryBinding?.appLayout?.tvTitle?.visibility = View.VISIBLE
        kycEntryBinding?.appLayout?.tvTitle?.text = getString(R.string.kyc_entry)
        kycEntryBinding?.appLayout?.ivBackArrow?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        // Get customer ID from flow manager
        addCustomerId = intent.getStringExtra(Constants.customerListId)
        
        if (addCustomerId.isNullOrEmpty()) {
            CommonFunction.showToastSingle(mContext!!, "Customer ID not found. Please start from beginning.", 0)

        }
        
        // Check if step is already completed (resume flow)
        // Removed auto-navigation - allow user to view/edit completed steps when navigating back
        
        // Load document info from API
        loadDocumentInfo()
        
        // Add TextWatchers to clear errors when user types
        setupTextWatchers()

        kycEntryBinding?.llKycLayout?.btnUploadKycEntry?.setOnClickListener {
            // Prevent multiple clicks
            if (isSubmitting) return@setOnClickListener
            
            val kycEntryParams = KycEntryParams()
            kycEntryParams.customerId = addCustomerId
            kycEntryParams.aadharNumber =
                kycEntryBinding?.llKycLayout?.etAddharNumberKycEntry?.text.toString().trim()
            kycEntryParams.panNumber = kycEntryBinding?.llKycLayout?.etPanCardKycEntry?.text.toString().trim().uppercase()
            kycEntryParams.aadharFrontImage = selectAadharCardFrontImage.toString()
            kycEntryParams.aadharBackImage = selectAadharCardBackImage.toString()
            kycEntryParams.panImage = selectPanCardImage.toString()
            kycEntryParams.customerPicture = selectCustomerImage.toString()
            kycEntryParams.signature = selectSignatureImage.toString()

            // Clear all previous errors
            kycEntryBinding?.llKycLayout?.tilAddharNumberKycEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            kycEntryBinding?.llKycLayout?.tilPanCardKycEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            
            var hasError = false
            
            if (TextUtils.isEmpty(kycEntryParams.aadharNumber)) {
                kycEntryBinding?.llKycLayout?.tilAddharNumberKycEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_aadhar_card_no)
                }
                hasError = true
            } else if (kycEntryParams.aadharNumber!!.length != 12 || !kycEntryParams.aadharNumber!!.all { it.isDigit() }) {
                kycEntryBinding?.llKycLayout?.tilAddharNumberKycEntry?.apply {
                    isErrorEnabled = true
                    error = "Aadhar number must be exactly 12 digits"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.panNumber)) {
                kycEntryBinding?.llKycLayout?.tilPanCardKycEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_pan_card_no)
                }
                hasError = true
            } else {
                // PAN format: 5 letters, 4 digits, 1 letter (e.g., ABCDE1234F)
                val panPattern = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")
                if (!panPattern.matches(kycEntryParams.panNumber!!.uppercase())) {
                    kycEntryBinding?.llKycLayout?.tilPanCardKycEntry?.apply {
                        isErrorEnabled = true
                        error = "PAN card must be in format: ABCDE1234F (5 letters, 4 digits, 1 letter)"
                    }
                    hasError = true
                }
            }
            if (TextUtils.isEmpty(kycEntryParams.aadharFrontImage)) {
                CommonFunction.showToastSingle(
                    mContext!!, getString(R.string.please_click_aadhar_card_front_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.aadharBackImage)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_aadhar_card_back_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.panImage)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_pan_card_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.customerPicture)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_aadhar_customer_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.signature)) {
                CommonFunction.showToastSingle(
                    mContext!!,
                    getString(R.string.please_click_signature_image),
                    0
                )
                hasError = true
            }
            
            if (!hasError) {
                isSubmitting = true
                kycEntryBinding?.llKycLayout?.btnUploadKycEntry?.isEnabled = false
                kycEntryApi(kycEntryParams)
            }
        }

        kycEntryBinding?.llKycLayout?.llUploadPanCard?.setOnClickListener {
            selectPictureDialog("1")
        }

        kycEntryBinding?.llKycLayout?.llUploadAadharCardFront?.setOnClickListener {
            selectPictureDialog("2")
        }

        kycEntryBinding?.llKycLayout?.llUploadAadharCardBack?.setOnClickListener {
            selectPictureDialog("3")
        }

        kycEntryBinding?.llKycLayout?.llUploadCustomerImage?.setOnClickListener {
            selectPictureDialog("4")
        }

        kycEntryBinding?.llKycLayout?.llUploadSignature?.setOnClickListener {
            selectPictureDialog("5")
        }
    }

    private fun setupTextWatchers() {
        fun createErrorClearingWatcher(til: TextInputLayout?) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                til?.error = null
                til?.isErrorEnabled = false
            }
        }
        
        kycEntryBinding?.llKycLayout?.etAddharNumberKycEntry?.addTextChangedListener(
            createErrorClearingWatcher(kycEntryBinding?.llKycLayout?.tilAddharNumberKycEntry)
        )
        kycEntryBinding?.llKycLayout?.etPanCardKycEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Convert to uppercase automatically
                val currentText = s?.toString() ?: ""
                val upperText = currentText.uppercase()
                if (currentText != upperText) {
                    kycEntryBinding?.llKycLayout?.etPanCardKycEntry?.setText(upperText)
                    kycEntryBinding?.llKycLayout?.etPanCardKycEntry?.setSelection(upperText.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                kycEntryBinding?.llKycLayout?.tilPanCardKycEntry?.error = null
                kycEntryBinding?.llKycLayout?.tilPanCardKycEntry?.isErrorEnabled = false
            }
        })
    }
    
    private fun loadDocumentInfo() {
        if (addCustomerId.isNullOrEmpty()) return
        
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call = ApiClient.buildService(mContext).memberDocumentInfoApi(addCustomerId!!)
            call?.enqueue(object : Callback<MemberDocumentInfoModel?> {
                override fun onResponse(
                    call: Call<MemberDocumentInfoModel?>,
                    response: Response<MemberDocumentInfoModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val documentInfoModel: MemberDocumentInfoModel? = response.body()
                        if (documentInfoModel != null && documentInfoModel.status == true) {
                            val data = documentInfoModel.data
                            data?.let { docData ->
                                // Set Aadhar and PAN numbers
                                docData.aadharNumber?.let {
                                    kycEntryBinding?.llKycLayout?.etAddharNumberKycEntry?.setText(it)
                                }
                                docData.panNumber?.let {
                                    kycEntryBinding?.llKycLayout?.etPanCardKycEntry?.setText(it)
                                }
                                
                                // Load images if URLs are available
                                docData.aadharFrontUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.llKycLayout?.tvUploadAadharCardFront?.visibility = View.GONE
                                        kycEntryBinding?.llKycLayout?.ivUploadAadharCardFront?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            mContext!!,
                                            url,
                                            kycEntryBinding?.llKycLayout?.ivUploadAadharCardFront,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.aadharBackUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.llKycLayout?.tvUploadAadharCardBack?.visibility = View.GONE
                                        kycEntryBinding?.llKycLayout?.ivUploadAadharCardBack?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            mContext!!,
                                            url,
                                            kycEntryBinding?.llKycLayout?.ivUploadAadharCardBack,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.panUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.llKycLayout?.tvPanCardUpload?.visibility = View.GONE
                                        kycEntryBinding?.llKycLayout?.ivPanCardImage?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            mContext!!,
                                            url,
                                            kycEntryBinding?.llKycLayout?.ivPanCardImage,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.profileImageUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.llKycLayout?.tvUploadCustomerImage?.visibility = View.GONE
                                        kycEntryBinding?.llKycLayout?.ivCustomerImage?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            mContext!!,
                                            url,
                                            kycEntryBinding?.llKycLayout?.ivCustomerImage,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.signatureUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.llKycLayout?.tvUploadSignature?.visibility = View.GONE
                                        kycEntryBinding?.llKycLayout?.ivSignatureImage?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            mContext!!,
                                            url,
                                            kycEntryBinding?.llKycLayout?.ivSignatureImage,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MemberDocumentInfoModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                }
            })
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
                    isSubmitting = false
                    kycEntryBinding?.llKycLayout?.btnUploadKycEntry?.isEnabled = true
                    
                    if (response.isSuccessful) {
                        val kycEntryModel: KycEntryModel? = response.body()
                        if (kycEntryModel != null) {
                            if (kycEntryModel.success == true) {
                                // Mark step as completed
                                MemberFlowManager.markStepCompleted(mContext!!, MemberFlowManager.FlowStep.KYC)
                                

                                // Show success message
                                CommonFunction.showToastSingle(mContext!!, 
                                    kycEntryModel.message ?: "KYC details saved successfully", 0)

                                finish()

                            } else {
                                val errorMsg = kycEntryModel.message ?: "Failed to save KYC details"
                                CommonFunction.showToastSingle(mContext!!, errorMsg, 0)
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorJson = JSONObject(errorBody)
                                val errorArray = errorJson.getJSONArray("error")
                                val errorMessage = errorArray.getJSONObject(0).getString("message")
                                CommonFunction.showToastSingle(mContext!!, errorMessage, 0)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                CommonFunction.showToastSingle(
                                    mContext!!,
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<KycEntryModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    isSubmitting = false
                    kycEntryBinding?.llKycLayout?.btnUploadKycEntry?.isEnabled = true
                    
                    throwable.printStackTrace()
                    CommonFunction.showToastSingle(
                        mContext!!,
                        "Network error. Please check your connection and try again.",
                        0
                    )
                }
            })
        } else {
            CommonFunction.showToastSingle(
                mContext!!, resources.getString(R.string.net_connection), 0
            )
        }
    }

//    override fun onUploadImage(imageUrl: Bitmap) {
//        super.onUploadImage(imageUrl)
//        kycEntryBinding?.llKycLayout?.tvPanCardUpload.visibility = View.GONE
//        kycEntryBinding?.llKycLayout?.ivPanCardImage.visibility = View.VISIBLE
//        kycEntryBinding?.llKycLayout?.ivPanCardImage.setImageBitmap(imageUrl)
//
//        kycEntryBinding?.llKycLayout?.ivFrontImage.setImageBitmap(imageUrl)
//
//        kycEntryBinding?.llKycLayout?.ivBackImage.setImageBitmap(imageUrl)
//    }

    private fun selectPictureDialog(imageValue: String) {
        val dialog = Dialog(mContext!!, R.style.CustomAlertDialogStylePopup)
        if (dialog.window != null) {
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setGravity(Gravity.BOTTOM)
        }
        val binding: SelectFileLayoutBinding =
            SelectFileLayoutBinding.inflate(LayoutInflater.from(mContext!!), null, false)
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
            val imageFile = File(mContext!!.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            cameraImageUri = FileProvider.getUriForFile(
                mContext!!,
                "${mContext!!.packageName}.provider",
                imageFile
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            }
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

    private val cropResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val croppedUri = UCrop.getOutput(result.data!!)
                croppedUri?.let { uri ->
                    try {
                        val inputStream = mContext!!.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        bitmap?.let {
                            when (selectValueImage) {
                                "1" -> { // PAN Card
                                    selectPanCardImage = bitmap
                                    kycEntryBinding?.llKycLayout?.tvPanCardUpload?.visibility = View.GONE
                                    kycEntryBinding?.llKycLayout?.ivPanCardImage?.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "2" -> { // Aadhar Front
                                    selectAadharCardFrontImage = bitmap
                                    kycEntryBinding?.llKycLayout?.tvUploadAadharCardFront?.visibility = View.GONE
                                    kycEntryBinding?.llKycLayout?.ivUploadAadharCardFront?.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "3" -> { // Aadhar Back
                                    selectAadharCardBackImage = bitmap
                                    kycEntryBinding?.llKycLayout?.tvUploadAadharCardBack?.visibility = View.GONE
                                    kycEntryBinding?.llKycLayout?.ivUploadAadharCardBack?.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "4" -> { // Customer Image
                                    selectCustomerImage = bitmap
                                    kycEntryBinding?.llKycLayout?.tvUploadCustomerImage?.visibility = View.GONE
                                    kycEntryBinding?.llKycLayout?.ivCustomerImage?.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "5" -> { // Signature
                                    selectSignatureImage = bitmap
                                    kycEntryBinding?.llKycLayout?.tvUploadSignature?.visibility = View.GONE
                                    kycEntryBinding?.llKycLayout?.ivSignatureImage?.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                else -> {
                                    Log.w("KycEntryActivity", "Unknown selectValueImage: $selectValueImage")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("KycEntryActivity", "Error loading cropped image", e)
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR && result.data != null) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
                Log.e("KycEntryActivity", "Crop error: ${cropError?.message}")
            }
        }

    private val resultImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                cameraImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
        }

    private val resultCustomerImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                cameraImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
        }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                cameraImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
        }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                startCrop(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = CommonFunction.getOutputUri(mContext!!)
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withMaxResultSize(1000, 1000)

        cropResultLauncher.launch(uCrop.getIntent(mContext!!))
    }
}