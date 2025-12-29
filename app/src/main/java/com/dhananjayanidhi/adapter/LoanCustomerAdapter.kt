package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.LoanCustomerLayoutBinding
import com.dhananjayanidhi.models.loanlist.DatumLoanListModel
import com.dhananjayanidhi.models.loansearch1.DatumLoanSearch1Model
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface

class LoanCustomerAdapter(
    private val datumLoanListModel: List<DatumLoanSearch1Model>, private val context: Activity,
    private val loanClickInterface: LoanClickInterface,
) : RecyclerView.Adapter<LoanCustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LoanCustomerLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < 0 || position >= datumLoanListModel.size) return
        
        val loan = datumLoanListModel[position]
        
        holder.layoutBinding.tvNameCustomerNameLoan.text = loan.customerName ?: ""
        holder.layoutBinding.tvEmiAmountLoan.text = String.format(
            "%s %s %s",
            context.getString(R.string.emi_prefix),
            context.getString(R.string.rs),
            loan.emi ?: "0"
        )
        holder.layoutBinding.tvLoanAccountNo.text = String.format(
            "%s %s",
            context.getString(R.string.account_prefix),
            loan.accountNumber ?: ""
        )
        holder.layoutBinding.llLoanCustomer.setOnClickListener {
            if (position < datumLoanListModel.size) {
                loanClickInterface.onLoanClick(position)
            }
        }
        holder.layoutBinding.ivCheckCustomerLoan.visibility = 
            if (loan.todayCollectionStatus == "yes") View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return datumLoanListModel.size
    }

    class ViewHolder(val layoutBinding: LoanCustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}