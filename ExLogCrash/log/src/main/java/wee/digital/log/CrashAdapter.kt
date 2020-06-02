package wee.digital.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_crash_item.view.*

class CrashAdapter : RecyclerView.Adapter<CrashAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v)

    var list: List<CrashItem> = listOf()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dialog_crash_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val model: CrashItem = list[position]
        holder.itemView.apply {
            textViewTitle.text = model.title
            textViewTrace.text = model.trace
            textViewCause.text = model.cause
        }
    }

}