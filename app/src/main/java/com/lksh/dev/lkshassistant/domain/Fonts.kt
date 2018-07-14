package com.lksh.dev.lkshassistant.domain

import android.content.Context
import android.graphics.Typeface


class Fonts private constructor(context: Context) {
    val montserrat = Typeface.createFromAsset(context.assets, "fonts/montserrat-med.ttf")

    companion object : SingletonHolder<Fonts, Context>(::Fonts)
}