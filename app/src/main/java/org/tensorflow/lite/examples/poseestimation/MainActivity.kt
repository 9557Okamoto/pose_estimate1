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
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.*
import org.tensorflow.lite.examples.poseestimation.training.Plank
import org.tensorflow.lite.examples.poseestimation.training.Squat

class MainActivity : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }
    private var speechRecognizer : SpeechRecognizer? = null
    private lateinit var recognize_text_view: TextView
    private lateinit var recognize_start_button: Button
    private lateinit var recognize_stop_button: Button

    private lateinit var surfaceView: SurfaceView

    private var device = Device.CPU

    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
//    private lateinit var x: TextView
//    private lateinit var y: TextView
//    private lateinit var name: TextView
//    private lateinit var taisei: TextView
    private lateinit var count: TextView
    private lateinit var calorie: TextView
    private lateinit var PlankBtn: Button
    private lateinit var SquatBtn: Button
    private var cameraSource: CameraSource? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recognize_text_view= findViewById(R.id.recognize_text_view)
        recognize_start_button = findViewById(R.id.recognize_start_button)
        recognize_stop_button = findViewById(R.id.recognize_stop_button)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
//        x = findViewById(R.id.x)
//        y = findViewById(R.id.y)
//        name = findViewById(R.id.name)
//        taisei = findViewById(R.id.taisei)
        count = findViewById(R.id.count)
        calorie = findViewById(R.id.calorie)
        PlankBtn = findViewById(R.id.PlankBtn)
        SquatBtn = findViewById(R.id.SquatBtn)
        surfaceView = findViewById(R.id.surfaceView)

        PlankBtn.setOnClickListener {
           if(cameraSource != null){
                cameraSource?.training = Plank(this)
            }
        }
        SquatBtn.setOnClickListener {
            if(cameraSource != null){
        cameraSource?.training = Squat(this)
            }
        }

        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        val granted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream { recognize_text_view.text = it })

        //recognize_start_button.setOnClickListener { speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)) }
        speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE))
        // setOnclickListner ?????????????????????????????????????????????????????????????????????????????????????????????
        //recognize_stop_button.setOnClickListener { speechRecognizer?.stopListening() }
    }

    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) { /** ?????????????????????????????? */ }
            override fun onReadyForSpeech(params: Bundle) { onResult("onReadyForSpeech") }
            override fun onBufferReceived(buffer: ByteArray) { onResult("onBufferReceived") }
            override fun onPartialResults(partialResults: Bundle) { onResult("onPartialResults") }
            override fun onEvent(eventType: Int, params: Bundle) { onResult("onEvent") }
            override fun onBeginningOfSpeech() { onResult("onBeginningOfSpeech") }
            override fun onEndOfSpeech() { onResult("onEndOfSpeech") }
            override fun onError(error: Int) { onResult("onError") }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                onResult("onResults " + stringArray.toString())
                speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE))
            }
        }
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

    // ????????????????????????????????????????????????
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // ??????????????????
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
//
//                        override fun xValue(data1: Float) {
//                            x.text = getString(R.string.tfe_pe_tv_x, data1)
//                        }
//
//                        override fun yValue(data2: Float) {
//                            y.text = getString(R.string.tfe_pe_tv_y, data2)
//                        }
//
//                        override fun nameValue(data3: BodyPart) {
//                            name.text = getString(R.string.tfe_pe_tv_name, data3)
//                        }
//
//                        override fun taisei(data4: Float) {
//                            taisei.text = getString(R.string.tfe_pe_tv_taisei, data4)
//                        }

//                        override fun message(data5: String) {
//                            message.text = data5
//                        }

                        override fun CalorieListener(Calorie: String) {
                            calorie.text = "${Calorie}????????????"
                        }

                        override fun CountListener(Count: String) {
                             count.text = Count
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

//    ?????????????????????????????????Lightning?????????)
    private fun createPoseEstimator() {
        showDetectionScore(true)
        val poseDetector = MoveNet.create(this, device, ModelType.Lightning)
        cameraSource?.setDetector(poseDetector)
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

//    ?????????????????????????????????
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
//    ???????????????????????????????????????????????????
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
