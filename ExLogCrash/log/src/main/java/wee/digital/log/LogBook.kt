package wee.digital.log

import android.app.Application
import androidx.lifecycle.MutableLiveData
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import wee.digital.log.data.CrashItem
import wee.digital.log.data.EnrollItem
import wee.digital.log.data.LogDb
import wee.digital.log.data.LogItem

object LogBook {

    private var mApp: Application? = null

    var app: Application
        set(value) {
            mApp = value
            crashLog = true
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
                saveRequest(request)
                try {
                    val response = chain.proceed(request)
                    saveResponse(response)
                    return response
                } catch (e: Throwable) {
                    saveResponse(request.url().toString(), e)
                    throw e
                } finally {
                    notifyLogChanged()
                }
            }

        }

    var crashLog: Boolean = false
        set(value) {
            if (value) Thread.setDefaultUncaughtExceptionHandler { _, e ->
                Thread {
                    var sTrace = ""
                    var traceArr: Array<StackTraceElement> = e.stackTrace
                    for (i in traceArr.indices) {
                        sTrace += "${traceArr[i]}\n"
                    }
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause
                    var sCause = ""
                    e.cause?.also {
                        sCause += it.toString()
                        val causeArr = it.stackTrace
                        for (i in causeArr.indices) {
                            sCause += "${causeArr[i]}\n"
                        }
                    }
                    LogDb.instance.crashDao.insert(
                            CrashItem(
                                    title = e.toString(),
                                    trace = sTrace,
                                    cause = sCause
                            )
                    )

                }.start()
            }
        }


    /**
     * Save request
     */
    fun saveRequest(url: String, method: String, requestBody: String) {
        if (instance.size > 20) instance.removeAt(0)
        instance.add(LogItem().also {
            it.url = url
            it.method = method
            it.requestBody = JSONObject(requestBody).toString(2)
            it.sentRequestTime = System.currentTimeMillis()
        })
        notifyLogChanged()
    }

    fun saveRequest(request: Request) {
        request.newBuilder().build().apply {
            val buffer = Buffer()
            body()?.writeTo(buffer)
            val requestBody = buffer.readUtf8()
            saveRequest(url().toString(), method(), requestBody)
        }
    }

    /**
     * Save response
     */
    fun saveResponse(url: String, block: (LogItem) -> Unit) {
        for (i in instance.lastIndex downTo 0) {
            if (url == instance[i].url) {
                block(instance[i])
                notifyLogChanged()
                break
            }
        }
    }

    fun saveResponse(url: String, throwable: Throwable?) {
        saveResponse(url) {
            it.throwable = throwable
            it.receivedResponseTime = System.currentTimeMillis()
        }
    }

    fun saveResponse(url: String, code: Int, message: String, responseBody: String?) {
        saveResponse(url) {
            it.code = code
            it.message = message
            it.responseBody = JSONObject(responseBody).toString(2)
            it.receivedResponseTime = System.currentTimeMillis()
        }
    }

    fun saveResponse(response: Response) {
        response.newBuilder().build().apply {
            val source = body()?.source()
            source?.request(Long.MAX_VALUE)
            val responseBody = source?.buffer()?.clone()?.readUtf8()
            val url = request().url().toString()
            saveResponse(url, code(), message(), responseBody)
        }
    }

    /**
     * Utils
     */
    fun notifyLogChanged() {
        updateLiveData.postValue(System.currentTimeMillis())
    }

    fun saveEnrollItem(item: EnrollItem) {
        Thread{
            LogDb.instance.enrollDao.insert(item)
        }.start()
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

}