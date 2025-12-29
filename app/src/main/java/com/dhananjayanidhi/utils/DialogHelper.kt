package com.dhananjayanidhi.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.LogoutPopupBinding
import com.dhananjayanidhi.databinding.MsgPopupBinding

/**
 * Helper class for creating and showing common dialogs
 * Provides reusable dialog components to reduce code duplication
 */
object DialogHelper {

    /**
     * Shows a confirmation dialog (e.g., logout confirmation)
     * @param activity The activity context
     * @param message The message to display
     * @param positiveButtonText Text for positive action button
     * @param negativeButtonText Text for negative action button
     * @param onPositiveClick Callback for positive button click
     * @param onNegativeClick Optional callback for negative button click
     * @return The created Dialog instance
     */
    fun showConfirmationDialog(
        activity: Activity,
        message: String,
        positiveButtonText: String = activity.getString(R.string.logout),
        negativeButtonText: String = activity.getString(R.string.cancel),
        onPositiveClick: () -> Unit,
        onNegativeClick: (() -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(activity, R.style.CustomAlertDialogStyle_space)
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(Gravity.CENTER)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        dialog.setCancelable(true)

        val binding: LogoutPopupBinding = LogoutPopupBinding.inflate(
            LayoutInflater.from(activity), null, false
        )
        dialog.setContentView(binding.root)

        binding.tvMessageTextPopup.text = message
        binding.tvYesTextPopup.text = positiveButtonText
        binding.tvNoTextPopup.text = negativeButtonText

        binding.tvNoTextPopup.setOnClickListener {
            dialog.dismiss()
            onNegativeClick?.invoke()
        }

        binding.tvYesTextPopup.setOnClickListener {
            dialog.dismiss()
            onPositiveClick()
        }

        dialog.show()
        return dialog
    }

    /**
     * Shows a simple message dialog with OK button
     * @param activity The activity context
     * @param message The message to display
     * @param onOkClick Optional callback for OK button click
     * @return The created Dialog instance
     */
    fun showMessageDialog(
        activity: Activity,
        message: String,
        onOkClick: (() -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(activity, R.style.CustomAlertDialogStyle_space)
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setGravity(Gravity.CENTER)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        dialog.setCancelable(true)

        val binding: MsgPopupBinding = MsgPopupBinding.inflate(
            LayoutInflater.from(activity), null, false
        )
        dialog.setContentView(binding.root)

        binding.tvMessageTextPopup.text = message
        binding.tvYesTextPopup.text = activity.getString(R.string.ok)

        binding.tvYesTextPopup.setOnClickListener {
            dialog.dismiss()
            onOkClick?.invoke()
        }

        dialog.show()
        return dialog
    }
}
