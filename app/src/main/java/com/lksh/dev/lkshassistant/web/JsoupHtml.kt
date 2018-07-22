package com.lksh.dev.lkshassistant.web

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.data.SingletonHolder
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.jsoup.Jsoup

data class ScheduleItem(var time : String = "", var description : String = "")

class JsoupHtml(val ctx: Context) {

    private val TIMEOUT = 5000
    private val URL_REQUEST = "assistant.p2.lksh.ru/get_timetable_events/"/* "http://ejudge.lksh.ru/"*/

    fun parseHtml() {
        var timetable = ""
        Jsoup.connect(URL_REQUEST).timeout(TIMEOUT).get().body().run {
            val items = Klaxon().parseArray<ScheduleItem>(text().replace("'", "\"")) ?: JsonArray()
            for (i in items)
                timetable += i.time + " " + i.description + '\n'
            ctx.runOnUiThread {
                Prefs.getInstance(ctx).timetable = timetable
                (ctx as JsoupInteraction).timetableLoaded()
            }
        }
    }

    interface JsoupInteraction {
        fun timetableLoaded()
    }

    companion object : SingletonHolder<JsoupHtml, Context>(::JsoupHtml)
}