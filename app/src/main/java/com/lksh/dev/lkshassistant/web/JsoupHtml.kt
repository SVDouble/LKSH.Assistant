package com.lksh.dev.lkshassistant.web

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.data.SingletonHolder
import org.jetbrains.anko.runOnUiThread
import org.jsoup.Jsoup

data class Schedule(var error : String, var result : List<ScheduleItem>)
data class ScheduleItem(var time : String = "", var description : String = "")

class JsoupHtml(val ctx: Context) {
    private val TIMEOUT = 5000
    private val URL_REQUEST = "http://assistant.p2.lksh.ru/get_timetable_events"

    fun parseHtml() {
        if (!isOnline())
            return
        try {

            var timetable = ""
            Jsoup.connect(URL_REQUEST).timeout(TIMEOUT).ignoreContentType(true).get().body().run {
                val it = Klaxon().parse<Schedule>(text())
                for (i in it?.result ?: listOf())
                    timetable += i.time + " " + i.description + '\n'
                ctx.runOnUiThread {
                    Prefs.getInstance(ctx).timetable = timetable
                    (ctx as JsoupInteraction).timetableLoaded()
                }
            }
        } catch (e : Exception){}
    }

    private fun isOnline(): Boolean {
        val connMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    interface JsoupInteraction {
        fun timetableLoaded()
    }

    companion object : SingletonHolder<JsoupHtml, Context>(::JsoupHtml)
}