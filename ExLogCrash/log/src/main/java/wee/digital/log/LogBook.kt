package wee.digital.log

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

object LogBook {

    private var mApp: Application? = null

    var app: Application
        set(value) {
            mApp = value
        }
        get() {
            if (null == mApp) throw NullPointerException("module not be set")
            return mApp!!
        }

    val updateLiveData = MutableLiveData<Long>()

    val instance: MutableList<LogItem> = mutableListOf()

    val loggingInterceptor: Interceptor
        get() = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request: Request = chain.request()
                val logItem = LogItem(request)
                if (instance.size > 20) instance.removeAt(0)
                instance.add(logItem)
                try {
                    val response = chain.proceed(request)
                    logItem.response = response
                    return response
                } catch (e: Throwable) {
                    logItem.throwable = e
                    throw e
                } finally {
                    notifyLogChanged()
                }
            }

        }

    fun save(url: String, responseBody: JsonObject?) {
        for (i in instance.lastIndex downTo 0) {
            if (url == instance[i].request.url().toString()) {
                instance[i].responseBody = responseBody?.toString().jsonFormat()
                notifyLogChanged()
                break
            }
        }
    }

    fun notifyLogChanged() {
        updateLiveData.postValue(System.currentTimeMillis())
    }

    private fun String?.jsonFormat(): String? {
        if (this.isNullOrEmpty()) return null

        return try {
            val obj = JSONObject(this)
            /*  .replace("{","<b>{</b>")
              .replace("}","<b>}</b>")
              .replace("[","<b>[</b>")
              .replace("]","<b>]</b>")
              .replace("\"","<font color=$#C8C8C8>\"</font>")*/
            obj.toString(2)
        } catch (e: JSONException) {
            null
        }
    }

    var crashLog: Boolean = false
        set(value) {
            if (value) Thread.setDefaultUncaughtExceptionHandler { _, e ->
                Thread {
                    var sTrace = ""
                    var arr: Array<StackTraceElement> = e.stackTrace
                    for (i in arr.indices) {
                        sTrace += "${arr[i]}\n"
                    }
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause

                    var sCause = ""
                    e.cause?.also {
                        sCause += it.toString()
                        val arr = it.stackTrace
                        for (i in arr.indices) {
                            sCause += "${arr[i]}\n"
                        }
                    }
                    CrashDb.data.insert(
                            CrashItem(
                                    title = e.toString(),
                                    trace = sTrace,
                                    cause = sCause
                            )
                    )

                }.start()
            }
        }

}