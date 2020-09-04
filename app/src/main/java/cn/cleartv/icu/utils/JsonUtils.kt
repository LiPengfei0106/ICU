package cn.cleartv.icu.utils

import cn.cleartv.voip.entity.CallRecord
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*


/**
 * <pre>
 *     author : Lee
 *     e-mail : lipengfei@cleartv.cn
 *     time   : 2020/08/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object JsonUtils {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun <T> fromJson(json: String, cls: Class<T>): T? {
        try {
            return moshi.adapter(cls).fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T : Any> toJson(obj: T): String {
        return moshi.adapter<T>(obj::class.java).toJson(obj)
    }

}