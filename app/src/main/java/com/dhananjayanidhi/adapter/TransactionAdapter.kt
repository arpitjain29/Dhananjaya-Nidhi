package com.dhananjayanidhi.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.TransactionLayoutBinding
import com.dhananjayanidhi.models.transaction.DatumTransactionModel
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface

class TransactionAdapter(
    private var datumTransactionModel: MutableList<DatumTransactionModel>,
    private val context: Activity,
    private val customerClickInterface: CustomerClickInterface,
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>(), Filterable {

    var searchArrayList: MutableList<DatumTransactionModel>? = null

    init {
        this.searchArrayList = datumTransactionModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TransactionLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < 0 || position >= datumTransactionModel.size) return
        
        val transaction = datumTransactionModel[position]
        
        holder.layoutBinding.tvNameTransaction.text = transaction.account?.customerName ?: ""
        holder.layoutBinding.tvDateTransaction.text = transaction.depositDate ?: ""
        holder.layoutBinding.tvAccountNumberTransaction.text = String.format(
            "%s %s",
            context.getString(R.string.account_prefix_equals),
            transaction.account?.accountNumber ?: ""
        )
        holder.layoutBinding.tvAmountTransaction.text = String.format(
            "%s %s",
            context.getString(R.string.rs),
            transaction.amount ?: "0"
        )
        
        // Set attractive status badge
        val status = transaction.status?.trim()?.lowercase() ?: ""
        setStatusBadge(holder.layoutBinding.tvStatusTransaction, status)
        holder.layoutBinding.llCustomerLayout.setOnClickListener {
            if (position < datumTransactionModel.size) {
                customerClickInterface.onCustomerClick(transaction.customerId, transaction.accountId)
            }
        }
    }

    override fun getItemCount(): Int {
        return datumTransactionModel.size
    }

    private fun setStatusBadge(textView: AppCompatTextView, status: String) {
        if (status.isEmpty()) {
            textView.text = "N/A"
            textView.setBackgroundResource(R.drawable.status_badge_default)
            textView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            return
        }
        val statusValue = status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val newReplace = statusValue.replace("_"," ")
         textView.text = newReplace
        
        when {
            status.contains("success", ignoreCase = true) || 
            status.contains("collected", ignoreCase = true) ||
            status.contains("completed", ignoreCase = true) ||
            status.contains("approved", ignoreCase = true) ||
            status.contains("active", ignoreCase = true) -> {
                textView.setBackgroundResource(R.drawable.status_badge_success)
                textView.setTextColor(ContextCompat.getColor(context, R.color.success))
            }
            status.contains("pending", ignoreCase = true) || 
            status.contains("processing", ignoreCase = true) || 
            status.contains("waiting", ignoreCase = true) ||
            status.contains("in progress", ignoreCase = true) -> {
                textView.setBackgroundResource(R.drawable.status_badge_pending)
                textView.setTextColor(Color.parseColor("#FF9800"))
            }
            status.contains("failed", ignoreCase = true) || 
            status.contains("error", ignoreCase = true) || 
            status.contains("rejected", ignoreCase = true) || 
            status.contains("cancelled", ignoreCase = true) ||
            status.contains("canceled", ignoreCase = true) ||
            status.contains("inactive", ignoreCase = true) -> {
                textView.setBackgroundResource(R.drawable.status_badge_failed)
                textView.setTextColor(ContextCompat.getColor(context, R.color.error))
            }
            else -> {
                textView.setBackgroundResource(R.drawable.status_badge_default)
                textView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }
        }
    }

    class ViewHolder(val layoutBinding: TransactionLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isEmpty()) {
                    filterResults.count = searchArrayList?.size ?: 0
                    filterResults.values = searchArrayList
                } else {
                    val resultsModel: MutableList<DatumTransactionModel> = ArrayList()
                    val searchStr = constraint.toString().lowercase()
                    searchArrayList?.forEach { itemsModel ->
                        val customerName = itemsModel.account?.customerName?.lowercase() ?: ""
                        if (customerName.contains(searchStr)) {
                            resultsModel.add(itemsModel)
                        }
                    }
                    filterResults.count = resultsModel.size
                    filterResults.values = resultsModel
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                datumTransactionModel = results.values as MutableList<DatumTransactionModel>
                notifyDataSetChanged()
            }
        }
    }
}