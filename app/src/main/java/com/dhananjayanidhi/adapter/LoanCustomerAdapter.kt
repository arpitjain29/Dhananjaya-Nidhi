package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.LoanCustomerLayoutBinding
import com.dhananjayanidhi.models.loanlist.DatumLoanListModel
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface

class LoanCustomerAdapter(
    private val datumLoanListModel: List<DatumLoanListModel>, private val context: Activity,
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
        holder.layoutBinding.tvNameCustomerNameLoan.text = datumLoanListModel[position].customerName
        holder.layoutBinding.tvEmiAmountLoan.text =
            String.format(
                "%s %s %s",
                "Emi: ",
                context.getString(R.string.rs),
                datumLoanListModel[position].emi
            )
        holder.layoutBinding.tvLoanAmount.text =
            String.format(
                "%s %s %s",
                "Loan Amount = ",
                context.getString(R.string.rs),
                datumLoanListModel[position].loanAmount
            )
        if (datumLoanListModel[position].loanStartDate != null) {
            holder.layoutBinding.tvLoanDate.text =
                String.format(
                    "%s %s",
                    "Loan Date: ",
                    CommonFunction.changeDateFormatFromAnother(datumLoanListModel[position].loanStartDate)
                )
        }
        holder.layoutBinding.tvPaidAmountLoan.text = String.format(
            "%s %s %s",
            "Paid Amount: ",
            context.getString(R.string.rs),
            datumLoanListModel[position].paidAmount
        )
        holder.layoutBinding.tvPendingOutstandingAmountLoan.text = String.format(
            "%s %s %s", "Pending/outstanding amount: ", context.getString(R.string.rs),
            datumLoanListModel[position].outstandingAmount
        )
        holder.layoutBinding.llLoanCustomer.setOnClickListener {
            loanClickInterface.onLoanClick(position)
        }
    }

    override fun getItemCount(): Int {
        return datumLoanListModel.size
    }

    class ViewHolder(val layoutBinding: LoanCustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}