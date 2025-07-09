package com.kenzo.admarket.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenzo.admarket.R
import com.kenzo.admarket.model.Transaction

class TransactionAdapter(
    private val transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvAmount: TextView = view.findViewById(R.id.tvTransactionAmount)
        private val tvDate: TextView = view.findViewById(R.id.tvTransactionDate)
        private val tvStatus: TextView = view.findViewById(R.id.tvTransactionStatus)

        fun bind(transaction: Transaction) {
            tvAmount.text = "₹${transaction.amount}"
            tvDate.text = transaction.date
            tvStatus.text = transaction.status

            // Optional: color status text
            tvStatus.setTextColor(
                when (transaction.status.lowercase()) {
                    "completed" -> Color.parseColor("#388E3C")
                    "pending" -> Color.parseColor("#F57C00")
                    "failed" -> Color.parseColor("#D32F2F")
                    else -> Color.DKGRAY
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size
}
