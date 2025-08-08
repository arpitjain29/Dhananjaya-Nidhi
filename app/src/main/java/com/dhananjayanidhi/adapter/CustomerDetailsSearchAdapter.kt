package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.CustomerDetailsLayoutBinding
import com.dhananjayanidhi.models.customerdetail.DdsAccountTransactionCustomerDetailModel

class CustomerDetailsSearchAdapter(
    private val ddsAccountTransactionCustomerDetailsModel: List<DdsAccountTransactionCustomerDetailModel>,
    private val context: Activity
) : RecyclerView.Adapter<CustomerDetailsSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomerDetailsLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layoutBinding.tvDateCustomerDetails.text =
            ddsAccountTransactionCustomerDetailsModel[position].depositDate
        holder.layoutBinding.tvAmountCustomerDetails.text = String.format(
            "%s %s",
            context.getString(R.string.rs),
            ddsAccountTransactionCustomerDetailsModel[position].amount
        )
    }

    override fun getItemCount(): Int {
        return ddsAccountTransactionCustomerDetailsModel.size
    }

    class ViewHolder(val layoutBinding: CustomerDetailsLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}