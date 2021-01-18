package com.example.speechtomessage


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SendingScreenFragment : Fragment() {

    private lateinit var mSendedMessage : TextView
    private lateinit var mUnsendedRecyclerView : RecyclerView
    private lateinit var mCloseImage: ImageView
    private lateinit var mTickImage: ImageView

    private lateinit var mBack_fromLoading: ImageButton
    private lateinit var progresBar : ProgressBar
    private  var countSendedMessage : Int = 0

    private val ALLSENDED  : Int = 0
    private val SOMEUNSENDED  : Int = 1
    private val NONESENDED  : Int = 2


    private var mMessageCaseList = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v  = inflater.inflate(R.layout.fragment_loading_screen, container, false)

        countSendedMessage = arguments!!.getInt("activity")
        Log.i("countSendedMessage","$countSendedMessage for fragment")
        // this.animat(anim2);

        mCloseImage =   v.findViewById(R.id.close_iv) as ImageView
        mTickImage =   v.findViewById(R.id.tick_iv) as ImageView
        mBack_fromLoading =   v.findViewById(R.id.back_fromLoading) as ImageButton

        mSendedMessage = v.findViewById(R.id.message_send) as TextView
        mUnsendedRecyclerView = v.findViewById(R.id.notsendeds) as RecyclerView
        progresBar = v.findViewById(R.id.progressBar) as ProgressBar
        val orientation = getLayoutManagerOrientation(resources.configuration.orientation)
        val layoutManager = LinearLayoutManager(this.requireContext(), orientation, false)

        mUnsendedRecyclerView.layoutManager = layoutManager



        /*
        Handler().postDelayed({
            mSendedMessage.animation= animSendedMessage
            mSendedMessage.visibility = View.VISIBLE
            check.visibility = View.VISIBLE
            progresBar.visibility = View.INVISIBLE
            check.check();
        }, 1000)



        animSendedMessage.setAnimationListener(object: Animation.AnimationListener {

            override fun onAnimationStart(animation:Animation) {

            }

            override fun onAnimationEnd(animation:Animation) {
                mSendedMessage.visibility = View.VISIBLE


            }
            override fun onAnimationRepeat(animation:Animation) {
            }
        })
     */
        return v
    }


    private fun messagesSended(case: String?)
    {
        val animSendedMessage = AnimationUtils.loadAnimation(context, R.anim.alpha_0to1)
        mSendedMessage.visibility = View.VISIBLE
        mSendedMessage.animation = animSendedMessage
        mTickImage.animation = animSendedMessage
        mTickImage.visibility =  View.VISIBLE
        progresBar.visibility =  View.INVISIBLE
        mSendedMessage.text =  case


        animSendedMessage.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

                Handler().postDelayed({
                    requireActivity().onBackPressed()
                }, 3000)


            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }
    private fun messagesUnsended(case: String?)
    {
        val anim = AnimationUtils.loadAnimation(context, R.anim.alpha_0to1)
        mSendedMessage.visibility = View.VISIBLE
        mSendedMessage.text =  case
        mSendedMessage.animation = anim

        mBack_fromLoading.animation = anim

        mCloseImage.visibility =  View.VISIBLE
        progresBar.visibility =  View.INVISIBLE

    }

    private fun someMessagesUnsended(messageList: List<Message>)
    {

        showList(messageList)

        val adapter = SendingCaseAdapter(messageList, requireContext())
        mUnsendedRecyclerView.adapter = adapter
        mUnsendedRecyclerView.visibility = View.VISIBLE

        messagesUnsended("Some Messages Unsended")

    }

    private fun showList(contactList: List<Message>)
    {
         for(i in contactList)
         {
             Log.i("messsagesUnsended", i.getContact()?.getDisplayName())
         }
    }

    fun addMessageCase(m: Message){

         mMessageCaseList.add(m)

         if(countSendedMessage == mMessageCaseList.size)
         {
             if(countSendedMessage == 1) {
                 oneMesssageSended(m)
                 return
             }
             listHandler()
         }
    }

    private fun oneMesssageSended(m: Message){

        Log.i("oneMesssageSended","${m.getIsSended()} ")
        if(m.getIsSended() == true)
            messagesSended("Message Sended")
        else
            messagesUnsended(m.getCaseMessage())

    }

    private fun listGet(mMessageCaseList: List<Message>) : Int
    {
       var count : Int = 0

       for (i in mMessageCaseList)
       {
         if(i.getIsSended() == true)
            count++
       }
        return if(count == mMessageCaseList.size)
            ALLSENDED
        else if (count == 0)
            NONESENDED
        else
            SOMEUNSENDED
    }
    private fun listHandler()
    {
        when(listGet(mMessageCaseList))
        {
            ALLSENDED -> messagesSended("All Messages Sended")
            SOMEUNSENDED -> someMessagesUnsended(mMessageCaseList)
            NONESENDED -> messagesUnsended("None Of Message Sended ${mMessageCaseList[0].getCaseMessage()}")
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
}
