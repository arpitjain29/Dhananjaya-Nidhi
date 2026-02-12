package com.dhananjayanidhi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.models.agentpayout.AgentMonthlyPayoutItem

class MonthlyPayoutAdapter(
    private val items: List<AgentMonthlyPayoutItem>
) : RecyclerView.Adapter<MonthlyPayoutAdapter.MonthlyPayoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyPayoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_payout, parent, false)
        return MonthlyPayoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthlyPayoutViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class MonthlyPayoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMonthName: AppCompatTextView =
            itemView.findViewById(R.id.tvMonthName)
        private val tvTotalCollection: AppCompatTextView =
            itemView.findViewById(R.id.tvTotalCollection)
        private val tvTotalPayout: AppCompatTextView =
            itemView.findViewById(R.id.tvTotalPayout)

        fun bind(item: AgentMonthlyPayoutItem) {
            val context = itemView.context
            tvMonthName.text = item.monthName ?: ""

            val collectionText = context.getString(
                R.string.monthly_collection_format,
                item.totalMonthCollection ?: 0
            )
            tvTotalCollection.text = collectionText

            val payoutText = context.getString(
                R.string.monthly_payout_format,
                item.totalPayout ?: 0
            )
            tvTotalPayout.text = payoutText
        }
    }
}

