/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.*
import org.tensorflow.lite.examples.poseestimation.training.Plank
import org.tensorflow.lite.examples.poseestimation.training.Squat
import org.tensorflow.lite.examples.poseestimation.position.Plank1
import org.tensorflow.lite.examples.poseestimation.position.Squat1
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }

    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer : SpeechRecognizer? = null
    private lateinit var recognize_text_view: TextView
    private lateinit var surfaceView: SurfaceView
    private var device = Device.CPU
    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var count: TextView
    private lateinit var calorie: TextView
    private lateinit var attention: TextView
    private var cameraSource: CameraSource? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textToSpeech = TextToSpeech(this, this)

        recognize_text_view= findViewById(R.id.recognize_text_view)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
        count = findViewById(R.id.count)
        calorie = findViewById(R.id.calorie)
        attention = findViewById(R.id.attention)
        surfaceView = findViewById(R.id.surfaceView)

        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        val granted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

//        println(cameraSource?.message)

        if(cameraSource != null){
            if(cameraSource?.message == "プランク"){
                cameraSource?.position = null
                cameraSource?.training = Plank(this)
                speak("プランク開始")
            }
        }

        if(cameraSource != null){
            if(cameraSource?.message == "スクワット"){
                cameraSource?.position = null
                cameraSource?.training = Squat(this)
                speak("スクワット開始")
            }
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream {
//            if(it == "プランク"){
//                speak("プランク開始")
//                if(cameraSource != null){
//                    cameraSource?.training = Plank(this)
//                }
//            }
//            if(it == "スクワット"){
//                speak("スクワット開始")
//                if(cameraSource != null){
//                    cameraSource?.training = Squat(this)
//                }
//            }
//            if(it == "終了"){
//                speak("トレーニング終了")
//                if(cameraSource != null){
//                    cameraSource?.training = null
//                }
//            }
//            recognize_text_view.text = it
            if(it == "プランク"){
                speak("立ち位置確認")
                if(cameraSource != null){
                    cameraSource?.position = Plank1(this)
                }
            }
            if(it == "スクワット"){
                speak("立ち位置確認")
                if(cameraSource != null){
                    cameraSource?.position = Squat1(this)
                }
            }
//            if(it == "プランク開始"){
//                speak("プランク開始")
//                if(cameraSource != null){
//                    cameraSource?.position = null
//                    cameraSource?.training = Plank(this)
//                }
//            }
//            if(it == "スクワット開始"){
//                speak("スクワット開始")
//                if(cameraSource != null){
//                    cameraSource?.position = null
//                    cameraSource?.training = Squat(this)
//                }
//            }
            if(it == "終了"){
                speak("トレーニング終了")
                if(cameraSource != null){
                    cameraSource?.training = null
                }
            }
            recognize_text_view.text = it
        })
        speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE))
    }

    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) { /** 今回は特に利用しない */ }
            override fun onReadyForSpeech(params: Bundle) { onResult("onReadyForSpeech") }
            override fun onBufferReceived(buffer: ByteArray) { onResult("onBufferReceived") }
            override fun onPartialResults(partialResults: Bundle) { onResult("onPartialResults") }
            override fun onEvent(eventType: Int, params: Bundle) { onResult("onEvent") }
            override fun onBeginningOfSpeech() { onResult("onBeginningOfSpeech") }
            override fun onEndOfSpeech() { onResult("onEndOfSpeech") }
            override fun onError(error: Int) {
                onResult("onError")
                speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE))
            }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                //onResult(stringArray.toString())
                if (stringArray != null) {
                    onResult(stringArray.joinToString(""))
                }
                speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE))
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val locale = Locale.JAPAN
                if (tts.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = Locale.JAPAN
                }
                //声の速度
                this.textToSpeech!!.setSpeechRate(1.2f)
                //声の高さ
                this.textToSpeech!!.setPitch(0.8f)
            }
        }
    }

    private fun speak(text: String){
        this.textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // カメラの許可が出ているか確認する
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // カメラを開く
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                       }

                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
                            tvScore.text = getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
                        }
                        override fun CalorieListener(Calorie: String) {
                            calorie.text = Calorie
                        }
                        override fun CountListener(Count: String) {
                             count.text = Count
                        }
                        override fun AttentionListener(Attention: String) {
                            attention.text = Attention
                        }
                    }).apply {
                        prepareCamera()
                    }
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

//    判別器の準備（一人でのLightningに固定)
    private fun createPoseEstimator() {
        showDetectionScore(true)
        val poseDetector = MoveNet.create(this, device, ModelType.Thunder)
        //val poseDetector = MoveNet.create(this, device, ModelType.Lightning)
        cameraSource?.setDetector(poseDetector)
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

//    カメラの許可を要求する
    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    /**
     * Shows an error message dialog.
     */
//    エラーが発生したらダイアログを出す
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
