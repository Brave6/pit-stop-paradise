package com.seth.pitstopparadise.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seth.pitstopparadise.databinding.ItemBookingBinding
import com.seth.pitstopparadise.domain.model.Booking

class BookingHistoryAdapter :
    ListAdapter<Booking, BookingHistoryAdapter.BookingViewHolder>(BookingDiffCallback()) {

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.textName.text = booking.name
            binding.textProductName.text = booking.product?.title
            binding.textDate.text = booking.date
            binding.textTime.text = booking.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
    override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem._id == newItem._id // Assuming Booking has an `id` field
    }

    override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem == newItem
    }
}
