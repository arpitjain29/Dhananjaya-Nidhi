package com.dhananjayanidhi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.models.monthlycollection.DatewiseCollectionItem

class MonthlyCollectionDateAdapter(
    private val items: List<DatewiseCollectionItem>
) : RecyclerView.Adapter<MonthlyCollectionDateAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_collection_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: AppCompatTextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: AppCompatTextView = itemView.findViewById(R.id.tvAmount)

        fun bind(item: DatewiseCollectionItem) {
            val context = itemView.context
            tvDate.text = item.date ?: ""
            tvAmount.text = context.getString(R.string.rupee_amount_format, item.amount ?: 0)
        }
    }
}

