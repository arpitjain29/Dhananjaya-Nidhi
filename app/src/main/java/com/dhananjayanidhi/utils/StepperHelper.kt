package com.dhananjayanidhi.utils

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dhananjayanidhi.R

/**
 * Helper class to update stepper UI state based on current step
 */
object StepperHelper {
    
    /**
     * Update stepper visual state based on current step
     * @param rootView The root view of step_indicator layout
     * @param currentStep The current step in the flow
     * @param context The context
     */
    fun updateStepperState(rootView: View, currentStep: MemberFlowManager.FlowStep, context: android.content.Context) {
        val stepNumber = currentStep.stepNumber
        
        // Get all step views
        val viewStep1 = rootView.findViewById<View>(R.id.viewStep1)
        val viewStep2 = rootView.findViewById<View>(R.id.viewStep2)
        val viewStep3 = rootView.findViewById<View>(R.id.viewStep3)
        val viewStep4 = rootView.findViewById<View>(R.id.viewStep4)
        val viewStep5 = rootView.findViewById<View>(R.id.viewStep5)
        
        val lineProgress = rootView.findViewById<View>(R.id.lineProgress)
        
        val tvStep1Label = rootView.findViewById<TextView>(R.id.tvStep1Label)
        val tvStep2Label = rootView.findViewById<TextView>(R.id.tvStep2Label)
        val tvStep3Label = rootView.findViewById<TextView>(R.id.tvStep3Label)
        val tvStep4Label = rootView.findViewById<TextView>(R.id.tvStep4Label)
        val tvStep5Label = rootView.findViewById<TextView>(R.id.tvStep5Label)
        
        // Update step circles
        updateStepCircle(viewStep1, stepNumber >= 1, context)
        updateStepCircle(viewStep2, stepNumber >= 2, context)
        updateStepCircle(viewStep3, stepNumber >= 3, context)
        updateStepCircle(viewStep4, stepNumber >= 4, context)
        updateStepCircle(viewStep5, stepNumber >= 5, context)
        
        // Update progress line width based on current step
        updateProgressLine(lineProgress, stepNumber, context)
        
        // Update label colors
        updateLabelColor(tvStep1Label, stepNumber >= 1, context)
        updateLabelColor(tvStep2Label, stepNumber >= 2, context)
        updateLabelColor(tvStep3Label, stepNumber >= 3, context)
        updateLabelColor(tvStep4Label, stepNumber >= 4, context)
        updateLabelColor(tvStep5Label, stepNumber >= 5, context)
    }
    
    private fun updateStepCircle(view: View?, isActive: Boolean, context: android.content.Context) {
        view?.background = if (isActive) {
            context.getDrawable(R.drawable.step_circle_active)
        } else {
            context.getDrawable(R.drawable.step_circle)
        }
    }
    
    private fun updateProgressLine(lineProgress: View?, stepNumber: Int, context: android.content.Context) {
        lineProgress?.let { line ->
            val parent = line.parent as? ConstraintLayout ?: return@let
            val layoutParams = ConstraintLayout.LayoutParams(line.layoutParams as ConstraintLayout.LayoutParams)
            
            // Clear existing constraints
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            layoutParams.endToStart = ConstraintLayout.LayoutParams.UNSET
            
            // Set the background color based on step number
            val backgroundDrawable = context.getDrawable(R.drawable.step_line_active)
            
            // Update end constraint based on step number to extend the line to the target step
            val targetStepId = when (stepNumber) {
                1 -> R.id.viewStep1
                2 -> R.id.viewStep2
                3 -> R.id.viewStep3
                4 -> R.id.viewStep4
                5 -> R.id.viewStep5
                else -> R.id.viewStep1
            }
            
            layoutParams.endToEnd = targetStepId
            layoutParams.startToStart = R.id.viewStep1
            
            // Set background
            line.background = backgroundDrawable
            
            // Apply the new layout params
            line.layoutParams = layoutParams
            
            // Force layout update
            parent.requestLayout()
            line.post {
                line.requestLayout()
            }
        }
    }
    
    private fun updateLabelColor(textView: TextView?, isActive: Boolean, context: android.content.Context) {
        textView?.setTextColor(context.getColor(if (isActive) R.color.primary else R.color.text_secondary))
    }
}
