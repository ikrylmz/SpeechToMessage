package com.example.speechtomessage

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.refactor.library.SmoothCheckBox
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import java.security.SecureRandom
import java.util.HashMap

class ContactsItemsAdapter(
    private var mClickListener: OnCheckBoxClickListener
)
    :ContactsAdapter<RecyclerView.ViewHolder>(),
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_item, parent, false)

        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val mItemtxt = holder.itemView.findViewById(R.id.item_txt) as TextView
        val mItemcheck = holder.itemView.findViewById(R.id.item_check) as SmoothCheckBox
        mItemtxt.text = getItem(position).getDisplayName()

        val c : Contact  = getItem(position)

        setChecks(c,mItemcheck)

        mItemcheck.setOnCheckedChangeListener { smoothCheckBox, b ->
            if (b) {
                c.setIsChecked(true)
                mClickListener.onItemCheck(c)
            }
            else {
                c.setIsChecked(false)
                mClickListener.onItemUnCheck(c)
            }
        }
    }
    private fun setChecks(c : Contact ,scb : SmoothCheckBox)
    {
        if(c.getIsChecked()) {
            scb.setChecked(true, false)
            mClickListener.onItemCheck(c)
        }
    }

   override fun getHeaderId(position: Int) : Long
   {
       return getItem(position).getDisplayName()?.get(0)!!.toLong()
   // getItem(position).getName()?.get(0)!!.toLong()
   }

    override fun onCreateHeaderViewHolder(parent: ViewGroup) :  RecyclerView.ViewHolder
    {
        val view  =  LayoutInflater.from(parent.context)
                .inflate(R.layout.view_header, parent, false)
        return object : RecyclerView.ViewHolder(view){}
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        val textView = holder.itemView as TextView
        textView.text = getItem(position).getDisplayName()?.get(0).toString()
    }

    private fun  getRandomColor() : Int
    {
        val rgen = SecureRandom()
        return Color.HSVToColor(150, floatArrayOf(rgen.nextInt(359).toFloat(), 1F, 1F))
    }

    interface OnCheckBoxClickListener {
        fun onItemCheck(c: Contact)
        fun onItemUnCheck(c: Contact)
    }

}