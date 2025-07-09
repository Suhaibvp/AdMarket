package com.kenzo.admarket.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kenzo.admarket.adapters.TransactionAdapter
import com.kenzo.admarket.databinding.FragmentUserTransactionBinding
import com.kenzo.admarket.model.Transaction


class UserTransactionFragment : Fragment() {

    private var _binding: FragmentUserTransactionBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dummyTransactions = listOf(
            Transaction("1", "2025-06-10", 250.0, "Completed"),
            Transaction("2", "2025-06-09", 100.0, "Pending"),
            Transaction("3", "2025-06-08", 500.0, "Failed")
        )

        val adapter = TransactionAdapter(dummyTransactions)
        binding.rvTrips.adapter = adapter
        binding.rvTrips.layoutManager = LinearLayoutManager(requireContext())

    }
}