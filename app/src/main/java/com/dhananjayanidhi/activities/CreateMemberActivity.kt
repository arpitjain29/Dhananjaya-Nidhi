package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.ActivityCreateMemberBinding
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.MemberFlowManager
import com.dhananjayanidhi.utils.StepperHelper

class CreateMemberActivity : BaseActivity() {
    private var createMemberBinding: ActivityCreateMemberBinding? = null
    private var customerData: com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel? = null
    private var currentStep: MemberFlowManager.FlowStep = MemberFlowManager.FlowStep.CUSTOMER
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createMemberBinding = ActivityCreateMemberBinding.inflate(layoutInflater)
        setContentView(createMemberBinding!!.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
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
        
        // Setup app bar
        createMemberBinding!!.appLayout.ivMenu.visibility = View.GONE
        createMemberBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        createMemberBinding!!.appLayout.ivSearch.visibility = View.GONE
        createMemberBinding!!.appLayout.tvTitle.text = getString(R.string.create_member)
        createMemberBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        // Check if we're editing an existing member (from pending members)
        val customerId = intent.getStringExtra("customer_id")
        customerData = intent.getSerializableExtra("customer_data") as? com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel
        
        if (customerId != null && customerData != null) {
            // Editing existing member - set customer ID and load with data
            MemberFlowManager.setCustomerId(mContext!!, customerId)
            MemberFlowManager.startNewFlow(mContext!!) // Still start fresh flow for navigation
        } else {
            // Creating new member - start fresh
            MemberFlowManager.startNewFlow(mContext!!)
        }
        
        // Always load Customer step when activity is created
        if (savedInstanceState == null) {
            currentStep = MemberFlowManager.FlowStep.CUSTOMER
            loadFragment(MemberFlowManager.FlowStep.CUSTOMER, customerData)
            // Setup step indicator with Customer step
            updateStepper(MemberFlowManager.FlowStep.CUSTOMER)
        } else {
            // Restore current step from saved state or detect from fragment
            currentStep = getCurrentStep()
            // Setup step indicator based on current fragment if restoring
            updateStepper()
        }
    }
    
    override fun onBackPressed() {
        // Use stored currentStep instead of detecting from fragment
        if (currentStep == MemberFlowManager.FlowStep.CUSTOMER) {
            // If on first step, finish activity
            finish()
        } else {
            // Navigate to previous step using the stored current step
            navigateToPreviousStep(currentStep)
        }
    }
    
    fun updateStepper(step: MemberFlowManager.FlowStep? = null) {
        val stepToUse = step ?: currentStep
        createMemberBinding?.stepIndicator?.root?.let { rootView ->
            StepperHelper.updateStepperState(rootView, stepToUse, mContext!!)
        }
    }
    
    fun navigateToNextStep() {
        val nextStep = when (currentStep) {
            MemberFlowManager.FlowStep.CUSTOMER -> MemberFlowManager.FlowStep.ADDRESS
            MemberFlowManager.FlowStep.ADDRESS -> MemberFlowManager.FlowStep.NOMINEE
            MemberFlowManager.FlowStep.NOMINEE -> MemberFlowManager.FlowStep.KYC
            MemberFlowManager.FlowStep.KYC -> MemberFlowManager.FlowStep.ACCOUNT
            MemberFlowManager.FlowStep.ACCOUNT -> null // Flow complete
        }
        
        nextStep?.let { step ->
            currentStep = step // Update stored current step
            loadFragment(step)
            // Update stepper with the new step explicitly
            updateStepper(step)
        } ?: run {
            // Flow complete
            MemberFlowManager.completeFlow(mContext!!)
            finish()
            startActivity(Intent(mContext, HomeActivity::class.java))
        }
    }
    
    fun navigateToPreviousStep(step: MemberFlowManager.FlowStep? = null) {
        val stepToUse = step ?: currentStep
        val previousStep = when (stepToUse) {
            MemberFlowManager.FlowStep.CUSTOMER -> null
            MemberFlowManager.FlowStep.ADDRESS -> MemberFlowManager.FlowStep.CUSTOMER
            MemberFlowManager.FlowStep.NOMINEE -> MemberFlowManager.FlowStep.ADDRESS
            MemberFlowManager.FlowStep.KYC -> MemberFlowManager.FlowStep.NOMINEE
            MemberFlowManager.FlowStep.ACCOUNT -> MemberFlowManager.FlowStep.KYC
        }
        
        previousStep?.let { prevStep ->
            currentStep = prevStep // Update stored current step
            // Load fragment immediately
            loadFragment(prevStep)
            // Update stepper with the new step explicitly
            updateStepper(prevStep)
        } ?: run {
            finish()
        }
    }
    
    private fun getCurrentStep(): MemberFlowManager.FlowStep {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        return when (currentFragment) {
            is CustomerEntryActivity -> MemberFlowManager.FlowStep.CUSTOMER
            is AddressEntryActivity -> MemberFlowManager.FlowStep.ADDRESS
            is NomineeDetailsActivity -> MemberFlowManager.FlowStep.NOMINEE
            is KycEntryFragment -> MemberFlowManager.FlowStep.KYC
            is AccountOpenActivity -> MemberFlowManager.FlowStep.ACCOUNT
            else -> MemberFlowManager.FlowStep.CUSTOMER
        }
    }
    
//    private fun loadFragmentForCurrentStep() {
//        val pendingStep = MemberFlowManager.getNextPendingStep(mContext!!)
//        val stepToLoad = pendingStep ?: MemberFlowManager.FlowStep.CUSTOMER
//        loadFragment(stepToLoad)
//    }
    
    private fun loadFragment(step: MemberFlowManager.FlowStep, customerData: com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel? = null) {
        val fragment: Fragment = when (step) {
            MemberFlowManager.FlowStep.CUSTOMER -> {
                val customerFragment = CustomerEntryActivity()
                customerData?.let {
                    val bundle = Bundle()
                    bundle.putSerializable("customer_data", it)
                    customerFragment.arguments = bundle
                }
                customerFragment
            }
            MemberFlowManager.FlowStep.ADDRESS -> AddressEntryActivity()
            MemberFlowManager.FlowStep.NOMINEE -> NomineeDetailsActivity()
            MemberFlowManager.FlowStep.KYC -> KycEntryFragment()
            MemberFlowManager.FlowStep.ACCOUNT -> AccountOpenActivity()
        }
        
        // Post to main thread to ensure we're not in the middle of another transaction
        Handler(Looper.getMainLooper()).post {
            try {
                // Check if fragment manager is in a valid state
                if (!supportFragmentManager.isStateSaved) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commitAllowingStateLoss()
                } else {
                    // If state is saved, try again after a short delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, fragment)
                                .commitAllowingStateLoss()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, 100)
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                // If transaction fails, try again after a short delay
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .commitAllowingStateLoss()
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }
                }, 100)
            }
        }
    }
}

