//package com.kenzo.admarket.ui.user
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.kenzo.admarket.databinding.FragmentUploadImageBinding
//
//class UploadImageFragment : Fragment() {
//
//    private var _binding: FragmentUploadImageBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentUploadImageBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.btnChooseFile.setOnClickListener {
//            // Call your image picker logic here (or delegate to activity)
//        }
//
//        binding.btnSubmitImage.setOnClickListener {
//            // Submit/upload logic here
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
