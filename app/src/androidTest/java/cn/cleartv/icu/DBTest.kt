package cn.cleartv.icu

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.cleartv.icu.db.ICUDatabase
import cn.cleartv.icu.db.dao.CallDao
import cn.cleartv.icu.db.dao.DeviceDao
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.Device
import kotlinx.coroutines.delay
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/09/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@RunWith(AndroidJUnit4::class)
class DBTest {

    private lateinit var callDao: CallDao
    private lateinit var deviceDao: DeviceDao
    private lateinit var db: ICUDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ICUDatabase::class.java).build()
        callDao = db.callDao()
        deviceDao = db.deviceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun test() {

    }

}