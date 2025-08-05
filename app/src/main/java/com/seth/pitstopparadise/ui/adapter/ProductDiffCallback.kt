package com.seth.pitstopparadise.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.seth.pitstopparadise.domain.model.Product

object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
