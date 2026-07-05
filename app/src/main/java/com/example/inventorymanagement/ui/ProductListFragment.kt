package com.example.inventorymanagement.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorymanagement.R
import com.example.inventorymanagement.data.InventoryDatabase
import com.example.inventorymanagement.data.ProductRepository
import com.example.inventorymanagement.databinding.FragmentProductListBinding
import com.google.android.material.chip.Chip

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels {
        val database = InventoryDatabase.getDatabase(requireContext())
        InventoryViewModelFactory(ProductRepository(database.productDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        val adapter = ProductAdapter { product ->
            val bundle = Bundle().apply {
                putInt("productId", product.id)
                putString("title", "Edit Product")
            }
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment, bundle)
        }

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter

        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                binding.emptyStateContainer.visibility = View.VISIBLE
                binding.rvProducts.visibility = View.GONE
                
                val query = binding.etSearch.text.toString()
                if (query.isNotEmpty()) {
                    binding.tvEmptyTitle.text = "No matching products found"
                    binding.tvEmptyMessage.text = "Try adjusting your search or filters"
                    binding.btnAddFirstProduct.visibility = View.GONE
                } else {
                    binding.tvEmptyTitle.text = "No Products Found"
                    binding.tvEmptyMessage.text = "Start by adding new items to your inventory."
                    binding.btnAddFirstProduct.visibility = View.VISIBLE
                }
            } else {
                binding.emptyStateContainer.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
                adapter.submitList(products)
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            updateCategoryChips(categories)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSort.setOnClickListener { view ->
            showSortMenu(view)
        }

        binding.fabAddProduct.setOnClickListener {
            navigateToAdd()
        }

        binding.btnAddFirstProduct.setOnClickListener {
            navigateToAdd()
        }
    }

    private fun updateCategoryChips(categories: List<String>) {
        val allChip = binding.chipAll
        binding.chipGroupCategories.removeAllViews()
        binding.chipGroupCategories.addView(allChip)

        categories.forEach { category ->
            val chip = Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle)
            chip.text = category
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setCategoryFilter(category)
                } else if (!binding.chipGroupCategories.children.any { (it as Chip).isChecked }) {
                    binding.chipAll.isChecked = true
                    viewModel.setCategoryFilter(null)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
        
        allChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setCategoryFilter(null)
            }
        }
    }

    private fun showSortMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.menu_sort, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort_name -> viewModel.setSortOrder(SortOrder.NAME)
                R.id.sort_price -> viewModel.setSortOrder(SortOrder.PRICE)
                R.id.sort_quantity -> viewModel.setSortOrder(SortOrder.QUANTITY)
            }
            true
        }
        popup.show()
    }

    private fun navigateToAdd() {
        val bundle = Bundle().apply {
            putInt("productId", -1)
            putString("title", "Add Product")
        }
        findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
