package com.lksh.dev.lkshassistant.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.utils.ContactsData
import com.lksh.dev.lkshassistant.utils.RulesLkshData
import kotlinx.android.synthetic.main.fragment_booklet_inforamtion.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BookletInformation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BookletInformation.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookletInformation : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booklet_inforamtion, container, false)
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
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

        for (rule in RulesLkshData.rules) {
            var emojiPositiveRate = "\uD83D\uDC4E"
            if (rule.isPositive) {
                emojiPositiveRate = "\uD83D\uDC4D"
            }
            goodRules.findViewById<LinearLayout>(rulesCards[curIndex]).findViewById<TextView>(R.id.mark).text = emojiPositiveRate
            goodRules.findViewById<LinearLayout>(rulesCards[curIndex]).findViewById<TextView>(R.id.text).text = rule.text
            curIndex++
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookletInformation.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String) =
                BookletInformation().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
