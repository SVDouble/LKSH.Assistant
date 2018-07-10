package com.lksh.dev.lkshassistant.timetable

import android.content.Context
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.SingletonHolder
import org.jsoup.Jsoup

class JsoupHtml(val ctx: Context) {

    fun shouldParseHtml() {
        //1. Fetching the HTML from a given URL
        Jsoup.connect("http://ejudge.lksh.ru").timeout(5000).get().run {
            //2. Parses and scrapes the HTML response
            select("div.schedule__item").forEachIndexed { index, element ->
                val event = element.text()
                Prefs.getInstance(ctx).timetable = event + "\n"
                //val title = item.text()
                //val txt = time.text()
                //3. Dumping Search Index, Title and URL on the stdout.
                println(event)
                println("----------------------------------------")
                //print(txt)
            }
        }
    }

    companion object : SingletonHolder<JsoupHtml, Context>(::JsoupHtml)
}