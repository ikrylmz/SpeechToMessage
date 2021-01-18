package com.example.speechtomessage

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.tamir7.contacts.Contacts
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MainActivity : AppCompatActivity(),
    ContactsFragment.OnFragmentInteractionListener,MessageSender.OnMessageSendingListener{


    private val TAG = "MainActivity"
    private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    private val TAG_NAME = this::class.java.simpleName
    private val READ_CONTACT_PERMISSION_REQUEST_CODE = 76

   private var listOfContacts : ArrayList<Contact> = ArrayList<Contact>()
   private lateinit var  listOfCheckedContacts: List<Contact>

    private lateinit var toBottom: Animation
    private lateinit var fromBottom: Animation
    private lateinit var toRight: Animation
    private lateinit var fromRight: Animation
    private lateinit var sendingScreen: SendingScreenFragment


    private var countSendedMessage: Int = 0

    private lateinit var mListen : Button
    private lateinit var mAddContact : ImageButton
    private lateinit var mFinish : Button
    private lateinit var mSend : Button
    private lateinit var mMessageEditTxt : EditText
    private lateinit var mPersonNameEditTxt : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Contacts.initialize(this)
        val mPrefForFirst = getSharedPreferences("firstrun", MODE_PRIVATE)
       // mPrefForFirst.edit().clear().apply()
        initContactList(mPrefForFirst)

       /* val sharedPreferenceFile = File("/data/data/$packageName/shared_prefs/")
        val listFiles: Array<File> = sharedPreferenceFile.listFiles()
        for (file in listFiles) {
            file.delete()
        }

        */

        // loadCheckedContacts()
       // makeContactsName()

        val colors : Array<Int> = arrayOf(
            ContextCompat.getColor(this, R.color.color1),
            ContextCompat.getColor(this, R.color.color2),
            ContextCompat.getColor(this, R.color.color3),
            ContextCompat.getColor(this, R.color.color4),
            ContextCompat.getColor(this, R.color.color5)
        )


        val heights : Array<Int> =  arrayOf(60, 30, 70, 40, 30)


        val recognitionProgressView = findViewById<View>(R.id.recognition_view) as RecognitionProgressView
        recognitionProgressView.setSpeechRecognizer(speechRecognizer)

        recognitionProgressView.setRecognitionListener(object : RecognitionListenerAdapter() {
            override fun onResults(results: Bundle) {
                startOver(recognitionProgressView)
                enableListenButton()
                showResults(results)
            }
        })

        recognitionProgressView.setColors(colors.toIntArray());
        recognitionProgressView.setBarMaxHeightsInDp(heights.toIntArray());
        recognitionProgressView.setCircleRadiusInDp(5);
        recognitionProgressView.setSpacingInDp(5);
        recognitionProgressView.setIdleStateAmplitudeInDp(5);
        recognitionProgressView.setRotationRadiusInDp(10);
        recognitionProgressView.play()

        mMessageEditTxt = findViewById<View>(R.id.message_edit_txt) as EditText
        mPersonNameEditTxt = findViewById<View>(R.id.person_name_et) as EditText
        mListen  = findViewById<View>(R.id.listen) as Button
        mFinish =  findViewById<View>(R.id.finish) as Button
        mSend =  findViewById<View>(R.id.send_btn) as Button
        mAddContact = findViewById<View>(R.id.add_contact_btn) as ImageButton
        val mDelete : Button =  findViewById<View>(R.id.delete) as Button



          toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom)
          fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom)
          toRight = AnimationUtils.loadAnimation(this, R.anim.to_right)
          fromRight = AnimationUtils.loadAnimation(this, R.anim.from_right)

        val activity = this
        mSend.setOnClickListener{


            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.alpha_0to1,
                R.anim.alpha_1to0
            )


            sendingScreen = SendingScreenFragment()

            val b  = Bundle()
            b.putInt("activity",countSendedMessage)
            sendingScreen.arguments = b
            Log.i("countSendedMessage",countSendedMessage.toString())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.add(R.id.frameLayout, sendingScreen).commit()

            val messageSender =  MessageSender(applicationContext,activity)
            sendMessage(messageSender)


        }

        mFinish.setOnClickListener{

            enableListenButton()
            startOver(recognitionProgressView)
            speechRecognizer.stopListening()

        }

        mAddContact.setOnClickListener{
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.from_right,
                R.anim.to_right,
                R.anim.to_left,
                R.anim.from_left
            )
            val contactsFragment: ContactsFragment = ContactsFragment.newInstance(listOfContacts)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.add(R.id.frameLayout, contactsFragment).commit()
        }

        mListen.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermission()

            } else {
                startRecognition()
                recognitionProgressView.postDelayed(
                    {
                        startRecognition()
                    }, 50
                )

                enableFinishButton()

            }
        }
        mDelete.setOnClickListener{
            mMessageEditTxt.text.clear()
        }
    }
    private fun  sendMessage(mSender:  MessageSender)
    {
        for(i in listOfCheckedContacts) {

            var m  =  Message()
            m.setText(mMessageEditTxt.text.toString())
            m.setContact(i)
            mSender.sendSMS(m)
        }
    }

    override fun onStop() {
        super.onStop()
        speechRecognizer?.destroy()
           saveContacts()
    }

    private  fun startOver(view: RecognitionProgressView)
    {
        view.stop()
        view.play()
    }

   private fun startRecognition() {

       val  intent =  Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
       intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName);
       intent.putExtra(
           RecognizerIntent.EXTRA_LANGUAGE_MODEL,
           RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
       )
       intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr");
       speechRecognizer.startListening(intent);

   }

    private fun initContactList(mPrefForFirst: SharedPreferences)
    {
        val contactsFetcher  = ContactFetcher()
        if (mPrefForFirst.getBoolean("firstrun", true)) {

            listOfContacts = ArrayList<Contact>(contactsFetcher.fetchList())
            mPrefForFirst.edit().putBoolean("firstrun", false).commit()

        }
        else {

            val mNewList : ArrayList<Contact> = ArrayList<Contact>(contactsFetcher.fetchList())
            val mOldList = loadContacts()



            listOfContacts = if(contactsFetcher.isListChanged(mOldList, mNewList)) {
                ArrayList<Contact>(contactsFetcher.generateNewContactList(mOldList, mNewList))
            } else{
                mOldList
            }


        }
    }
    private fun enableListenButton()
    {
        mFinish.visibility = View.INVISIBLE
        mListen.visibility = View.VISIBLE
        mFinish.startAnimation(toBottom)
        mListen.startAnimation(fromRight)
    }
    private fun enableFinishButton()
    {
        mListen.visibility = View.INVISIBLE
        mFinish.visibility = View.VISIBLE
        mFinish.startAnimation(fromBottom)
        mListen.startAnimation(toRight)
    }
    private fun showResults(results: Bundle?) {

        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val oldText = mMessageEditTxt.text
        val newText = matches?.get(0)
        mMessageEditTxt.setText("$oldText $newText")

    }

    private fun requestPermission()
    {
     if (ActivityCompat.shouldShowRequestPermissionRationale(
             this,
             Manifest.permission.RECORD_AUDIO
         )) {
      Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show();
    }
     else {

         ActivityCompat.requestPermissions(
             this,
             arrayOf<String>(Manifest.permission.RECORD_AUDIO),
             REQUEST_RECORD_AUDIO_PERMISSION_CODE
         )
    }
  }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {



        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf<String>(Manifest.permission.READ_CONTACTS),
                    READ_CONTACT_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: Array<Int>
    ) {
        if (requestCode == READ_CONTACT_PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val contacts = Contacts.getQuery().find()
        }
    }


    override fun onCheckedList(listOfCheckedContacts: ArrayList<Contact>) {

        this.listOfCheckedContacts  = listOfCheckedContacts
        makeContactsName()
        countSendedMessage = listOfCheckedContacts.size

    }

    override fun onAllList(listOfContacts: ArrayList<Contact>) {

        this.listOfContacts = listOfContacts
        Log.i("Back buton", "backked")

    }

    private fun makeContactsName() {
        if (!listOfCheckedContacts.isEmpty()) {

            var names: String = "To: "
            for (i in 0 until listOfCheckedContacts.count()) {

                if (i == 0)
                    names += listOfCheckedContacts[i].getDisplayName()
                else
                    names += ", ${listOfCheckedContacts[i].getDisplayName()}"
            }

            mPersonNameEditTxt.setText(names)
        }
        else
            Toast.makeText(applicationContext, "Bos ", Toast.LENGTH_SHORT).show()
    }

    private fun saveContacts()
    {
        val mPrefs = getSharedPreferences("contactList6", MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(listOfContacts)



        prefsEditor.putString("contactList7", json)
        prefsEditor.apply()
    }

    private fun loadContacts() : ArrayList<Contact> {

        val mPrefs = getSharedPreferences("contactList6", MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString("contactList7", null)
        val type: Type = object : TypeToken<ArrayList<Contact>>() {}.type



        return gson.fromJson(json, type)
    }

    override fun onMessageCase(m: Message) {

        Log.i("onMessageCase"," tikladi  ")
        sendingScreen.addMessageCase(m)

    }
}


