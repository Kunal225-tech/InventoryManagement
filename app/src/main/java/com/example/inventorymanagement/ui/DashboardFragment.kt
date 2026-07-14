package com.example.inventorymanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorymanagement.R
import com.example.inventorymanagement.data.InventoryDatabase
import com.example.inventorymanagement.data.ProductRepository
import com.example.inventorymanagement.data.UserRepository
import com.example.inventorymanagement.data.UserSession
import com.example.inventorymanagement.databinding.FragmentDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels {
        val database = InventoryDatabase.getDatabase(requireContext())
        InventoryViewModelFactory(ProductRepository(database.productDao()))
    }

    private val authViewModel: AuthViewModel by viewModels {
        val database = InventoryDatabase.getDatabase(requireContext())
        AuthViewModelFactory(UserRepository(database.userDao()), UserSession(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        viewModel.totalProductCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalProducts.text = "$count Products"
        }

        viewModel.totalInventoryValue.observe(viewLifecycleOwner) { value ->
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            binding.tvTotalValue.text = format.format(value ?: 0.0)
        }

        // Combine low stock and out of stock for the message
        viewModel.lowStockCount.observe(viewLifecycleOwner) { lowCount ->
            updateStatusMessage(lowCount, viewModel.outOfStockCount.value ?: 0)
            binding.tvLowStock.text = "$lowCount Items"
        }

        viewModel.outOfStockCount.observe(viewLifecycleOwner) { outCount ->
            updateStatusMessage(viewModel.lowStockCount.value ?: 0, outCount)
        }

        authViewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            event?.let {
                when (it) {
                    is AuthViewModel.NavigationEvent.NavigateToLogin -> {
                        findNavController().navigate(R.id.action_global_loginFragment)
                        authViewModel.onNavigationHandled()
                    }
                    else -> {}
                }
            }
        }

        binding.toolbar.inflateMenu(R.menu.menu_dashboard)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    showLogoutConfirmation()
                    true
                }
                else -> false
            }
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Logout") { _, _ ->
                authViewModel.logout()
            }
            .show()
    }

    private fun updateStatusMessage(lowCount: Int, outCount: Int) {
        when {
            outCount > 0 -> {
                binding.tvStatusMessage.text = "$outCount items are Out of Stock!"
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_error))
            }
            lowCount > 0 -> {
                binding.tvStatusMessage.text = "$lowCount products are running low"
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning))
            }
            else -> {
                binding.tvStatusMessage.text = "Inventory is healthy"
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.success))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
