package com.example.speechtomessage


import com.github.tamir7.contacts.Contacts
import java.util.*
import kotlin.collections.ArrayList

class ContactFetcher {

    private val LANGUAGE : String  = "tr" // for now

    inline fun <T, R : Comparable<R>> Iterable<T>.sortedWithLocaleBy(
        locale: Locale,
        crossinline selector: (T) -> R?
    ) = sortedWith(compareBy(java.text.Collator.getInstance(locale), selector))

    fun fetchList() :List<Contact>
    {
        var tempList :  MutableList<Contact> = mutableListOf()
        val list = Contacts.getQuery().find()
        for (contact in list) {

            var tempContact =  Contact()

            tempContact.setDisplayName(contact.displayName.capitalize())
            tempContact.setNumber(contact.phoneNumbers[0].number)
            tempList.add(tempContact)
        }

        return  tempList.sortedWithLocaleBy(Locale(LANGUAGE)) { it.getDisplayName() }
    }

    fun isListChanged(oldList: ArrayList<Contact>, newList: ArrayList<Contact>) : Boolean
    {
        val newListSize = newList.size
        val oldListSize = oldList.size

        return newListSize > oldListSize || newListSize < oldListSize
    }
    fun generateNewContactList(oldList: ArrayList<Contact>, newList: ArrayList<Contact>) : List<Contact>
    {
        var tempList = oldList

        for(i in oldList)
           if(i.getIsChecked())
               i.setIsChecked(false)


        newList.removeAll(oldList)
        oldList.removeAll(tempList)
        newList.addAll(oldList)
        newList.addAll(tempList)

        return  tempList.sortedWithLocaleBy(Locale(LANGUAGE)) { it.getDisplayName() }
    }
}