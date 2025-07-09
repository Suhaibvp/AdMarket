package com.kenzo.admarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.kenzo.admarket.R

class CouponAdapter(
    private val coupons: List<DocumentSnapshot>,
    private val onStatusChange: (DocumentSnapshot) -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    inner class CouponViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val code = view.findViewById<TextView>(R.id.tvCouponCode)
        val amount = view.findViewById<TextView>(R.id.tvCouponAmount)
        val status = view.findViewById<TextView>(R.id.tvCouponStatus)
        val editButton = view.findViewById<Button>(R.id.btnEditStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coupon, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val doc = coupons[position]
        holder.code.text = doc.getString("code")
        holder.amount.text = "₹${doc.getLong("amount") ?: 0}"
        holder.status.text = "${doc.getString("status")}"

        holder.editButton.setOnClickListener {
            onStatusChange(doc)
        }
    }

    override fun getItemCount() = coupons.size
}
