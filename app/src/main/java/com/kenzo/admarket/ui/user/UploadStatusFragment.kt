//package com.kenzo.admarket.ui.user
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.*
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import com.google.firebase.Firebase
//import com.google.firebase.firestore.Blob
//import com.google.firebase.firestore.firestore
//import com.kenzo.admarket.databinding.FragmentUploadScreenshotBinding
//import com.kenzo.admarket.utils.WalletManager
//
//class UploadScreenshotFragment : Fragment() {
//
//    private var _binding: FragmentUploadScreenshotBinding? = null
//    private val binding get() = _binding!!
//
//    private var selectedImageUri: Uri? = null
//
//    // ActivityResultLauncher to pick image
//    private val pickImageLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                selectedImageUri = it
//                binding.screenshotPreview.setImageURI(it)
//            }
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentUploadScreenshotBinding.inflate(inflater, container, false)
//
//        // On click of the CardView or ImageView, trigger image selection
//        binding.screenshotCard.setOnClickListener {
//            pickImageLauncher.launch("image/*")
//        }
//        binding.sendButton.setOnClickListener {
//            uploadStatusScreenshot()
//        }
//
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//    private fun uploadStatusScreenshot() {
//        val userId = requireContext()
//            .getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
//            .getString("user_id", null)
//
//        if (userId.isNullOrEmpty()) {
//            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (selectedImageUri == null) {
//            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val imageBytes = uriToByteArray(selectedImageUri!!) ?: return
//
//        val statusData = hashMapOf(
//            "userId" to userId,
//            "timestamp" to System.currentTimeMillis(),
//            "image" to Blob.fromBytes(imageBytes) // ← Fix here
//        )
//
//
//        Firebase.firestore.collection("status_uploads")
//            .add(statusData)
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Status uploaded", Toast.LENGTH_SHORT).show()
//                selectedImageUri = null
//                binding.screenshotPreview.setImageDrawable(null) // or set a placeholder image
//                // Add ₹6 to wallet
//                WalletManager(requireContext()).updateWalletAmount(6L)
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun uriToByteArray(uri: Uri): ByteArray? {
//        return requireContext().contentResolver.openInputStream(uri)?.use {
//            it.readBytes()
//        }
//    }
//}
