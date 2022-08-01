package org.tensorflow.lite.examples.poseestimation.training

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person

abstract class CountTraining(name: String, context: Context) : Training(name, context) {

    private var count: Int = 0
    private var countattention: Int = 0
    private var counting: Boolean = false

    override fun addPerson(person: Person) {
        if(personList.isNotEmpty()){
            var attention: String = attention(person)

//            if(attention == message1){
//                return
//            }
//            if(attention == message3){
//                speak(attention)
//                return
//            }
//            if(attention == message4){
//                speak(attention)
//                return
//            }
//            if(attention == message5) {
//                count++
//                return
//            }
//        }

            if(attention != message1){
                speak(attention)
                countattention++
                return
            }

            if(isCount(person) && !counting){
                count++
                counting = true
                speak(getResult())
            }else if(isCountRelease(person)){
                counting = false
            }
        }
        personList = personList.plus(person)
    }

    override fun getResult(): String {
        return "${count}回"
    }

    override fun getKcal(): String {
        return "消費カロリー：${0.4f * count}カロリー"
    }

    override fun getAttentionCount(): String{
        return "注意回数：${countattention}回"
    }

    protected abstract fun isCount(person: Person): Boolean

    protected abstract fun isCountRelease(person: Person): Boolean


}