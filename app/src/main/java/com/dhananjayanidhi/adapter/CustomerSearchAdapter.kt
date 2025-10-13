package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.databinding.CustomerSearchLayoutBinding
import com.dhananjayanidhi.models.customersearch.DatumCustomerSearchModel
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface

class CustomerSearchAdapter(
    private val datumCustomerSearchModel: List<DatumCustomerSearchModel>,
    private val context: Activity,
    private val customerClickInterface: CustomerClickInterface,
) : RecyclerView.Adapter<CustomerSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomerSearchLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layoutBinding.tvSearchName.text = datumCustomerSearchModel[position].customerName
        holder.layoutBinding.tvSearchName.setOnClickListener {
            customerClickInterface.onCustomerClick(position)
        }
    }

    override fun getItemCount(): Int {
        return datumCustomerSearchModel.size
    }

    class ViewHolder(val layoutBinding: CustomerSearchLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}