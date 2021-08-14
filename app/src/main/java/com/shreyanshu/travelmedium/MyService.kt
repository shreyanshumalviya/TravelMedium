package com.shreyanshu.travelmedium

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class MyService : Service() {
    private lateinit var audioManager: AudioManager
    private lateinit var t1: TextToSpeech
    private val TAG = "Background service"
    private val db = FirebaseFirestore.getInstance()

    override fun onBind(intent: Intent): IBinder? {

        Log.d(TAG, "onBind: return the communication channel to service")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onHandleIntent: t1 initialized")
        audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        t1 = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                t1.language = Locale("hin")
            }
        }
        t1.setSpeechRate(0.9f)
        t1.setPitch(0.9f)
        Log.d(TAG, "onHandleActionFoo: starting listener")

        db.collection("collection").orderBy("time").limitToLast(1)
            .addSnapshotListener { documentSnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

                if (firebaseFirestoreException != null) {
                    Log.w(TAG, "Listen failed.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val document = documentSnapshot?.documents?.get(0)

                if (document!!.exists()) {
                    Log.d(TAG, "Current data: ${document.data}")
                    val string = document.get("text") as String
                    t1.speak(string, TextToSpeech.QUEUE_FLUSH, null)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }
}
