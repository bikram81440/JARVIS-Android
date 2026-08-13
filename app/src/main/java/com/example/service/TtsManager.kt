package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context, private val onSpeakingStateChanged: (Boolean) -> Unit) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.UK) // British accent for JARVIS
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "UK English language not supported, defaulting to US")
                tts?.language = Locale.US
            }
            isInitialized = true
        } else {
            Log.e("TtsManager", "TTS initialization failed with status $status")
        }
    }

    fun speak(text: String, speechSpeed: Float = 1.0f) {
        if (!isInitialized) return
        tts?.setSpeechRate(speechSpeed)
        onSpeakingStateChanged(true)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "JarvisUtteranceId")

        // Periodically check if speaking has finished or use a listener
        // For simplicity, we can also estimate or reset state. TextToSpeech has UtteranceProgressListener.
        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onSpeakingStateChanged(true)
            }
            override fun onDone(utteranceId: String?) {
                onSpeakingStateChanged(false)
            }
            override fun onError(utteranceId: String?) {
                onSpeakingStateChanged(false)
            }
        })
    }

    fun stop() {
        tts?.stop()
        onSpeakingStateChanged(false)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
