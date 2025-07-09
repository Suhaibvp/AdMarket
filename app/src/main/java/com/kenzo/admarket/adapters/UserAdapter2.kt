package com.kenzo.admarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenzo.admarket.R
import com.kenzo.admarket.model.User

class UserAdapter2(
    private val users: List<User>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<UserAdapter2.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val userMobNumber: TextView=itemView.findViewById(R.id.tv_mobile)
        val userEmail: TextView=itemView.findViewById(R.id.tv_user_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = user.name
        holder.userMobNumber.text=user.contact
        holder.userEmail.text=user.email
        holder.itemView.setOnClickListener {
            onClick(user.email)
        }
    }
}
