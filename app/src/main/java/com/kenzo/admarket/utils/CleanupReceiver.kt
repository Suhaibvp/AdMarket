package com.kenzo.admarket.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class CleanupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("cleanup_prefs", Context.MODE_PRIVATE)
        val startDateMillis = prefs.getLong("cleanup_start_date", -1L)

        if (startDateMillis == -1L) {
            Log.d("CleanupReceiver", "No start date found. Cleanup not scheduled.")
            return
        }

        val now = System.currentTimeMillis()
        val daysSince = ((now - startDateMillis) / (1000 * 60 * 60 * 24)).toInt()

        Log.d("CleanupReceiver", "Days since start: $daysSince")

        if (daysSince != 0 && daysSince % 10 == 0) {
            Log.d("CleanupReceiver", "✅ Triggering cleanup on day $daysSince")
            Toast.makeText(context, "Running auto-cleanup (day $daysSince)", Toast.LENGTH_SHORT).show()
            clearStatusUploads()
        } else {
            Log.d("CleanupReceiver", "⏳ Not cleanup day.")
        }
    }
    private fun clearStatusUploads() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("status_uploads")

        collectionRef.get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()

                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d("CleanupReceiver", "✅ status_uploads collection cleared")
                        //Toast.makeText(context, "status_uploads cleared", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.e("CleanupReceiver", "❌ Failed to clear status_uploads: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.e("CleanupReceiver", "❌ Failed to fetch status_uploads: ${it.message}")
            }
    }

}

