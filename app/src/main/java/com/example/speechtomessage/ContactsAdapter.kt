package com.example.speechtomessage

import androidx.recyclerview.widget.RecyclerView

abstract class ContactsAdapter<VH : RecyclerView.ViewHolder>:RecyclerView.Adapter<VH>() {

    private val items = ArrayList<Contact>()
    init{
        setHasStableIds(true)
    }

    open fun addAll(collection: Collection<Contact>) {
        if (collection != null) {
            items.addAll(collection)
            notifyDataSetChanged()
        }
    }

    private fun addAll(vararg items: Contact) {
        addAll(items.asList())
    }

    fun getItem(position: Int):Contact {
        return items.get(position)
    }
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }
    override fun getItemCount(): Int {
        return items.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}