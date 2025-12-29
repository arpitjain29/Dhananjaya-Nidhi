package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.CustomerLayoutBinding
import com.dhananjayanidhi.models.customerlist.DatumCustomerListModel
import com.dhananjayanidhi.models.customerlistv1.DatumCustomerListV1Model
import com.dhananjayanidhi.models.customersearch.DatumCustomerSearchModel
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface

class CustomerAdapter(
    private val datumCustomerList: List<DatumCustomerSearchModel>,
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
        if (position < 0 || position >= datumCustomerList.size) return
        
        val customer = datumCustomerList[position]
        
        holder.layoutBinding.tvCustomerName.text = customer.customerName ?: ""
        holder.layoutBinding.tvCustomerAccount.text = String.format(
            "%s %s",
            context.getString(R.string.rs),
            customer.collectionAmount ?: "0"
        )
        holder.layoutBinding.tvCustomerAddress.text = customer.cutomerAddress?.fullAddress ?: ""
        holder.layoutBinding.tvCustomerMobileNumber.text = customer.mobileNumber ?: ""
        holder.layoutBinding.tvAccountNumber.text = String.format(
            "%s %s",
            context.getString(R.string.account_prefix),
            customer.account?.accountNumber ?: ""
        )
        holder.layoutBinding.llCustomerLayout.setOnClickListener {
            if (position < datumCustomerList.size) {
                customerClickInterface.onCustomerClick(position)
            }
        }
        holder.layoutBinding.ivCheckCustomer.visibility = 
            if (customer.todayCollectionStatus == "yes") View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return datumCustomerList.size
    }

    class ViewHolder(val layoutBinding: CustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}