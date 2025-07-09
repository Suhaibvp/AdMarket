package com.kenzo.admarket.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kenzo.admarket.databinding.ItemTransactionAdminBinding
import com.kenzo.admarket.databinding.ItemTransactionBinding
import com.kenzo.admarket.model.Transaction

class AdminTransactionAdapter(
    private val context: Context,
    transactions: List<Transaction>
) : RecyclerView.Adapter<AdminTransactionAdapter.TransactionViewHolder>() {

    private val transactionList = transactions.toMutableList()

    inner class TransactionViewHolder(val binding: ItemTransactionAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.binding.tvAmount.text = "₹${transaction.amount}"
        holder.binding.tvStatus.text = transaction.status

        holder.binding.btnAction.text = "Edit Status"
        holder.binding.btnAction.setOnClickListener {
            showStatusDialog(transaction, position)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    private fun showStatusDialog(transaction: Transaction, position: Int) {
        val options = listOf("pending", "completed", "rejected")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Status")

        val spinner = Spinner(context)
        spinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)

        val currentIndex = options.indexOf(transaction.status)
        if (currentIndex != -1) spinner.setSelection(currentIndex)

        builder.setView(spinner)
        builder.setPositiveButton("Update") { _, _ ->
            val selectedStatus = spinner.selectedItem.toString()
            updateStatus(transaction.id, selectedStatus, position)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateStatus(transactionId: String, newStatus: String, position: Int) {
        FirebaseFirestore.getInstance()
            .collection("withdrawals")
            .document(transactionId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                // ✅ Update the local list and UI
                transactionList[position] = transactionList[position].copy(status = newStatus)
                notifyItemChanged(position)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }
}
