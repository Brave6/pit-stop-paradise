package com.seth.pitstopparadise.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seth.pitstopparadise.databinding.ItemOfferBinding
import com.seth.pitstopparadise.domain.model.Offer
import com.seth.pitstopparadise.domain.model.Product

class OfferAdapter(
    private val onBookNowClick: (Product) -> Unit
) : ListAdapter<Offer, OfferAdapter.OfferViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OfferViewHolder(private val binding: ItemOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer) {
            binding.textTitle.text = offer.title
            binding.textDescription.text = offer.description
            binding.textDiscount.text = "${offer.discountPercent}% OFF"

            // Format validUntil date
            val formattedDate = try {
                val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
                parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val date = parser.parse(offer.validUntil)

                val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                formatter.format(date!!)
            } catch (e: Exception) {
                offer.validUntil // fallback if parsing fails
            }

            binding.textValidUntil.text = "Valid until: $formattedDate"

            binding.buttonBookNow.setOnClickListener {
                if (offer.product == null) {
                    android.util.Log.e("OfferAdapter", "Product is null for offer: ${offer.title}")
                } else {
                    val originalProduct = offer.product
                    val discountedPrice = originalProduct.price - (originalProduct.price * offer.discountPercent / 100)

                    val productWithDiscount = originalProduct.copy(
                        discountedPrice = discountedPrice
                    )

                    onBookNowClick(productWithDiscount)
                }
            }


        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(old: Offer, new: Offer) = old.id == new.id
        override fun areContentsTheSame(old: Offer, new: Offer) = old == new
    }
}
