package wee.digital.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import wee.digital.log.dialog.CrashDialog


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewCrash.setOnClickListener {
            crash()
        }
        viewCrashLog.setOnClickListener {
            CrashDialog(this).show()
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun crash(): Int {
        return listOf<Int>()[0]
    }
}
