package cn.cleartv.icu.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.provider.Settings
import android.view.MenuItem
import android.webkit.URLUtil
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import cn.cleartv.icu.*
import cn.cleartv.icu.db.entity.Device
import cn.cleartv.icu.repository.CallRepository
import cn.cleartv.icu.repository.DeviceRepository
import cn.cleartv.icu.utils.Utils
import cn.cleartv.voip.VoIPClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


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

    class SettingsFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {

        override fun onDestroy() {
            super.onDestroy()
            cancel()
        }

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
                App.hostDevice = Device(newValue.toString(), "护士站主机")
                return@setOnPreferenceChangeListener true
            }

            findPreference<EditTextPreference>("adapter_width")?.setOnPreferenceChangeListener { preference, newValue ->
                return@setOnPreferenceChangeListener try {
                    App.adapterWidth = newValue.toString().toInt()
                    true
                } catch (e: Exception) {
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
                Utils.showToast(if (App.isRecord) "开启录制" else "关闭录制")
                return@setOnPreferenceChangeListener true
            }


            findPreference<Preference>("clean_call_record")?.setOnPreferenceClickListener {
                Snackbar.make(requireView(),"确认删除通话记录？",Snackbar.LENGTH_LONG).setAction("删除"){
                    launch(Dispatchers.IO) {
                        CallRepository.deleteCallRecord()
                    }
                    Utils.showToast("删除成功！")
                }.show()
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("clean_device_data")?.setOnPreferenceClickListener {
                Snackbar.make(requireView(),"确认删除设备列表？",Snackbar.LENGTH_LONG).setAction("删除"){
                    launch(Dispatchers.IO) {
                        DeviceRepository.deleteAllDevice()
                    }
                    Utils.showToast("删除成功！")
                }.show()
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("call_record")?.setOnPreferenceClickListener {
                startActivity(Intent(requireActivity(), CallRecordActivity::class.java))
                return@setOnPreferenceClickListener true
            }

            findPreference<Preference>("version")?.apply {
                summary = BuildConfig.VERSION_NAME
            }?.setOnPreferenceClickListener {
                checkUpdate()
                return@setOnPreferenceClickListener true
            }
        }

        private fun checkUpdate() {
            launch {
                val url = when(App.deviceType){
                    DeviceType.HOST -> App.updateUrl + "/upgrade_host.json"
                    DeviceType.BED -> App.updateUrl + "/upgrade_bed.json"
                    DeviceType.GUEST -> App.updateUrl + "/upgrade_guest.json"
                    else -> App.updateUrl + "/upgrade.json"
                }
                val connection = withContext(Dispatchers.IO) {
                    (URL(url).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 5000
                        requestMethod = "GET"
                        connect()
                    }
                }
                val responseCode = withContext(Dispatchers.IO) {connection.responseCode}
                if (responseCode == 200) {
                    val data = withContext(Dispatchers.IO) {
                        val sb = StringBuilder()
                        try {
                            val reader = BufferedReader(InputStreamReader(connection.inputStream))
                            var line: String? = reader.readLine()
                            while (line != null) {
                                sb.append(line)
                                sb.append(System.lineSeparator())
                                line = reader.readLine()
                            }
//                            reader.lines()
//                                .collect(Collectors.joining(System.lineSeparator()))
                        } catch (e: Exception) {
                        } finally {
                            try {
                                connection.inputStream.close()
                            } catch (e: IOException) {
                            }
                        }
                        sb.toString()
                    }
                    try {
                        JSONObject(data).apply {
                            val appPackageName = optString("appPackageName")
                            val appVersionCode = optInt("appVersionCode")
                            val appVersionName = optString("appVersionName")
                            val updateInfo = optString("updateInfo")
                            val uploadTime = optString("uploadTime")
                            val downloadUrl = optString("downloadUrl")
                            val apkSize = optInt("apkSize")
                            if (appPackageName == requireActivity().packageName && appVersionCode > BuildConfig.VERSION_CODE) {
                                Snackbar.make(requireView(),"发现新版本：$appVersionName",Snackbar.LENGTH_LONG).apply {
                                    setAction("更新"){
                                        this.dismiss()
                                        val downloadDir =
                                            requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                        if (downloadDir == null) {
                                            Utils.showToast("无法获取存储路径")
                                            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
                                        } else {
                                            downloadFile(
                                                downloadUrl,
                                                downloadDir.absolutePath,
                                                URLUtil.guessFileName(downloadUrl, null, null)
                                            )
                                        }
                                    }
                                    show()
                                }
                            } else {
                                Utils.showToast("已是最新版本")
                            }
                        }
                    } catch (e: Exception) {
                        Utils.showToast("检查更新失败！")
                    }

                } else {
                    Utils.showToast("检查更新失败！")
                }
            }

        }

        private fun downloadFile(url: String, path: String, fileName: String) {
            launch {
                var downloadFlag = true
                val downloadSnackbar = Snackbar.make(requireView(),"正在下载...",Snackbar.LENGTH_INDEFINITE)
                downloadSnackbar.setAction("取消"){
                        downloadFlag = false
                        downloadSnackbar.dismiss()
                    }
                downloadSnackbar.show()
                val apkFile = File(path, fileName)
                if (!apkFile.exists()) {
                    apkFile.createNewFile()
                }
                val connection = withContext(Dispatchers.IO) {
                    (URL(url).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15000
                        requestMethod = "GET"
                        connect()
                    }
                }
                val responseCode = withContext(Dispatchers.IO) {connection.responseCode}
                if (responseCode == 200) {
                    withContext(Dispatchers.IO) {
                        try {
                            connection.inputStream?.let {
                                val fileSize = connection.contentLength
                                val fos = FileOutputStream(apkFile)
                                val buf = ByteArray(1024)
                                var downLoadFileSize = 0
                                do {
                                    //循环读取
                                    val numread: Int = it.read(buf)
                                    if (numread == -1) {
                                        break
                                    }
                                    fos.write(buf, 0, numread)
                                    downLoadFileSize += numread
                                    withContext(Dispatchers.Main){
                                        //更新进度条
                                        downloadSnackbar.setText("正在下载...(${(downLoadFileSize * 100 / fileSize)}%)")
                                    }
                                } while (downloadFlag)
                                it.close()
                                if(downLoadFileSize == fileSize){
                                    withContext(Dispatchers.Main){
                                        downloadSnackbar.setText("下载成功！ 正在安装...")
                                        startActivity(getInstallAppIntent(apkFile,true))
                                        delay(3000)
                                        downloadSnackbar.dismiss()
                                    }
                                }else{
                                    apkFile.delete()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main){
                                Utils.showToast("下载新版本失败！")
                            }
                        }
                    }
                } else {
                    Utils.showToast("下载新版本失败！")
                }
            }

        }

        private fun getInstallAppIntent(file: File, isNewTask: Boolean): Intent? {
            val intent = Intent("android.intent.action.VIEW")
            val type = "application/vnd.android.package-archive"
            val data: Uri
            if (Build.VERSION.SDK_INT < 24) {
                data = Uri.fromFile(file)
            } else {
                val authority: String = "cn.cleartv.icu.provider"
                data = FileProvider.getUriForFile(requireActivity(), authority, file)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            requireActivity().grantUriPermission(
                requireActivity().packageName,
                data,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            intent.setDataAndType(data, type)
            return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
        }
    }
}