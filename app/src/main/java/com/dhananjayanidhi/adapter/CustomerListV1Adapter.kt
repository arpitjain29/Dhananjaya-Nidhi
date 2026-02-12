package com.dhananjayanidhi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.models.customerlist.DatumCustomerListModel
import com.dhananjayanidhi.models.customerlistv1.DatumCustomerListV1Model
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface
import com.google.android.material.imageview.ShapeableImageView

class CustomerListV1Adapter(
    private var customerList: List<DatumCustomerListModel>,
    private val context: Context,
    private val customerClickInterface: CustomerClickInterface
) : RecyclerView.Adapter<CustomerListV1Adapter.ViewHolder>() {
    // Inside CustomerListV1Adapter class:
    fun updateList(newList: List<DatumCustomerListModel>) {
        // You might need to change your internal list in the adapter to a MutableList or update it
        customerList = newList // Assuming 'customerList' is the list used in the adapter
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = customerList[position]
        holder.customerName.text = customer.customerName
        holder.mobileNumber.text = customer.mobileNumber
        holder.address.text = customer.cutomerAddress?.address
        holder.accountNumber.text =
        String.format(
            "%s %s",
            context.getString(R.string.account_prefix),
            customer.account?.accountNumber ?: ""
        )
        if (customer.todayCollectionStatus == "1") {
            holder.checkIcon.visibility = View.VISIBLE
        } else {
            holder.checkIcon.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            customerClickInterface.onCustomerClick(customer.customerId, customer.accountId)
        }
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerName: AppCompatTextView = itemView.findViewById(R.id.tvCustomerName)
        val mobileNumber: AppCompatTextView = itemView.findViewById(R.id.tvCustomerMobileNumber)
        val address: AppCompatTextView = itemView.findViewById(R.id.tvCustomerAddress)
        val accountNumber: AppCompatTextView = itemView.findViewById(R.id.tvCustomerAccount)
        val checkIcon: ShapeableImageView = itemView.findViewById(R.id.ivCheckCustomer)
    }
}
