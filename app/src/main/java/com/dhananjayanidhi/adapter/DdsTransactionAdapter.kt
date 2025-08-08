package com.dhananjayanidhi.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.databinding.DdsTransactionLayoutBinding

class DdsTransactionAdapter(private val mList: List<String>, private val context: Activity
): RecyclerView.Adapter<DdsTransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DdsTransactionLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 6
    }

    class ViewHolder(val layoutBinding: DdsTransactionLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root)
}