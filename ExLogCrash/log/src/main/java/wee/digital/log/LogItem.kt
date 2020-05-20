package wee.digital.log

import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class LogItem(val request: Request) {

    var response: Response? = null

    var responseBody: String? = null

    var throwable: Throwable? = null

    private val sentRequestAtMillis = System.currentTimeMillis()

    var requestBody: String? = null

    val ago: String
        get() {
            val ago = System.currentTimeMillis() - sentRequestAtMillis
            if (ago < 60000) {
                return "${(ago / 1000)} seconds ago"
            }
            return SimpleDateFormat("HH:mm:ss").format(Date(sentRequestAtMillis))
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

    init {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()?.writeTo(buffer)
            val s = buffer.readUtf8()
            requestBody = JSONObject(s).toString(2)
        } catch (ignore: Exception) {
        }
    }

}