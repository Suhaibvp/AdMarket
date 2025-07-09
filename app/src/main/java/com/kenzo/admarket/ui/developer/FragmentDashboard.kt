package com.kenzo.admarket.ui.developer

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kenzo.admarket.databinding.FragmentDeveloperDashboardBinding
import com.kenzo.admarket.databinding.FragmentMyCompanyBinding
import com.kenzo.admarket.utils.CleanupReceiver
import com.kenzo.admarket.utils.WalletManager
import java.util.Calendar
import java.util.Locale

class FragmentDashboard : Fragment() {

    private var _binding: FragmentDeveloperDashboardBinding? = null
    private val binding get() = _binding!!
    private var selectedHour = 0
    private var selectedMinute = 0
    private val prefs by lazy {
        requireContext().getSharedPreferences("cleanup_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeveloperDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load previously saved time (optional)
        selectedHour = prefs.getInt("hour", 0)
        selectedMinute = prefs.getInt("minute", 0)
        updateTimeText()

        // 🕓 Handle time picking
        binding.btnPickTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                prefs.edit().putInt("hour", hourOfDay).putInt("minute", minute).apply()
                updateTimeText()
            }, selectedHour, selectedMinute, true).show()
        }

        // 📅 Handle start date saving
        binding.btnScheduleCleanup.setOnClickListener {
            val day = binding.datePickerStart.dayOfMonth
            val month = binding.datePickerStart.month
            val year = binding.datePickerStart.year

            val calendar = Calendar.getInstance().apply {
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val startDateMillis = calendar.timeInMillis
            prefs.edit().putLong("cleanup_start_date", startDateMillis).apply()
            Toast.makeText(requireContext(), "Start date saved", Toast.LENGTH_SHORT).show()
        }

        // ⏰ Handle alarm scheduling
        binding.btnScheduleAlarm.setOnClickListener {
            scheduleDailyCleanupAlarm(requireContext(), selectedHour, selectedMinute)
            Toast.makeText(requireContext(), "Daily cleanup alarm scheduled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimeText() {
        val time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        binding.tvSelectedTime.text = "Selected Time: $time"
    }

    private fun scheduleDailyCleanupAlarm(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CleanupReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to allow exact alarms
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
