package com.shreyanshu.travelmedium

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.shreyanshu.travelmedium.action.FOO"
private const val ACTION_BAZ = "com.shreyanshu.travelmedium.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.shreyanshu.travelmedium.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.shreyanshu.travelmedium.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class MyIntentService : IntentService("MyIntentService") {
    private lateinit var audioManager: AudioManager
    private lateinit var t1: TextToSpeech
    private val TAG = "Background service"
    private val db = FirebaseFirestore.getInstance()
    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent: t1 initialized")
        audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        t1 = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                t1.language = Locale("hin")
            }
        }
        t1.setSpeechRate(0.9f)
        t1.setPitch(0.9f)


        when (intent?.action) {
            ACTION_FOO -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionFoo(param1 ?: "", param2 ?: "")
            }
            ACTION_BAZ -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionBaz(param1 ?: "", param2 ?: "")
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
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
        db.collection("audio control").orderBy("time").limitToLast(1)
            .addSnapshotListener { documentSnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

                if (firebaseFirestoreException != null) {
                    Log.w(TAG, "Listen failed.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val document = documentSnapshot?.documents?.get(0)

                if (document!!.exists()) {
                    Log.d(TAG, "Current data: ${document.data}")
                    val string = document.get("text") as String
                    when (string) {
                        "decrease" ->
                            audioManager.adjustVolume(
                                AudioManager.ADJUST_LOWER,
                                AudioManager.FLAG_PLAY_SOUND
                            )
                        "increase" ->
                            audioManager.adjustVolume(
                                AudioManager.ADJUST_RAISE,
                                AudioManager.FLAG_PLAY_SOUND
                            )
                        "mute" ->
                            audioManager.adjustVolume(
                                AudioManager.ADJUST_MUTE,
                                AudioManager.FLAG_PLAY_SOUND
                            )
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        handleActionFoo("", "")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        //TODO("Handle action Baz")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, MyIntentService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, MyIntentService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
