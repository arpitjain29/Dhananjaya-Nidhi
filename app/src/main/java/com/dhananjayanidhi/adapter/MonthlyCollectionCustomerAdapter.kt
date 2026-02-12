package com.dhananjayanidhi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.models.monthlycollection.CustomerwiseCollectionItem

class MonthlyCollectionCustomerAdapter(
    private val items: List<CustomerwiseCollectionItem>
) : RecyclerView.Adapter<MonthlyCollectionCustomerAdapter.CustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_collection_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCustomerName: AppCompatTextView = itemView.findViewById(R.id.tvCustomerName)
        private val tvAmount: AppCompatTextView = itemView.findViewById(R.id.tvAmount)

        fun bind(item: CustomerwiseCollectionItem) {
            val context = itemView.context
            tvCustomerName.text = item.customerName ?: ""
            tvAmount.text = context.getString(R.string.rupee_amount_format, item.amount ?: 0)
        }
    }
}

