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

            if(attention != message1){
                speak(attention)
                countattention++
                personList = personList.plus(person)
                return
            }

            if(isCount(person) && !counting){
                count++
                counting = true
            }else if(isCountRelease(person) && counting){
                speak(getResult())
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
    protected abstract fun attention(person: Person): String

    protected abstract fun isCount(person: Person): Boolean

    protected abstract fun isCountRelease(person: Person): Boolean


}