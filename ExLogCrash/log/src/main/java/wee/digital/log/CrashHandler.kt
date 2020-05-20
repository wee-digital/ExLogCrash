package wee.digital.log

object CrashHandler {

    fun enable() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
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
