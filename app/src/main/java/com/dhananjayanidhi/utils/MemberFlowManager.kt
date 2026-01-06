package com.dhananjayanidhi.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * MemberFlowManager - Centralized state management for multi-step member creation flow
 * 
 * Manages:
 * - Current customerId/memberId throughout the flow
 * - Completed steps tracking
 * - Flow state persistence
 * - Resume flow detection
 */
object MemberFlowManager {
    private const val PREFS_NAME = "member_flow_prefs"
    private const val KEY_CUSTOMER_ID = "customer_id"
    private const val KEY_STEP_CUSTOMER = "step_customer_completed"
    private const val KEY_STEP_ADDRESS = "step_address_completed"
    private const val KEY_STEP_NOMINEE = "step_nominee_completed"
    private const val KEY_STEP_KYC = "step_kyc_completed"
    private const val KEY_STEP_ACCOUNT = "step_account_completed"
    private const val KEY_FLOW_IN_PROGRESS = "flow_in_progress"
    private const val KEY_CUSTOMER_DATA = "customer_data"

    /**
     * Enum representing all steps in the member creation flow
     */
    enum class FlowStep(val stepNumber: Int, val stepName: String) {
        CUSTOMER(1, "Customer Entry"),
        ADDRESS(2, "Address Entry"),
        NOMINEE(3, "Nominee Details"),
        KYC(4, "KYC Entry"),
        ACCOUNT(5, "Account Open")
    }

    /**
     * Get SharedPreferences instance
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Start a new member creation flow
     */
    fun startNewFlow(context: Context) {
        getPrefs(context).edit().apply {
            putString(KEY_CUSTOMER_ID, null)
            putBoolean(KEY_STEP_CUSTOMER, false)
            putBoolean(KEY_STEP_ADDRESS, false)
            putBoolean(KEY_STEP_NOMINEE, false)
            putBoolean(KEY_STEP_KYC, false)
            putBoolean(KEY_STEP_ACCOUNT, false)
            putBoolean(KEY_FLOW_IN_PROGRESS, true)
            apply()
        }
    }

    /**
     * Store customer ID after successful customer creation
     */
    fun setCustomerId(context: Context, customerId: String?) {
        getPrefs(context).edit().apply {
            putString(KEY_CUSTOMER_ID, customerId)
            apply()
        }
    }

    /**
     * Get stored customer ID
     */
    fun getCustomerId(context: Context): String? {
        return getPrefs(context).getString(KEY_CUSTOMER_ID, null)
    }

    /**
     * Mark a step as completed
     */
    fun markStepCompleted(context: Context, step: FlowStep) {
        getPrefs(context).edit().apply {
            when (step) {
                FlowStep.CUSTOMER -> putBoolean(KEY_STEP_CUSTOMER, true)
                FlowStep.ADDRESS -> putBoolean(KEY_STEP_ADDRESS, true)
                FlowStep.NOMINEE -> putBoolean(KEY_STEP_NOMINEE, true)
                FlowStep.KYC -> putBoolean(KEY_STEP_KYC, true)
                FlowStep.ACCOUNT -> putBoolean(KEY_STEP_ACCOUNT, true)
            }
            apply()
        }
    }

    /**
     * Check if a step is completed
     */
    fun isStepCompleted(context: Context, step: FlowStep): Boolean {
        return getPrefs(context).getBoolean(
            when (step) {
                FlowStep.CUSTOMER -> KEY_STEP_CUSTOMER
                FlowStep.ADDRESS -> KEY_STEP_ADDRESS
                FlowStep.NOMINEE -> KEY_STEP_NOMINEE
                FlowStep.KYC -> KEY_STEP_KYC
                FlowStep.ACCOUNT -> KEY_STEP_ACCOUNT
            },
            false
        )
    }

    /**
     * Check if previous step is completed (required before accessing current step)
     * 
     * CRITICAL: This enforces sequential flow - users cannot skip steps.
     * Each step validates that the previous step is completed before allowing access.
     */
    fun canAccessStep(context: Context, step: FlowStep): Boolean {
        return when (step) {
            FlowStep.CUSTOMER -> true // First step is always accessible
            FlowStep.ADDRESS -> isStepCompleted(context, FlowStep.CUSTOMER)
            FlowStep.NOMINEE -> isStepCompleted(context, FlowStep.ADDRESS)
            FlowStep.KYC -> isStepCompleted(context, FlowStep.NOMINEE)
            FlowStep.ACCOUNT -> isStepCompleted(context, FlowStep.KYC)
        }
    }

    /**
     * Get the last completed step (for resume flow)
     */
    fun getLastCompletedStep(context: Context): FlowStep? {
        return when {
            isStepCompleted(context, FlowStep.ACCOUNT) -> FlowStep.ACCOUNT
            isStepCompleted(context, FlowStep.KYC) -> FlowStep.KYC
            isStepCompleted(context, FlowStep.NOMINEE) -> FlowStep.NOMINEE
            isStepCompleted(context, FlowStep.ADDRESS) -> FlowStep.ADDRESS
            isStepCompleted(context, FlowStep.CUSTOMER) -> FlowStep.CUSTOMER
            else -> null
        }
    }

