package plugin.gradle.my.interactive_test

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import plugin.gradle.my.R

import kotlinx.android.synthetic.main.fragment_interactive.*
import kotlinx.android.synthetic.main.fragment_interactive.view.*

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * [InteractiveFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [InteractiveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InteractiveFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_interactive, container, false)
        view.showDialog.setOnClickListener {
            context?.let { it1 ->
                val builder = AlertDialog.Builder(it1)
                builder.setTitle("交互测试").setOnKeyListener { dialog, keyCode, event ->
                    true
                }.create().show()
            }
        }
        view.check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.i("zkw", "check::>${isChecked}")
        }
        val swipe = view as SwipeRefreshLayout
        swipe.setOnRefreshListener {
            Log.i("zkw", "refresh!@!!!!!!")
            swipe.isRefreshing = false
        }

        view.imageView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.i("zkw", event.toString())
            }
            true
        }

        return view
    }

    override fun onResume() {
        super.onResume()

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        // The request code must be 0 or greater.
        private val PLUS_ONE_REQUEST_CODE = 0

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InteractiveFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): InteractiveFragment {
            val fragment = InteractiveFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
