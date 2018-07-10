package com.lksh.dev.lkshassistant.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.activities.AddUser
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import kotlinx.android.synthetic.main.activity_user_list.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onResume() {
        super.onResume()

        var usrDataList = DBWrapper.getInstance(context!!).listUsers("%")
        if (usrDataList.size > 0) {
            var lazyData = ArrayList<String>()
            for (temp in usrDataList) {
                lazyData.add(/*temp.ID.toString() + */
                        "Login : " + temp.login + "\n" +
                                "Name : " + temp.name + "\n" +
                                "Surname : " + temp.surname + "\n" +
                                "House : " + temp.house + "\n" +
                                "Parallel : " + temp.parallel + "\n" +
                                "Password : " + temp.password + "\n" +
                                "Admin : " + temp.admin)
            }
            var adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, lazyData)
            userlist.adapter = adapter
        }

        add_new.setOnClickListener {
            val intent = Intent(context!!, AddUser::class.java)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and NAME
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UserListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
