package wee.digital.log

import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_log_item.view.*
import okhttp3.Request

class LogAdapter : RecyclerView.Adapter<LogAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun getItemCount(): Int {
        return LogBook.instance.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dialog_log_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.onBindItem(LogBook.instance[position])
    }

    private fun View.onBindItem(model: LogItem) {

        val request: Request = model.request

        // Title
        var s = "${request.method()} - ${request.url()} - ${model.interval}"
        textViewTitle.setTextColor(model.color)
        textViewTitle.text = s

        model.color = Color.parseColor("#4B4B4B")

        // RequestBody
        val requestTitle = "RequestBody"
        textViewRequest.text = requestTitle
        textViewRequest.setOnClickListener {
            textViewRequest.text = if (textViewRequest.text.toString() != requestTitle) {
                requestTitle
            } else {
                model.jsonRequest
            }
        }

        model.throwable?.also {
            textViewResponse.setHyperText("<font color=#FF5252>${(it.message ?: "unknown error")}</font>")
            return
        }

        model.response?.also { res ->
            val responseTitle = "Response - ${res.code()}"
            textViewResponse.text = responseTitle
            textViewResponse.setOnClickListener {
                textViewResponse.text = if (textViewResponse.text.toString() != responseTitle) {
                    responseTitle
                } else {
                    model.responseBody
                }
            }
            return
        }
    }

    private fun TextView.setHyperText(s: String?) {
        text = when {
            s.isNullOrEmpty() -> null
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(s, 1)
            else -> @Suppress("DEPRECATION")
            Html.fromHtml(s)
        }
    }

}