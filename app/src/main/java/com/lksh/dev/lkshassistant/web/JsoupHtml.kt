package com.lksh.dev.lkshassistant.web

import android.content.Context
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.data.SingletonHolder
import org.jetbrains.anko.runOnUiThread
import org.jsoup.Jsoup

class JsoupHtml(val ctx: Context) {

    private val TIMEOUT = 5000
    private val URL_REQUEST = "http://ejudge.lksh.ru"
    private val CSS_QUERY = "div.schedule__item"

    fun parseHtml() {
        var timetable = ""
        Jsoup.connect(URL_REQUEST).timeout(TIMEOUT).get().run {
            select(CSS_QUERY).forEach { timetable += "${it.text()}\n" }
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