package org.tensorflow.lite.examples.poseestimation.training

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Person
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class KeepTraining(name: String, context: Context) : Training(name, context) {

    private var start: LocalDateTime? = null
    private var now: LocalDateTime? = null
    private var count: Int = 0
    private var time: Int = 0
    private var countattention: Int = 0

    override fun addPerson(person: Person) {
        if(personList.isNotEmpty()) {
            val lieattention: String = LieAttention(person)
            if(lieattention!=message2){
                speak(lieattention)
                countattention++
                return
            }
            val waistattention: String = WaistAttention(person)
            if(waistattention!=message2){
                speak(waistattention)
                count -= 2
                countattention++
                return
            }
            val elbowattention: String = ElbowAttention(person)
            if(elbowattention!=message2){
                speak(elbowattention)
                countattention++
                return
            }

            if(Waist(person) && Elbow(person) && Shoulder(person) && Lie(person) && start==null){
                start = LocalDateTime.now()
            }

            if(Waist(person) && Elbow(person) && Shoulder(person) && Lie(person) && start!=null) {
                now = LocalDateTime.now()
                time = ((now?.toEpochSecond(ZoneOffset.ofHours(+9)) ?: 0) - (start?.toEpochSecond(
                    ZoneOffset.ofHours(+9)
                ) ?: 0)).toInt()

                if (time >= 1) {
                    count++
                    time = 0
                    start = null
                    if (count % 10 == 0) {
                        speak(getResult())
                    }
                }
            }
        }


//            val attention: String = attention(person)
//
//            if (attention != message2) {
//                speak(attention)
//                count -= 2
//                countattention++
//                return
//            }
//
//
//            if(isKeep(person) && Lie(person) && start==null){
//                start = LocalDateTime.now()
//            }
//            if(isKeep(person) && Lie(person) && start!=null){
//                now = LocalDateTime.now()
//                time = ((now?.toEpochSecond(ZoneOffset.ofHours(+9)) ?: 0) - (start?.toEpochSecond(ZoneOffset.ofHours(+9)) ?: 0)).toInt()
//
//                if(time>=1){
//                    count++
//                    time = 0
//                    start = null
//                    if(count%10==0){
//                        speak(getResult())
//                    }
//                }
//            }

        personList = personList.plus(person)

    }

    override fun getResult(): String {
        return "${count}秒"
    }

    override fun getKcal(): String {
        return "消費カロリー：${count.toFloat()/10.0f}カロリー"
    }

    override fun getAttentionCount(): String{
        return "注意回数：${countattention}回"
    }

//    protected abstract fun isKeep(person: Person): Boolean

    protected abstract fun Lie(person: Person): Boolean
    protected abstract fun Waist(person: Person): Boolean
    protected abstract fun Shoulder(person: Person): Boolean
    protected abstract fun Elbow(person: Person): Boolean
    protected abstract fun WaistAttention(person: Person): String
    protected abstract fun LieAttention(person: Person): String
    protected abstract fun ElbowAttention(person: Person): String

//    private fun getSecond(): Int{
//        return ((finish?.toEpochSecond(ZoneOffset.ofHours(+9)) ?: 0) -
//                (start?.toEpochSecond(ZoneOffset.ofHours(+9)) ?: 0)).toInt()
//    }

//    interface KeepTrainingListener {
//        fun Attention(data: String)
//    }
}