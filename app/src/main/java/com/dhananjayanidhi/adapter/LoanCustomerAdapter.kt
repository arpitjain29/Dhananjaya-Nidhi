package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.databinding.LoanCustomerLayoutBinding
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface

class LoanCustomerAdapter(private val mList: List<String>, private val context: Activity,
    private val loanClickInterface: LoanClickInterface,
): RecyclerView.Adapter<LoanCustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LoanCustomerLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layoutBinding.llLoanCustomer.setOnClickListener {
            loanClickInterface.onLoanClick(position)
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    class ViewHolder(val layoutBinding: LoanCustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}