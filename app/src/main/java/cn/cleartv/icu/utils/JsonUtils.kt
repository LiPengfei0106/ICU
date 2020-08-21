package cn.cleartv.icu.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.jetbrains.annotations.NotNull

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

    fun <T> fromJson(json: String, cls: Class<T>): T?{
        try {
            return moshi.adapter(cls).fromJson(json)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    fun <T : Any> toJson(obj: T): String{
        return moshi.adapter<T>(obj::class.java).toJson(obj)
    }

}