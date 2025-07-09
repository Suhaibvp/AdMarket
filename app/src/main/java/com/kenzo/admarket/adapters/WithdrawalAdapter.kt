package com.kenzo.admarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenzo.admarket.R
import com.kenzo.admarket.model.Withdrawal

class WithdrawalAdapter(private val withdrawals: List<Withdrawal>) :
    RecyclerView.Adapter<WithdrawalAdapter.WithdrawalViewHolder>() {

    class WithdrawalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountText: TextView = itemView.findViewById(R.id.withdrawAmount)
        val statusText: TextView = itemView.findViewById(R.id.withdrawStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_withdrawal_row, parent, false)
        return WithdrawalViewHolder(view)
    }

    override fun onBindViewHolder(holder: WithdrawalViewHolder, position: Int) {
        val item = withdrawals[position]
        holder.amountText.text = "₹${item.amount}"
        holder.statusText.text = item.status
    }

    override fun getItemCount() = withdrawals.size
}
