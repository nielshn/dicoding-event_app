package com.dicoding.dicodingevent.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.dicodingevent.SettingPreferences
import com.dicoding.dicodingevent.dataStore
import com.dicoding.dicodingevent.databinding.FragmentSettingsBinding
import com.dicoding.dicodingevent.utils.Event
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel

    private val _toastMessage = MutableLiveData<Event<String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModel with Factory using DataStore
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        settingsViewModel =
            ViewModelProvider(this, SettingsViewModelFactory(pref))[SettingsViewModel::class.java]


        // Observe toast message to show toast
        settingsViewModel.toastMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // Switch Theme
        val switchTheme: SwitchMaterial = binding.switchTheme
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        // Save theme setting when switch is changed
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsViewModel.savThemeSetting(isChecked)
                val mode = if (isChecked) "Dark Mode" else "Light Mode"
                _toastMessage.value = Event("Theme changed to $mode")
            }
        }

        // Switch Reminder Notification
        val switchReminder: SwitchMaterial = binding.switchReminder
        settingsViewModel.getReminderSetting().observe(viewLifecycleOwner) { isReminderActive ->
            switchReminder.isChecked = isReminderActive
            if (isReminderActive) {
                DailyReminderManager.startReminder(requireContext())
            } else {
                DailyReminderManager.cancelReminder(requireContext())
            }
        }
        // Save reminder setting when switch is changed
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsViewModel.saveReminderSetting(isChecked)
                val status = if (isChecked) "enabled" else "disabled"
                _toastMessage.value = Event("Reminder notification $status")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
