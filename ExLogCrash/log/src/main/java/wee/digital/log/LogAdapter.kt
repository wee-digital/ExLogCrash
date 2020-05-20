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
        textViewTitle.text = "${request.method()} - ${request.url()}"

        onBindRequest(model)

        model.throwable?.also {
            textViewResponseTitle.setTextColor(Color.parseColor("#D81B60"))
            textViewResponseTitle.setHyperText("<font color=#FF5252>${(it.message ?: "unknown error")}</font>")
            return
        }

        onBindResponse(model)
    }

    private fun View.onBindRequest(model: LogItem) {
        textViewRequestTitle.text = "Request - ${model.ago}"
        textViewRequest.text = "..."
        val onClick = View.OnClickListener {
            textViewRequest.text = if (textViewRequest.text.toString() == "...") model.requestBody else "..."
        }
        textViewRequestTitle.setOnClickListener(onClick)
        textViewRequest.setOnClickListener(onClick)
    }

    private fun View.onBindResponse(model: LogItem) {
        textViewResponseTitle.setTextColor(Color.parseColor("#00897B"))
        textViewResponseTitle.text = when (model.response) {
            null -> "Response"
            else -> "Response - ${model.response!!.code()}  - ${model.interval}"
        }
        val onClick = View.OnClickListener {
            textViewResponse.text = if (textViewResponse.text.toString() == "...") model.responseBody else "..."
        }
        textViewResponse.text = "..."
        textViewResponseTitle.setOnClickListener(onClick)
        textViewResponse.setOnClickListener(onClick)
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