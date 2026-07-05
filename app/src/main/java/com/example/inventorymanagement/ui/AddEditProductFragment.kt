package com.example.inventorymanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorymanagement.data.InventoryDatabase
import com.example.inventorymanagement.data.Product
import com.example.inventorymanagement.data.ProductRepository
import com.example.inventorymanagement.databinding.FragmentAddEditProductBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AddEditProductFragment : Fragment() {

    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels {
        val database = InventoryDatabase.getDatabase(requireContext())
        InventoryViewModelFactory(ProductRepository(database.productDao()))
    }

    private var currentProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val productId = arguments?.getInt("productId", -1) ?: -1
        val title = arguments?.getString("title") ?: "Add Product"
        binding.tvTitle.text = title

        if (productId != -1) {
            viewModel.getProductById(productId).observe(viewLifecycleOwner) { product ->
                product?.let {
                    currentProduct = it
                    binding.etProductName.setText(it.name)
                    binding.etProductCategory.setText(it.category)
                    binding.etProductQuantity.setText(it.quantity.toString())
                    binding.etMinQuantity.setText(it.minQuantity.toString())
                    binding.etProductPrice.setText(it.price.toString())
                    binding.btnDeleteProduct.visibility = View.VISIBLE
                }
            }
        }

        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }

        binding.btnDeleteProduct.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${currentProduct?.name}? This action cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                currentProduct?.let {
                    viewModel.delete(it)
                    showSnackbar("Product deleted successfully")
                    findNavController().navigateUp()
                }
            }
            .show()
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString()
        val category = binding.etProductCategory.text.toString()
        val quantityStr = binding.etProductQuantity.text.toString()
        val minQuantityStr = binding.etMinQuantity.text.toString()
        val priceStr = binding.etProductPrice.text.toString()

        var isValid = true

        if (name.isBlank()) {
            binding.tilName.error = "Name is required"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (category.isBlank()) {
            binding.tilCategory.error = "Category is required"
            isValid = false
        } else {
            binding.tilCategory.error = null
        }

        val quantity = quantityStr.toIntOrNull() ?: run {
            binding.tilQuantity.error = "Invalid"
            isValid = false
            0
        }
        if (quantity < 0) {
            binding.tilQuantity.error = "Must be positive"
            isValid = false
        }

        val price = priceStr.toDoubleOrNull() ?: run {
            binding.tilPrice.error = "Invalid price"
            isValid = false
            0.0
        }
        if (price < 0) {
            binding.tilPrice.error = "Invalid price"
            isValid = false
        }

        if (!isValid) return

        val minQuantity = minQuantityStr.toIntOrNull() ?: 0

        val product = Product(
            id = if (currentProduct != null) currentProduct!!.id else 0,
            name = name,
            category = category,
            quantity = quantity,
            price = price,
            minQuantity = minQuantity
        )

        if (currentProduct == null) {
            viewModel.insert(product)
            showSnackbar("Product added successfully")
        } else {
            viewModel.update(product)
            showSnackbar("Product updated successfully")
        }
        findNavController().navigateUp()
    }

    private fun showSnackbar(message: String) {
        // Since we are navigating up, we should show the snackbar on the previous screen's view or coordinate with it.
        // For simplicity in this demo, showing it before navigation is often too fast, 
        // but finding a view in the parent activity or the next fragment is better.
        // Actually, many apps show a Toast or use a shared ViewModel for such messages.
        Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
