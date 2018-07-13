package com.lksh.dev.lkshassistant

import android.content.Context
import org.jetbrains.anko.runOnUiThread
import org.jsoup.Jsoup

class JsoupHtml(val ctx: Context) {

    fun shouldParseHtml() {
        var timetable = ""
        Jsoup.connect("http://ejudge.lksh.ru").timeout(5000).get().run {
            select("div.schedule__item").forEachIndexed { index, element ->
                timetable += element.text() + "\n"
                //Log.d(TAG, element.text())
            }
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