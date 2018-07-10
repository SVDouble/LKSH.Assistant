package com.lksh.dev.lkshassistant.timetable

import android.content.Context
import android.util.Log
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.SingletonHolder
import com.lksh.dev.lkshassistant.activities.TAG
import org.jsoup.Jsoup

class JsoupHtml(val ctx: Context) {

    fun shouldParseHtml() {
        Prefs.getInstance(ctx).timetable = ""
        Jsoup.connect("http://ejudge.lksh.ru").timeout(5000).get().run {
            select("div.schedule__item").forEachIndexed { index, element ->
                Prefs.getInstance(ctx).timetable += element.text()
                Prefs.getInstance(ctx).timetable += "\n"
                //Log.d(TAG, element.text())
            }
            Log.d(TAG, Prefs.getInstance(ctx).timetable)
            (ctx as JsoupInteraction).timetableLoaded()
        }
    }


    interface JsoupInteraction {
        fun timetableLoaded()
    }

    companion object : SingletonHolder<JsoupHtml, Context>(::JsoupHtml)
}