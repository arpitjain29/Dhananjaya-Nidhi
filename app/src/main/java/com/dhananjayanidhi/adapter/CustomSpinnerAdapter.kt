package com.dhananjayanidhi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.models.depositscheme.SchemeDepositSchemeModel

class CustomSpinnerAdapter (private val context: Context, private val items: List<SchemeDepositSchemeModel>)
    : BaseAdapter() {

    data class SpinnerItem(val iconRes: Int, val text: String)

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false)
            holder = ViewHolder()
            holder.text = view.findViewById(R.id.spinner_text)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val item = items[position]
        holder.text.text = item.schemeName

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }

    private class ViewHolder {
        lateinit var text: TextView
    }
}