package com.lksh.dev.lkshassistant.timetable

import android.content.Context
import android.util.Log
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.SingletonHolder
import org.jsoup.Jsoup
import java.net.SocketTimeoutException

class JsoupHtml(val ctx: Context) {

    fun shouldParseHtml() {
        //1. Fetching the HTML from a given URL
        Log.d("_LKSH", "fetch data")
        try {
            Jsoup.connect("https://ejudge.lksh.ru").timeout(5000).get().run {
                //2. Parses and scrapes the HTML response
                Log.d("_LKSH", "Connected!")
                select("div.schedule__item").forEachIndexed { index, element ->
                    val event = element.text()
                    Prefs.getInstance(ctx).timetable = event + "\n"
                    Log.d("_LKSH", event)
                }
            }
        } catch (e: SocketTimeoutException) {
            Log.d("_LKSH", "Can't connect to ejudge")
        }
    }

    companion object : SingletonHolder<JsoupHtml, Context>(::JsoupHtml)
}