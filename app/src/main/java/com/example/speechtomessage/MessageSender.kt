package com.example.speechtomessage

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager


class MessageSender(context : Context,listener:OnMessageSendingListener) {

    private  var mContext : Context = context
    private  var mListener : OnMessageSendingListener = listener

    val SENT = "SMS_SENT"
    val DELIVERED = "SMS_DELIVERED"


     fun sendSMS(m : Message) {

        val phoneNumber  = m.getContact()?.getNumber()
        val text  = m.getText()

        val sentPI = PendingIntent.getBroadcast(
            mContext, 0,
            Intent(SENT), 0
        )
        val deliveredPI = PendingIntent.getBroadcast(
            mContext, 0,
            Intent(DELIVERED), 0
        )

        //---when the SMS has been sent---
         mContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        m.setCaseMessage("Error! Message not sended because Generic failure")
                        m.setIsSended(false)
                        mListener.onMessageCase(m)
                    }

                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        m.setCaseMessage("Error! Message not sended because No service")
                        m.setIsSended(false)
                        mListener.onMessageCase(m)
                    }

                    SmsManager.RESULT_ERROR_NULL_PDU ->  {
                        m.setCaseMessage("Error! Message not sended because Null PDU")
                        m.setIsSended(false)
                        mListener.onMessageCase(m)
                        }

                    SmsManager.RESULT_ERROR_RADIO_OFF ->  {
                        m.setCaseMessage("Error! Message not sended because Radio off")
                        m.setIsSended(false)
                        mListener.onMessageCase(m)
                    }
                }
            }
        }, IntentFilter(SENT))

        //---when the SMS has been delivered---
         mContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {

                    Activity.RESULT_OK -> {
                        m.setCaseMessage("SMS delivered")
                        m.setIsSended(true)
                        mListener.onMessageCase(m)
                    }

                    Activity.RESULT_CANCELED -> {
                        m.setCaseMessage("Error! Message  not delivered")
                        m.setIsSended(false)
                        mListener.onMessageCase(m)
                    }
                }
            }
        }, IntentFilter(DELIVERED))


        val sms: SmsManager = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, text, sentPI, deliveredPI)
    }

    interface OnMessageSendingListener {

        fun onMessageCase(c : Message)

    }
}