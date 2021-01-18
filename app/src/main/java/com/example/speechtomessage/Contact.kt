package com.example.speechtomessage

import java.io.Serializable

class Contact : Serializable{


    private var number : String? = null
    private var displayName : String? = null
    private var isChecked : Boolean = false

    fun getNumber() = number
    fun setNumber(s: String) {
        number = s
    }

    fun getDisplayName() = displayName
    fun setDisplayName(s: String) {
        displayName = s
    }

     fun getIsChecked() = isChecked
     fun setIsChecked(b : Boolean) {
         isChecked = b
     }


}