package com.example.speechtomessage

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.github.tamir7.contacts.Contacts
import com.google.gson.Gson
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import java.util.*
import kotlin.collections.ArrayList


class ContactsFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var listOfCheckedContacts : ArrayList<Contact> = ArrayList<Contact>()
    private var listOfContacts : ArrayList<Contact> = ArrayList<Contact>()
    lateinit var mRecyclerView  : RecyclerView


    companion object {

        var LIST_KEY :  String = "list_key"

        fun newInstance(list: ArrayList<Contact>) : ContactsFragment{

                    val fragment = ContactsFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(LIST_KEY, list)
                    fragment.arguments = bundle
                    return fragment;
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val v =  inflater.inflate(R.layout.fragment_contacts, container, false)
        mRecyclerView = v.findViewById(R.id.recyclerview) as RecyclerView

        val  mbackBtn  = v.findViewById(R.id.backBtn) as ImageButton

        val mClicklistener   = object  : ContactsItemsAdapter.OnCheckBoxClickListener {
            override fun onItemCheck(c: Contact) {
                listOfCheckedContacts.add(c)

                val index : Int = listOfContacts.indexOf(c)
                if(index > -1)
                    listOfContacts[index].setIsChecked(true)
                else
                    Log.e("LIST","no such element")
            }
            override fun onItemUnCheck(c: Contact) {
                listOfCheckedContacts.remove(c)
                val index : Int = listOfContacts.indexOf(c)
                if(index > -1)
                    listOfContacts[index].setIsChecked(false)
                else
                    Log.e("LIST","no such element")
            }
        }

        listOfContacts = arguments!!.getSerializable(
            LIST_KEY
        ) as  ArrayList<Contact>

        for (i in listOfContacts)
            Log.i("listOfContactsFragment","${i.getDisplayName()}  ${i.getIsChecked()} ")
        /*
        var recyclerViewReadyCallback = object : RecyclerViewReadyCallback {
            override fun onLayoutReady() {
              setChecks(mRecyclerView)
            }
        }
         */



        val adapter = ContactsItemsAdapter(mClicklistener)

        adapter.addAll(listOfContacts)
        mRecyclerView.adapter = adapter

        mbackBtn.setOnClickListener {
            mListener?.onCheckedList(listOfCheckedContacts)
            mListener?.onAllList(listOfContacts)
            //Log.i("CALİSMA","onBack pressed")
            requireActivity().onBackPressed()
        }

        // Set layout manager
        // Set layout manager
        val orientation = getLayoutManagerOrientation(resources.configuration.orientation)
        val layoutManager = LinearLayoutManager(this.requireContext(), orientation, false)
        mRecyclerView.layoutManager = layoutManager


        val headersDecor = StickyRecyclerHeadersDecoration(adapter)
        mRecyclerView.addItemDecoration(headersDecor)


        mRecyclerView.addItemDecoration(DividerDecoration(this.requireContext()))





        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                headersDecor.invalidateHeaders()
            }
        })
        //postAndNotifyAdapter(Handler(), mRecyclerView)
        return v
    }



     override fun  onAttach(context: Context) {
        super.onAttach(context);
        if (context is OnFragmentInteractionListener) {
            mListener = context;
        } else {

        }
    }

    private fun getLayoutManagerOrientation(activityOrientation: Int):Int {

        return if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            LinearLayoutManager.VERTICAL
        }
        else
        {
            LinearLayoutManager.HORIZONTAL
        }
    }
    interface OnFragmentInteractionListener {

        fun onCheckedList(listOfCheckedContacts: ArrayList<Contact>)
        fun onAllList(listOfContacts: ArrayList<Contact>)
    }

}