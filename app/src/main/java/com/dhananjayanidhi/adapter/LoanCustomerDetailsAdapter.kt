package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.LoanCustomerDetailsLayoutBinding
import com.dhananjayanidhi.models.loandetails.TransactionLoanDetailsModel
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface

class LoanCustomerDetailsAdapter(private val mList: List<TransactionLoanDetailsModel>, private val context: Activity,
                                 private val loanClickInterface: LoanClickInterface,
): RecyclerView.Adapter<LoanCustomerDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LoanCustomerDetailsLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layoutBinding.tvLoanAmountDetails.text = String.format(
            "%s %s",
            context.getString(R.string.rs),mList[position].amount)
        holder.layoutBinding.tvLoanDateDetails.text = CommonFunction.changeDateFormatFromAnother(mList[position].depositeDate)
        holder.layoutBinding.llLoanCustomer.setOnClickListener {
            loanClickInterface.onLoanClick(position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val layoutBinding: LoanCustomerDetailsLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}