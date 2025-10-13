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
        holder.layoutBinding.tvNameCustomerNameLoan.text = datumLoanListModel[position].customerName
        holder.layoutBinding.tvEmiAmountLoan.text =
            String.format(
                "%s %s %s",
                "Emi: ",
                context.getString(R.string.rs),
                datumLoanListModel[position].emi
            )
        holder.layoutBinding.tvLoanAccountNo.text =
            String.format(
                "%s %s",
                "A/C : ",
                datumLoanListModel[position].accountNumber
            )
//        if (datumLoanListModel[position].loanStartDate != null) {
//            holder.layoutBinding.tvLoanDate.text =
//                String.format(
//                    "%s %s",
//                    "Loan Date: ",
//                    CommonFunction.changeDateFormatFromAnother(datumLoanListModel[position].loanStartDate)
//                )
//        }
//        holder.layoutBinding.tvPaidAmountLoan.text = String.format(
//            "%s %s %s",
//            "Paid Amount: ",
//            context.getString(R.string.rs),
//            datumLoanListModel[position].paidAmount
//        )
//        holder.layoutBinding.tvPendingOutstandingAmountLoan.text = String.format(
//            "%s %s %s", "Pending/outstanding amount: ", context.getString(R.string.rs),
//            datumLoanListModel[position].outstandingAmount
//        )
        holder.layoutBinding.llLoanCustomer.setOnClickListener {
            loanClickInterface.onLoanClick(position)
        }
        if (datumLoanListModel[position].todayCollectionStatus == "yes"){
            holder.layoutBinding.ivCheckCustomerLoan.visibility = View.VISIBLE
        }else{
            holder.layoutBinding.ivCheckCustomerLoan.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return datumLoanListModel.size
    }

    class ViewHolder(val layoutBinding: LoanCustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}