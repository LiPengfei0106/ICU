package cn.cleartv.icu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.cleartv.icu.BaseActivity
import cn.cleartv.icu.R
import cn.cleartv.icu.db.entity.Device
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun contentLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun afterSetContentView() {
        viewModel.dateTime.observe(this, Observer {
            tv_date.text = it
        })
//        supportFragmentManager.beginTransaction().add(R.id.fl_content,HostMainFragment()).commit()
//        supportFragmentManager.beginTransaction().add(R.id.fl_content,GuestMainFragment()).commit()
        supportFragmentManager.beginTransaction().add(R.id.fl_content,BedMainFragment()).commit()
    }

}
