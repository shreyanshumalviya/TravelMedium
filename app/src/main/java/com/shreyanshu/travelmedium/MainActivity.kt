package com.shreyanshu.travelmedium

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var tvInput: TextView
    var db = FirebaseFirestore.getInstance()

    private lateinit var t1: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInput = findViewById(R.id.input)
        
        t1 = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                t1.language = Locale("hin")
            }
        }
        val a: MutableSet<String> = HashSet()
        a.add("male") //here you can give male if you want to select male voice.

        val v = Voice("en-us-x-sfg#male_2-local", Locale("hi", "IN"), 400, 200, true, a)
        t1.voice=v
        t1.setSpeechRate(0.8f)
        t1.setPitch(0.5f)

        Log.d("TAG", "onHandleIntent: ${t1.voices}")
        t1.speak("Welcome", TextToSpeech.QUEUE_FLUSH, null)
    }

    fun record(view: View) {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            PackageManager.PERMISSION_GRANTED
        )

        val intentRecogniser = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intentRecogniser.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intentRecogniser.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            9000
        )

        Log.d("rrrrrrrrrrrrrrrrrrrrr", "record: start listening")

        val recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        var string = ""
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {
                //  updateMessage(string)
            }

            override fun onError(i: Int) {
                recognizer.stopListening()
                //   updateMessage(string)
            }

            override fun onResults(bundle: Bundle) {
                Log.d("rrrrrrrrrrrrrrrrrrrrr", "record: start listening")

                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    string = string + " " + matches[0]
                }
                updateMessage(string)
            }

            override fun onPartialResults(bundle: Bundle) {
                Log.d("rrrrrrrrrrrrrrrrrrrrr", "record: start listening")

                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    string = string + " Partial " + matches[0]
                }
                //        updateMessage(string)
            }

            override fun onEvent(i: Int, bundle: Bundle) {
                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    string = string + " event " + matches[0]
                }
                //      updateMessage(string)
            }
        })
        recognizer.startListening(intentRecogniser)
        Log.d("rrrrrrrrrrrrrrrrrrrrr", "record: start listening")

    }

    fun speak(view: View){
        t1.speak("nai", TextToSpeech.QUEUE_FLUSH, null)
    }

    fun updateMessage(message: String) {
        tvInput.text = message

        val id = db.collection("collection").document().id
        db.collection("collection").document(id).update("text", message).addOnSuccessListener{
            Toast.makeText(this, "$message updated", Toast.LENGTH_SHORT).show()
        }
        db.collection("collection").document(id).update("time", Calendar.getInstance().time)

    }

    fun raise(view:View){
        db.collection("audio control").document("one").update("text","increase")
        db.collection("audio control").document("one").update("fe",Calendar.getInstance().timeInMillis.toString())
    }

    fun lower(view:View){
        db.collection("audio control").document("one").update("text","decrease")
        db.collection("audio control").document("one").update("fe",Calendar.getInstance().timeInMillis.toString())
    }

    fun mute(view:View){
        db.collection("audio control").document("one").update("text","mute")
        db.collection("audio control").document("one").update("fe",Calendar.getInstance().timeInMillis.toString())
    }


    override fun onPause() {
        /*val intent = Intent(this@MainActivity, MyIntentService::class.java)
        startService(intent)*/
        //MyIntentService.startActionFoo(this, "", "")

        startService(Intent(this,MyService::class.java))
        super.onPause()
    }

    override fun onStop() {
        //MyIntentService.startActionFoo(this, "", "")
        startService(Intent(this,MyService::class.java))
        super.onStop()
        /*val intent = Intent(this@MainActivity, MyIntentService::class.java)
        startService(intent)*/
    }
}
