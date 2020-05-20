package wee.digital.log

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_log.view.*

class LogDialog {

    private var self: AlertDialog? = null

    private lateinit var view: View

    private var adapter = LogAdapter()

    private var dataUpdateObserver = Observer<Long> {
        adapter.notifyDataSetChanged()
    }

    constructor(activity: FragmentActivity?) {

        activity ?: return

        onViewConfig(activity)

        onAdapterConfig(view)
    }

    private fun onViewConfig(activity: FragmentActivity) {
        view = LayoutInflater.from(activity).inflate(R.layout.dialog_log, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.LogDialog)
        builder.setView(view)
        self = builder.create()
        self?.window?.apply {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        view.isFocusable = false
        view.isFocusableInTouchMode = true
    }

    private fun onAdapterConfig(v: View) {

        v.dialogRecyclerView.layoutManager = LinearLayoutManager(view.context).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        v.dialogRecyclerView.adapter = adapter

        v.viewClose.setOnClickListener {
            dismiss()
        }

        v.viewClear.setOnClickListener {
            LogBook.instance.clear()
            adapter.notifyDataSetChanged()
            dismiss()
        }

        LogBook.updateLiveData.observeForever(dataUpdateObserver)
    }

    fun show() {
        self?.show()
    }

    fun dismiss() {
        LogBook.updateLiveData.removeObserver(dataUpdateObserver)
        self?.dismiss()
    }


}