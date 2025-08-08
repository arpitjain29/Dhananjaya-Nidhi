package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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
        holder.layoutBinding.tvNameTransaction.text =
            datumTransactionModel[position].account?.customerName
        holder.layoutBinding.tvDateTransaction.text = datumTransactionModel[position].depositDate
        holder.layoutBinding.tvAccountNumberTransaction.text = String.format(
            "%s %s", "A/C = ",
            datumTransactionModel[position].account?.accountNumber
        )
        holder.layoutBinding.tvAmountTransaction.text = String.format(
            "%s %s",
            context.getString(R.string.rs),
            datumTransactionModel[position].amount
        )
        holder.layoutBinding.tvStatusTransaction.text = datumTransactionModel[position].status
        holder.layoutBinding.llCustomerLayout.setOnClickListener {
            customerClickInterface.onCustomerClick(position)
        }
    }

    override fun getItemCount(): Int {
        return datumTransactionModel.size
    }

    class ViewHolder(val layoutBinding: TransactionLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isEmpty()) {
                    filterResults.count = searchArrayList!!.size
                    filterResults.values = searchArrayList
                } else {
                    val resultsModel: MutableList<DatumTransactionModel> = ArrayList()
                    val searchStr = constraint.toString().lowercase()
                    for (itemsModel in searchArrayList!!) {
                        if (itemsModel.account!!.customerName!!.lowercase().contains(searchStr)) {
                            resultsModel.add(itemsModel)
                        }
                        filterResults.count = resultsModel.size
                        filterResults.values = resultsModel
                    }
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