package com.kenzo.admarket.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kenzo.admarket.databinding.FragmentMyCompanyBinding
import com.kenzo.admarket.databinding.FragmentWalletBinding
import com.kenzo.admarket.utils.WalletManager

class MyCompanyFragment : Fragment() {

    private var _binding: FragmentMyCompanyBinding? = null
    private val binding get() = _binding!!

    private lateinit var walletManager: WalletManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCompanyBinding.inflate(inflater, container, false)
        walletManager = WalletManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
