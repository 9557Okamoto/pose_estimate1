package org.tensorflow.lite.examples.poseestimation.position

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.training.Squat

abstract class SquatPosition(name: String,context: Context) : Position(name, context) {

    override fun addPerson(person: Person) {
        if(personList.isNotEmpty()) {
            if(FullBody(person)&&Stand(person)&&Angle(person)){
                count++
            }
            println(count)
            if(count == 20){
                speak("スクワットの立ち位置ＯＫです")
                message = "スクワット"
                getResult()
            }
//            if(count == 80){
//                speak("スクワット開始")
//            }
        }
        personList = personList.plus(person)
    }

    override fun getResult(): String {
        return message
    }

    protected abstract fun FullBody(person: Person): Boolean
    protected abstract fun Stand(person: Person): Boolean
    protected abstract fun Angle(person: Person): Boolean
}