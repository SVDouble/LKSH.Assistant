package com.lksh.dev.lkshassistant

import android.content.Context
import org.jetbrains.anko.runOnUiThread
import org.jsoup.Jsoup

class JsoupHtml(val ctx: Context) {

    private val TIMEOUT = 5000
    private val URL_REQUEST = "http://ejudge.lksh.ru"
    private val CSS_QUERY = "div.schedule__item"

    fun shouldParseHtml() {
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