    /**
     * Get the next pending step (for resume flow)
     * 
     * Used when user reopens the app or navigates back to member creation.
     * Automatically detects where the user left off and redirects to the correct step.
     */
    fun getNextPendingStep(context: Context): FlowStep? {
        return when {
            !isStepCompleted(context, FlowStep.CUSTOMER) -> FlowStep.CUSTOMER
            !isStepCompleted(context, FlowStep.ADDRESS) -> FlowStep.ADDRESS
            !isStepCompleted(context, FlowStep.NOMINEE) -> FlowStep.NOMINEE
            !isStepCompleted(context, FlowStep.KYC) -> FlowStep.KYC
            !isStepCompleted(context, FlowStep.ACCOUNT) -> FlowStep.ACCOUNT
            else -> null // All steps completed
        }
    }

    /**
     * Check if flow is in progress
     */
    fun isFlowInProgress(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_FLOW_IN_PROGRESS, false)
    }

    /**
     * Complete the entire flow (called after EMI step)
     */
    fun completeFlow(context: Context) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_FLOW_IN_PROGRESS, false)
            // Keep customer ID for reference but clear step flags
            putBoolean(KEY_STEP_CUSTOMER, false)
            putBoolean(KEY_STEP_ADDRESS, false)
            putBoolean(KEY_STEP_NOMINEE, false)
            putBoolean(KEY_STEP_KYC, false)
            putBoolean(KEY_STEP_ACCOUNT, false)
            apply()
        }
    }

    /**
     * Clear all flow data (for testing or reset)
     */
    fun clearFlow(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    /**
     * Get current step number for display (e.g., "Step 3 of 6")
     */
    fun getCurrentStepNumber(step: FlowStep): Int {
        return step.stepNumber
    }

    /**
     * Get total number of steps
     */
    fun getTotalSteps(): Int {
        return FlowStep.values().size
    }

    /**
     * Save customer data for later retrieval when navigating back
     */
    fun saveCustomerData(context: Context, firstName: String?, lastName: String?, phoneNumber: String?, 
                         dob: String?, gender: String?, fatherName: String?, motherName: String?,
                         annualIncome: String?, occupation: String?, caste: String?) {
        getPrefs(context).edit().apply {
            putString("${KEY_CUSTOMER_DATA}_first_name", firstName)
            putString("${KEY_CUSTOMER_DATA}_last_name", lastName)
            putString("${KEY_CUSTOMER_DATA}_phone_number", phoneNumber)
            putString("${KEY_CUSTOMER_DATA}_dob", dob)
            putString("${KEY_CUSTOMER_DATA}_gender", gender)
            putString("${KEY_CUSTOMER_DATA}_father_name", fatherName)
            putString("${KEY_CUSTOMER_DATA}_mother_name", motherName)
            putString("${KEY_CUSTOMER_DATA}_annual_income", annualIncome)
            putString("${KEY_CUSTOMER_DATA}_occupation", occupation)
            putString("${KEY_CUSTOMER_DATA}_caste", caste)
            apply()
        }
    }

    /**
     * Get saved customer data
     */
    fun getSavedCustomerData(context: Context): Map<String, String?> {
        val prefs = getPrefs(context)
        return mapOf(
            "first_name" to prefs.getString("${KEY_CUSTOMER_DATA}_first_name", null),
            "last_name" to prefs.getString("${KEY_CUSTOMER_DATA}_last_name", null),
            "phone_number" to prefs.getString("${KEY_CUSTOMER_DATA}_phone_number", null),
            "dob" to prefs.getString("${KEY_CUSTOMER_DATA}_dob", null),
            "gender" to prefs.getString("${KEY_CUSTOMER_DATA}_gender", null),
            "father_name" to prefs.getString("${KEY_CUSTOMER_DATA}_father_name", null),
            "mother_name" to prefs.getString("${KEY_CUSTOMER_DATA}_mother_name", null),
            "annual_income" to prefs.getString("${KEY_CUSTOMER_DATA}_annual_income", null),
            "occupation" to prefs.getString("${KEY_CUSTOMER_DATA}_occupation", null),
            "caste" to prefs.getString("${KEY_CUSTOMER_DATA}_caste", null)
        )
    }

    /**
     * Clear saved customer data
     */
    fun clearCustomerData(context: Context) {
        getPrefs(context).edit().apply {
            remove("${KEY_CUSTOMER_DATA}_first_name")
            remove("${KEY_CUSTOMER_DATA}_last_name")
            remove("${KEY_CUSTOMER_DATA}_phone_number")
            remove("${KEY_CUSTOMER_DATA}_dob")
            remove("${KEY_CUSTOMER_DATA}_gender")
            remove("${KEY_CUSTOMER_DATA}_father_name")
            remove("${KEY_CUSTOMER_DATA}_mother_name")
            remove("${KEY_CUSTOMER_DATA}_annual_income")
            remove("${KEY_CUSTOMER_DATA}_occupation")
            remove("${KEY_CUSTOMER_DATA}_caste")
            apply()
        }
    }
}

