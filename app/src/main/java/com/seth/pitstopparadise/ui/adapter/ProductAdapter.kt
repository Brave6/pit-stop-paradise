package com.seth.pitstopparadise.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seth.pitstopparadise.R
import com.seth.pitstopparadise.databinding.ItemProductBinding
import com.seth.pitstopparadise.domain.model.Product

class ProductAdapter(
    private val onBookClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) = with(binding) {
            textTitle.text = product.title
            textPrice.text = "₱${product.price} / ${product.duration}"
            textDescription.text = product.description

            Glide.with(imageProduct.context)
                .load(product.imageUrl)
                .into(imageProduct)

            // Accessibility: Combine title, price, and description
            val context = root.context
            val accessibilityText = "${product.title}, " +
                    "Price: ₱${product.price} for ${product.duration}, " +
                    "${product.description}"

            productCard.contentDescription = accessibilityText

            // Make "Book Now" accessible too
            buttonBook.contentDescription = context.getString(
                R.string.book_now_for,
                product.title
            )

            buttonBook.setOnClickListener {
                onBookClick(product)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
