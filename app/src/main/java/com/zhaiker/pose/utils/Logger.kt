package com.zhaiker.pose.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter
import java.time.format.DateTimeFormatter
import java.util.Collections

class Logger {
    companion object{

        private var logNum = 0

        var logString:String = ""

        /**
         * 打印日志
         * @param log
         * @param level日志级别 d,i,w,e
         */
        fun log(log: Any, level : String? = null,) {
            CoroutineScope(Dispatchers.IO).launch {
                val logTemp:String = if(log is java.lang.Exception||log is Exception) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    log.printStackTrace(pw)
                    Log.e("App", "$sw")
                    sw.toString()
                }else {
                    val slog = "$log"
                    Log.e("App", slog)
                    slog
                }
                if(logNum>1000) {
                    logString = ""
                    logNum=0
                }
                logNum++
                logString =("["+TimeUtils.getCurrentDate("HH:mm:ss")+"]  "+logTemp+"\n")+logString
            }

        }
    }
}