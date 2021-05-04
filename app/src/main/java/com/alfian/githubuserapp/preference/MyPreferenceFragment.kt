package com.alfian.githubuserapp.preference

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.alfian.githubuserapp.R

class MyPreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var REMINDER: String
    private lateinit var isReminderOn: SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        REMINDER = resources.getString(R.string.key_reminder)
        isReminderOn = findPreference<SwitchPreference>(REMINDER) as SwitchPreference

        val sh = preferenceManager.sharedPreferences
        isReminderOn.isChecked = sh.getBoolean(REMINDER, false)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val reminderAlarm = AlarmReceiver()
        if (key == REMINDER) {
            val getState = sharedPreferences!!.getBoolean(REMINDER, false)
            isReminderOn.isChecked = getState
            Toast.makeText(activity, "status reminder $getState", Toast.LENGTH_SHORT).show()

            if (getState) {
                reminderAlarm.setRepeatingAlarm(
                    requireActivity(),
                    "09:00",
                    "click to open github app today"
                )
            } else {
                reminderAlarm.cancelAlarm(requireActivity())
            }
        }
    }

}