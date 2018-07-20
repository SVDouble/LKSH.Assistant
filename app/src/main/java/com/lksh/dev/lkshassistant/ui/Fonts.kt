package com.lksh.dev.lkshassistant.ui

import android.content.Context
import android.graphics.Typeface
import com.lksh.dev.lkshassistant.data.SingletonHolder


class Fonts private constructor(context: Context) {
    val montserrat = Typeface.createFromAsset(context.assets, "fonts/montserrat-med.ttf")

    companion object : SingletonHolder<Fonts, Context>(::Fonts)
}