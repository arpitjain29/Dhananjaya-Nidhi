package com.dhananjayanidhi.activities

import android.app.Activity
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
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.dhananjayanidhi.R
import com.yalantis.ucrop.UCrop
import java.io.File
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityKycEntryBinding
import com.dhananjayanidhi.databinding.SelectFileLayoutBinding
import com.dhananjayanidhi.models.kycentry.KycEntryModel
import com.dhananjayanidhi.models.memberdocumentinfo.MemberDocumentInfoModel
import com.dhananjayanidhi.parameters.KycEntryParams
import com.dhananjayanidhi.utils.BaseFragment
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.MemberFlowManager
import com.dhananjayanidhi.utils.RealFileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable
import com.dhananjayanidhi.utils.Constants

class KycEntryFragment : BaseFragment() {
    private var kycEntryBinding: ActivityKycEntryBinding? = null
    private var selectValueImage: String? = null
    private var addCustomerId: String? = null
    private var selectAadharCardFrontImage: Bitmap? = null
    private var selectAadharCardBackImage: Bitmap? = null
    private var selectPanCardImage: Bitmap? = null
    private var selectCustomerImage: Bitmap? = null
    private var selectSignatureImage: Bitmap? = null
    private var isSubmitting = false
    private var cameraImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        kycEntryBinding = ActivityKycEntryBinding.inflate(inflater, container, false)
        return kycEntryBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Validate step access
        if (!MemberFlowManager.canAccessStep(requireContext(), MemberFlowManager.FlowStep.KYC)) {
            CommonFunction.showToastSingle(requireContext(), "Please complete previous steps first", 0)
            (activity as? CreateMemberActivity)?.navigateToPreviousStep()
            return
        }

        // Get customer ID from flow manager
        addCustomerId = MemberFlowManager.getCustomerId(requireContext())
        
        if (addCustomerId.isNullOrEmpty()) {
            CommonFunction.showToastSingle(requireContext(), "Customer ID not found. Please start from beginning.", 0)
            (activity as? CreateMemberActivity)?.navigateToPreviousStep()
            return
        }
        
        // Check if step is already completed (resume flow)
        // Removed auto-navigation - allow user to view/edit completed steps when navigating back
        
        // Load document info from API
        loadDocumentInfo()
        
        // Add TextWatchers to clear errors when user types
        setupTextWatchers()

