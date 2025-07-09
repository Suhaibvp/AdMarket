package com.kenzo.admarket.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.adapters.ScreenshotAdapter
import com.kenzo.admarket.databinding.FragmentUserScreenshotsGalleryBinding

class UserScreenshotsGalleryFragment : Fragment() {

    private var _binding: FragmentUserScreenshotsGalleryBinding? = null
    private val binding get() = _binding!!

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("user_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserScreenshotsGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.firestore.collection("status_uploads")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val screenshots = documents.mapNotNull { doc ->
                    val blob = doc.getBlob("image")
                    blob?.toBytes()?.let { ScreenshotItem(it) }
                }

                binding.recyclerScreenshots.layoutManager = GridLayoutManager(requireContext(),2)
                binding.recyclerScreenshots.adapter = ScreenshotAdapter(screenshots)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load screenshots", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class ScreenshotItem(val imageBytes: ByteArray)
