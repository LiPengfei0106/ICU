package cn.cleartv.icu.ui

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import cn.cleartv.icu.App
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.BuildConfig
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.utils.Utils
import cn.cleartv.voip.VoIPClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : BaseActivity() {

    override fun contentLayoutRes(): Int {
        return R.layout.settings_activity
    }

    override fun afterSetContentView() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        VoIPClient.statusData.observe(this, Observer {
            when (it) {
                VoIPClient.Status.CONNECTED -> {
                    supportActionBar?.title = "设置(已连接)"
                    Utils.showToast("已连接")
                }
                VoIPClient.Status.DISCONNECT -> {
                    supportActionBar?.title = "设置(连接已断开)"
                    Utils.showToast("连接已断开")
                }
                VoIPClient.Status.CONNECTING -> {
                    supportActionBar?.title = "设置(正在连接中...)"
                    Utils.showToast("正在连接中...")
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return false
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<Preference>("system_setting")?.setOnPreferenceClickListener {
                startActivity(Intent(Settings.ACTION_SETTINGS))
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("reboot")?.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Process.killProcess(Process.myPid())
                return@setOnPreferenceClickListener true
            }

            findPreference<EditTextPreference>("host_number")?.setOnPreferenceChangeListener { preference, newValue ->
                App.hostDevice = Device(newValue.toString(),"护士站主机")
                return@setOnPreferenceChangeListener true
            }

            findPreference<EditTextPreference>("adapter_width")?.setOnPreferenceChangeListener { preference, newValue ->
                return@setOnPreferenceChangeListener try {
                    App.adapterWidth = newValue.toString().toInt()
                    true
                }catch (e: Exception){
                    false
                }
            }
            findPreference<Preference>("reconnect")?.setOnPreferenceClickListener {
                val username = App.settingSP.getString("username", null) ?: ""
                val password = App.settingSP.getString("password", null) ?: ""
                App.deviceInfo = Device(
                    username,
                    username,
                    type = App.deviceType
                )
                VoIPClient.hostUrl =
                    App.settingSP.getString("host_url", null)
                        ?: resources.getString(R.string.host_url)
                VoIPClient.init(App.instance, username, password, BuildConfig.DEBUG)
                return@setOnPreferenceClickListener true
            }

            findPreference<SwitchPreference>("is_record")?.setOnPreferenceChangeListener { preference, newValue ->
                App.isRecord = newValue as Boolean
                Utils.showToast(if(App.isRecord) "开启录制" else "关闭录制")
                return@setOnPreferenceChangeListener true
            }


            findPreference<Preference>("clean_call_record")?.setOnPreferenceClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    CallRepository.deleteCallRecord()
                }
                Utils.showToast("删除成功！")
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("clean_device_data")?.setOnPreferenceClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    DeviceRepository.deleteAllDevice()
                }
                Utils.showToast("删除成功！")
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("call_record")?.setOnPreferenceClickListener {
                startActivity(Intent(requireActivity(),CallRecordActivity::class.java))
                return@setOnPreferenceClickListener true
            }
        }
    }
}