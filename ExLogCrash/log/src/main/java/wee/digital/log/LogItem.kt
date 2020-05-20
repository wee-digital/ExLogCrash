package wee.digital.log

import android.graphics.Color
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LogItem(val request: Request) {

    var response: Response? = null

    var responseBody: String? = null

    var throwable: Throwable? = null

    var color: Int = Color.parseColor("#2196F3")

    val stringRequest: String?
        get() = try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            null
        }

    val jsonRequest: String?
        get() = try {
            JSONObject(stringRequest).toString(2)
        } catch (e: JSONException) {
            null
        }

    val interval: String
        get() = if (response == null && throwable == null) {
            "~ms"
        } else if (response != null) {
            val inv = response!!.receivedResponseAtMillis() - response!!.sentRequestAtMillis()
            "${inv}ms"
        } else {
            ""
        }
}