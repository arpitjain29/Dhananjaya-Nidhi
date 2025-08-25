package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.CustomerLayoutBinding
import com.dhananjayanidhi.models.customerlistv1.DatumCustomerListV1Model
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface

class CustomerAdapter(
    private val datumCustomerList: List<DatumCustomerListV1Model>,
    private val context: Activity,
    private val customerClickInterface: CustomerClickInterface,
) : RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomerLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layoutBinding.tvCustomerName.text = datumCustomerList[position].customerName
        holder.layoutBinding.tvCustomerAccount.text = String.format(
            "%s %s",
            context.getString(R.string.rs),
            datumCustomerList[position].collectionAmount
        )
        holder.layoutBinding.tvCustomerAddress.text =
            datumCustomerList[position].cutomerAddress?.fullAddress
        holder.layoutBinding.tvCustomerMobileNumber.text = datumCustomerList[position].mobileNumber
        holder.layoutBinding.tvAccountNumber.text =String.format(
            "%s %s", "A/C : ",
            datumCustomerList[position].account?.accountNumber)
        holder.layoutBinding.llCustomerLayout.setOnClickListener {
            customerClickInterface.onCustomerClick(position)
        }
        if (datumCustomerList[position].todayCollectionStatus == "yes"){
            holder.layoutBinding.ivCheckCustomer.visibility = View.VISIBLE
        }else{
            holder.layoutBinding.ivCheckCustomer.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return datumCustomerList.size
    }

    class ViewHolder(val layoutBinding: CustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}