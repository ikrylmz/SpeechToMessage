package com.example.speechtomessage

class Message {


    private var contact : Contact? = null
    private var isSended : Boolean? = false
    private var text : String? = null
    private var messageCase : String? = null

    fun getContact() = contact
    fun setContact(contact: Contact) {
        this.contact = contact
    }

    fun getText() = text
    fun setText(text: String) {
        this.text = text
    }

    fun getCaseMessage() = messageCase
    fun setCaseMessage(messageCase: String) {
        this.messageCase = messageCase
    }

    fun getIsSended() = isSended
    fun setIsSended(isSended : Boolean) {
        this.isSended = isSended
    }
}