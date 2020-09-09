package cn.cleartv.icu.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.cleartv.icu.App
import cn.cleartv.icu.BuildConfig
import cn.cleartv.icu.DeviceStatus
import cn.cleartv.icu.DeviceType
import cn.cleartv.icu.db.dao.CallDao
import cn.cleartv.icu.db.dao.DeviceDao
import cn.cleartv.icu.db.entity.Call
import cn.cleartv.icu.db.entity.Device
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Database(entities = [Device::class,Call::class],version = 4, exportSchema = false)
abstract class ICUDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    abstract fun callDao(): CallDao

    companion object {
        private const val DB_NAME = "ICU.DB"
        internal const val TABLE_DEVICE = "devices_table"
        internal const val TABLE_CALL = "call_table"
        val instance: ICUDatabase by lazy {
            Room.databaseBuilder(App.instance,ICUDatabase::class.java,DB_NAME)
                .addCallback(object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        // init
                        CoroutineScope(Dispatchers.IO).launch{
                            instance.deviceDao().apply {
                                insert(Device("10001000",name = "护士站",type = DeviceType.HOST))
                                insert(Device("10001001",name = "01床",type = DeviceType.BED))
                                insert(Device("10001002",name = "02床",type = DeviceType.BED))
                                insert(Device("10001003",name = "03床",type = DeviceType.BED))
                                insert(Device("10001004",name = "04床",type = DeviceType.BED))
                                insert(Device("10001005",name = "05床",type = DeviceType.BED))
                                insert(Device("10001006",name = "06床",type = DeviceType.BED))
                                insert(Device("10001007",name = "07床",type = DeviceType.BED))
                                insert(Device("10001008",name = "08床",type = DeviceType.BED))
                                insert(Device("10001009",name = "09床",type = DeviceType.BED))
                                insert(Device("10001010",name = "10床",type = DeviceType.BED))
                                insert(Device("10002001",name = "01探视机",type = DeviceType.GUEST))
                                insert(Device("10002002",name = "02探视机",type = DeviceType.GUEST))
                                insert(Device("10003001",name = "门禁机",type = DeviceType.DOOR))
                            }
                        }
                    }
                })
                // 清理以前的数据库
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()

        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE $TABLE_DEVICE "
                            + " ADD COLUMN `type` TEXT NOT NULL DEFAULT 'unknown'"
                )
                database.execSQL(
                        "ALTER TABLE $TABLE_DEVICE "
                                + " ADD COLUMN `status` TEXT NOT NULL DEFAULT 'DISCONNECT'"
                        )

            }
        }


    }

}