        kycEntryBinding!!.btnUploadKycEntry.setOnClickListener {
            // Prevent multiple clicks
            if (isSubmitting) return@setOnClickListener
            
            val kycEntryParams = KycEntryParams()
            kycEntryParams.customerId = addCustomerId
            kycEntryParams.aadharNumber =
                kycEntryBinding?.etAddharNumberKycEntry?.text.toString().trim()
            kycEntryParams.panNumber = kycEntryBinding?.etPanCardKycEntry?.text.toString().trim().uppercase()
            kycEntryParams.aadharFrontImage = selectAadharCardFrontImage.toString()
            kycEntryParams.aadharBackImage = selectAadharCardBackImage.toString()
            kycEntryParams.panImage = selectPanCardImage.toString()
            kycEntryParams.customerPicture = selectCustomerImage.toString()
            kycEntryParams.signature = selectSignatureImage.toString()

            // Clear all previous errors
            kycEntryBinding?.tilAddharNumberKycEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            kycEntryBinding?.tilPanCardKycEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            
            var hasError = false
            
            if (TextUtils.isEmpty(kycEntryParams.aadharNumber)) {
                kycEntryBinding?.tilAddharNumberKycEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_aadhar_card_no)
                }
                hasError = true
            } else if (kycEntryParams.aadharNumber!!.length != 12 || !kycEntryParams.aadharNumber!!.all { it.isDigit() }) {
                kycEntryBinding?.tilAddharNumberKycEntry?.apply {
                    isErrorEnabled = true
                    error = "Aadhar number must be exactly 12 digits"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.panNumber)) {
                kycEntryBinding?.tilPanCardKycEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_pan_card_no)
                }
                hasError = true
            } else {
                // PAN format: 5 letters, 4 digits, 1 letter (e.g., ABCDE1234F)
                val panPattern = Regex("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")
                if (!panPattern.matches(kycEntryParams.panNumber!!.uppercase())) {
                    kycEntryBinding?.tilPanCardKycEntry?.apply {
                        isErrorEnabled = true
                        error = "PAN card must be in format: ABCDE1234F (5 letters, 4 digits, 1 letter)"
                    }
                    hasError = true
                }
            }
            if (TextUtils.isEmpty(kycEntryParams.aadharFrontImage)) {
                CommonFunction.showToastSingle(
                    requireContext(), getString(R.string.please_click_aadhar_card_front_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.aadharBackImage)) {
                CommonFunction.showToastSingle(
                    requireContext(),
                    getString(R.string.please_click_aadhar_card_back_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.panImage)) {
                CommonFunction.showToastSingle(
                    requireContext(),
                    getString(R.string.please_click_pan_card_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.customerPicture)) {
                CommonFunction.showToastSingle(
                    requireContext(),
                    getString(R.string.please_click_aadhar_customer_image),
                    0
                )
                hasError = true
            }
            if (TextUtils.isEmpty(kycEntryParams.signature)) {
                CommonFunction.showToastSingle(
                    requireContext(),
                    getString(R.string.please_click_signature_image),
                    0
                )
                hasError = true
            }
            
            if (!hasError) {
                isSubmitting = true
                kycEntryBinding!!.btnUploadKycEntry.isEnabled = false
                kycEntryApi(kycEntryParams)
            }
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
            selectValueImage = "4"
            val imageFile = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                imageFile
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            }
            resultCustomerImageLauncher.launch(cameraIntent)
        }

        kycEntryBinding!!.llUploadSignature.setOnClickListener {
            selectValueImage = "5"
            val imageFile = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                imageFile
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            }
            resultImageLauncher.launch(cameraIntent)
        }
    }

    private fun setupTextWatchers() {
        fun createErrorClearingWatcher(til: com.google.android.material.textfield.TextInputLayout?) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                til?.error = null
                til?.isErrorEnabled = false
            }
        }
        
        kycEntryBinding?.etAddharNumberKycEntry?.addTextChangedListener(
            createErrorClearingWatcher(kycEntryBinding?.tilAddharNumberKycEntry)
        )
        kycEntryBinding?.etPanCardKycEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Convert to uppercase automatically
                val currentText = s?.toString() ?: ""
                val upperText = currentText.uppercase()
                if (currentText != upperText) {
                    kycEntryBinding?.etPanCardKycEntry?.setText(upperText)
                    kycEntryBinding?.etPanCardKycEntry?.setSelection(upperText.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                kycEntryBinding?.tilPanCardKycEntry?.error = null
                kycEntryBinding?.tilPanCardKycEntry?.isErrorEnabled = false
            }
        })
    }
    
    private fun loadDocumentInfo() {
        if (addCustomerId.isNullOrEmpty()) return
        
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call = ApiClient.buildService(activity).memberDocumentInfoApi(addCustomerId!!)
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
                                    kycEntryBinding?.etAddharNumberKycEntry?.setText(it)
                                }
                                docData.panNumber?.let {
                                    kycEntryBinding?.etPanCardKycEntry?.setText(it)
                                }
                                
                                // Load images if URLs are available
                                docData.aadharFrontUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.tvUploadAadharCardFront?.visibility = View.GONE
                                        kycEntryBinding?.ivUploadAadharCardFront?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            requireContext(),
                                            url,
                                            kycEntryBinding?.ivUploadAadharCardFront,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.aadharBackUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.tvUploadAadharCardBack?.visibility = View.GONE
                                        kycEntryBinding?.ivUploadAadharCardBack?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            requireContext(),
                                            url,
                                            kycEntryBinding?.ivUploadAadharCardBack,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.panUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.tvPanCardUpload?.visibility = View.GONE
                                        kycEntryBinding?.ivPanCardImage?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            requireContext(),
                                            url,
                                            kycEntryBinding?.ivPanCardImage,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.profileImageUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.tvUploadCustomerImage?.visibility = View.GONE
                                        kycEntryBinding?.ivCustomerImage?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            requireContext(),
                                            url,
                                            kycEntryBinding?.ivCustomerImage,
                                            R.drawable.ic_app_image
                                        )
                                    }
                                }
                                
                                docData.signatureUrl?.let { url ->
                                    if (url.isNotEmpty()) {
                                        kycEntryBinding?.tvUploadSignature?.visibility = View.GONE
                                        kycEntryBinding?.ivSignatureImage?.visibility = View.VISIBLE
                                        CommonFunction.loadImageViaGlide(
                                            requireContext(),
                                            url,
                                            kycEntryBinding?.ivSignatureImage,
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

    private fun navigateToNextStep() {
        (activity as? CreateMemberActivity)?.navigateToNextStep()
    }

    private fun kycEntryApi(kycEntryParams: KycEntryParams) {
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val coverImageFileFront =
                selectAadharCardFrontImage?.let { CommonFunction.persistImage(it, requireContext()) }
            val requestFileFront =
                coverImageFileFront?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsAadharFront = requestFileFront?.let {
                MultipartBody.Part.createFormData(
                    Constants.aadhar_front_image, coverImageFileFront.name,
                    it
                )
            }

            val coverImageFileBack =
                selectAadharCardBackImage?.let { CommonFunction.persistImage(it, requireContext()) }
            val requestFileBack =
                coverImageFileBack?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsAadharBack = requestFileBack?.let {
                MultipartBody.Part.createFormData(
                    Constants.aadhar_back_image, coverImageFileBack.name,
                    it
                )
            }

            val coverImageFilePanCard =
                selectPanCardImage?.let { CommonFunction.persistImage(it, requireContext()) }
            val requestFilePanCard =
                coverImageFilePanCard?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsPanCard = requestFilePanCard?.let {
                MultipartBody.Part.createFormData(
                    Constants.pan_image, coverImageFilePanCard.name,
                    it
                )
            }

            val coverImageFileCustomer =
                selectCustomerImage?.let { CommonFunction.persistImage(it, requireContext()) }
            val requestFileCustomer =
                coverImageFileCustomer?.asRequestBody(Constants.imageOutput.toMediaTypeOrNull())
            val mediaFilePartsCustomer = requestFileCustomer?.let {
                MultipartBody.Part.createFormData(
                    Constants.customer_picture, coverImageFileCustomer.name,
                    it
                )
            }

            val coverImageFileSignature =
                selectPanCardImage?.let { CommonFunction.persistImage(it, requireContext()) }
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

            val call = ApiClient.buildService(activity)
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
                    kycEntryBinding!!.btnUploadKycEntry.isEnabled = true
                    
                    if (response.isSuccessful) {
                        val kycEntryModel: KycEntryModel? = response.body()
                        if (kycEntryModel != null) {
                            if (kycEntryModel.success == true) {
                                // Mark step as completed
                                MemberFlowManager.markStepCompleted(requireContext(), MemberFlowManager.FlowStep.KYC)
                                
                                // Update stepper in parent activity
                                (activity as? CreateMemberActivity)?.updateStepper()
                                
                                // Show success message
                                CommonFunction.showToastSingle(requireContext(), 
                                    kycEntryModel.message ?: "KYC details saved successfully", 0)
                                
                                // Navigate to next step
                                navigateToNextStep()
                            } else {
                                val errorMsg = kycEntryModel.message ?: "Failed to save KYC details"
                                CommonFunction.showToastSingle(requireContext(), errorMsg, 0)
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorJson = JSONObject(errorBody)
                                val errorArray = errorJson.getJSONArray("error")
                                val errorMessage = errorArray.getJSONObject(0).getString("message")
                                CommonFunction.showToastSingle(requireContext(), errorMessage, 0)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                CommonFunction.showToastSingle(
                                    requireContext(),
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
                    kycEntryBinding!!.btnUploadKycEntry.isEnabled = true
                    
                    throwable.printStackTrace()
                    CommonFunction.showToastSingle(
                        requireContext(),
                        "Network error. Please check your connection and try again.",
                        0
                    )
                }
            })
        } else {
            CommonFunction.showToastSingle(
                requireContext(), resources.getString(R.string.net_connection), 0
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
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialogStylePopup)
        if (dialog.window != null) {
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setGravity(Gravity.BOTTOM)
        }
        val binding: SelectFileLayoutBinding =
            SelectFileLayoutBinding.inflate(LayoutInflater.from(requireContext()), null, false)
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
            val imageFile = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
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
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val croppedUri = UCrop.getOutput(result.data!!)
                croppedUri?.let { uri ->
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        bitmap?.let {
                            when (selectValueImage) {
                                "1" -> { // PAN Card
                                    selectPanCardImage = bitmap
                                    kycEntryBinding!!.tvPanCardUpload.visibility = View.GONE
                                    kycEntryBinding!!.ivPanCardImage.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "2" -> { // Aadhar Front
                                    selectAadharCardFrontImage = bitmap
                                    kycEntryBinding!!.tvUploadAadharCardFront.visibility = View.GONE
                                    kycEntryBinding!!.ivUploadAadharCardFront.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "3" -> { // Aadhar Back
                                    selectAadharCardBackImage = bitmap
                                    kycEntryBinding!!.tvUploadAadharCardBack.visibility = View.GONE
                                    kycEntryBinding!!.ivUploadAadharCardBack.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "4" -> { // Customer Image
                                    selectCustomerImage = bitmap
                                    kycEntryBinding!!.tvUploadCustomerImage.visibility = View.GONE
                                    kycEntryBinding!!.ivCustomerImage.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                "5" -> { // Signature
                                    selectSignatureImage = bitmap
                                    kycEntryBinding!!.tvUploadSignature.visibility = View.GONE
                                    kycEntryBinding!!.ivSignatureImage.apply {
                                        visibility = View.VISIBLE
                                        setImageBitmap(bitmap)
                                    }
                                }
                                else -> {
                                    Log.w("KycEntryFragment", "Unknown selectValueImage: $selectValueImage")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("KycEntryFragment", "Error loading cropped image", e)
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR && result.data != null) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
                Log.e("KycEntryFragment", "Crop error: ${cropError?.message}")
            }
        }

    private val resultImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
        }

    private val resultCustomerImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraImageUri?.let { uri ->
                    startCrop(uri)
                }
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
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                startCrop(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = CommonFunction.getOutputUri(requireContext())
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)

        cropResultLauncher.launch(uCrop.getIntent(requireContext()))
    }
}