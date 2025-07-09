package com.kenzo.admarket.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenzo.admarket.databinding.ItemScreenshotBinding
import com.kenzo.admarket.ui.admin.ScreenshotItem

class ScreenshotAdapter(private val items: List<ScreenshotItem>) :
    RecyclerView.Adapter<ScreenshotAdapter.ScreenshotViewHolder>() {

    inner class ScreenshotViewHolder(val binding: ItemScreenshotBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val binding = ItemScreenshotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScreenshotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val bitmap = BitmapFactory.decodeByteArray(items[position].imageBytes, 0, items[position].imageBytes.size)
        holder.binding.screenshotImage.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int = items.size
}
