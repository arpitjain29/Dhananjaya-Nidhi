package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.CustomerLayoutBinding
import com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface

class PendingMembersAdapter(
    private var memberList: List<DatumMemberDraftModel>,
    private val context: Activity,
    private val memberClickInterface: CustomerClickInterface,
) : RecyclerView.Adapter<PendingMembersAdapter.ViewHolder>() {
    
    fun updateList(newList: List<DatumMemberDraftModel>) {
        memberList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomerLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < 0 || position >= memberList.size) return

        val member = memberList[position]

        holder.layoutBinding.tvCustomerName.text = member.customerName?.trim() ?: ""
        holder.layoutBinding.tvCustomerAccount.text = String.format(
            "%s %s",
            context.getString(R.string.account_prefix),
            member.customerId ?: ""
        )
        holder.layoutBinding.tvCustomerAddress.text = member.acType ?: ""
        holder.layoutBinding.tvCustomerMobileNumber.text = member.mobileNumber ?: ""
        holder.layoutBinding.tvAccountNumber.visibility = View.GONE
        holder.layoutBinding.tvAccountNumber.text = String.format(
            "%s %s",
            context.getString(R.string.account_prefix),
            member.customerId ?: ""
        )
        holder.layoutBinding.llCustomerLayout.setOnClickListener {
            if (position < memberList.size) {
                memberClickInterface.onCustomerClick(position)
            }
        }
        holder.layoutBinding.ivCheckCustomer.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    class ViewHolder(val layoutBinding: CustomerLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}

