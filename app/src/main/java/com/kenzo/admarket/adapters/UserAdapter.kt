package com.kenzo.admarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenzo.admarket.R
import com.kenzo.admarket.model.User

class UserAdapter(
    private val users: List<User>,
    private val onEditClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val index = view.findViewById<TextView>(R.id.tv_index)
        val name = view.findViewById<TextView>(R.id.tv_name)
        val referral = view.findViewById<TextView>(R.id.tv_referral)
        val mobile = view.findViewById<TextView>(R.id.tv_mobile)
        val btnEdit = view.findViewById<Button>(R.id.btn_editUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_members, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.index.text = (position + 1).toString()
        holder.name.text = user.name
        holder.referral.text = user.sponsorId
        holder.mobile.text = user.contact
        holder.btnEdit.setOnClickListener { onEditClick(user) }
    }

    override fun getItemCount(): Int = users.size
}
