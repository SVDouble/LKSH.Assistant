package com.lksh.dev.lkshassistant.fragments

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.R.id.*
import com.lksh.dev.lkshassistant.R.raw.test
import com.lksh.dev.lkshassistant.activities.MainActivity
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import com.lksh.dev.lkshassistant.utils.ContactsData
import com.lksh.dev.lkshassistant.utils.RulesLkshData
import kotlinx.android.synthetic.main.fragment_building_info.*
import kotlinx.android.synthetic.main.part_rv_building.view.*
import org.w3c.dom.Text

private const val ARG_HOUSE_ID = "house_id"

class BuildingInfoFragment : Fragment() {
    private var houseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            houseId = it.getString(ARG_HOUSE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_building_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        contacts.findViewById<TextView>(R.id.header).text = "Контакты"
        for (item in ContactsData.contacts) {
            contacts.findViewById<LinearLayout>(item.handlerView).findViewById<TextView>(R.id.name).text = item.name
            contacts.findViewById<LinearLayout>(item.handlerView).findViewById<TextView>(R.id.role).text = item.role
            contacts.findViewById<LinearLayout>(item.handlerView).findViewById<TextView>(R.id.phone).text = item.phone
            contacts.findViewById<LinearLayout>(item.handlerView).findViewById<ImageView>(R.id.userPhoto).setImageDrawable(resources.getDrawable(item.imageSrc, resources.newTheme()))
        }

        goodRules.findViewById<TextView>(R.id.goodRulesHeader).text = "Правила"

        var rulesCards = listOf(R.id.goodRule1, R.id.goodRule2, R.id.goodRule3, R.id.goodRule4, R.id.goodRule5, R.id.goodRule6, R.id.goodRule7, R.id.goodRule8, R.id.goodRule9, R.id.goodRule10, R.id.goodRule11, R.id.goodRule12, R.id.goodRule13, R.id.goodRule14, R.id.goodRule15)
        var curIndex = 0

        for (rule in RulesLkshData.rules){
            var emojiPositiveRate = "\uD83D\uDC4E"
            if (rule.isPositive){
                emojiPositiveRate = "\uD83D\uDC4D"
            }
            goodRules.findViewById<LinearLayout>(rulesCards[curIndex]).findViewById<TextView>(R.id.mark).text = emojiPositiveRate
            goodRules.findViewById<LinearLayout>(rulesCards[curIndex]).findViewById<TextView>(R.id.text).text = rule.text
            curIndex++
        }

    }

    override fun onStart() {
        super.onStart()

        view?.background = ColorDrawable(Color.argb(0, 0, 0, 0))
    }

    companion object {
        @JvmStatic
        fun newInstance(houseId: String) =
                BuildingInfoFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOUSE_ID, houseId)
                    }
                }
    }
}
