package com.example.inventorymanagement.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanagement.R
import com.example.inventorymanagement.data.Product
import com.example.inventorymanagement.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.*

class ProductAdapter(private val onItemClicked: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductCategory.text = product.category
            
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            binding.tvProductPrice.text = format.format(product.price)
            
            binding.tvProductQuantity.text = "${product.quantity} units"
            
            val context = itemView.context
            when {
                product.quantity == 0 -> {
                    binding.tvStockStatus.text = "Out of Stock"
                    binding.cvStockBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_errorContainer))
                    binding.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_error))
                }
                product.quantity <= product.minQuantity -> {
                    binding.tvStockStatus.text = "Low Stock"
                    binding.cvStockBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.warningContainer))
                    binding.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.warning))
                }
                else -> {
                    binding.tvStockStatus.text = "In Stock"
                    binding.cvStockBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.successContainer))
                    binding.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.success))
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}
