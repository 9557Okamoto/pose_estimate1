package org.tensorflow.lite.examples.poseestimation.position

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person
import android.speech.tts.TextToSpeech
import java.util.*

abstract class Position(val name: String, val context: Context): TextToSpeech.OnInitListener {
    var personList : List<Person> = mutableListOf()
    private var tts: TextToSpeech? = null

    abstract fun getResult(): String

    init {
        this.tts = TextToSpeech(context, this)
    }

    abstract fun addPerson(person: Person)

    protected fun speak(text: String){
        this.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // ロケールの指定
            val locale = Locale.JAPAN
            if (this.tts!!.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                this.tts!!.language = Locale.JAPAN
            }

            //声の速度
            this.tts!!.setSpeechRate(1.2f)
            //声の高さ
            this.tts!!.setPitch(0.8f)
            // 音声合成の実行
//            this.tts!!.speak("こんにちは", TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        }
    }
}
