package com.lksh.dev.lkshassistant.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.parseCsv
import kotlinx.android.synthetic.main.fragment_booklet.*
import kotlinx.android.synthetic.main.part_booklet_contact.view.*
import kotlinx.android.synthetic.main.part_booklet_rule.view.*

data class RuleItem(val isPositive: Boolean,
                    val text: String)

data class ContactItem(val name: String,
                       val role: String,
                       val phone: String,
                       val imageSrc: Int)

class BookletInformation : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_booklet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val images = mapOf(
                "director" to R.drawable.director,
                "deputy_director" to R.drawable.subdirector,
                "doctor" to R.drawable.doctor
        )

        header.text = "Контакты"
        goodRulesHeader.text = "Правила"

        val contacts = parseCsv(context!!, R.raw.contacts).map { ContactItem(it[0], it[1], it[2], images[it[3]]!!) }
        for (item in contacts) {
            contacts_container.addView(layoutInflater.inflate(R.layout.part_booklet_contact, contacts_container, false).apply {
                findViewById<TextView>(R.id.bk_name).text = item.name
                bk_role.text = item.role
                bk_phone.text = item.phone
                bk_userPhoto.setImageResource(item.imageSrc)
            })
        }

        val rules = parseCsv(context!!, R.raw.lksh_rules).map { RuleItem(it[0] == "+", it[1]) }
        for (rule in rules) {
            rules_container.addView(layoutInflater.inflate(R.layout.part_booklet_rule, rules_container, false).apply {
                bk_mark.text = if (rule.isPositive) "\uD83D\uDC4D" else "\uD83D\uDC4E"
                bk_text.text = rule.text
            })
        }
    }
}
