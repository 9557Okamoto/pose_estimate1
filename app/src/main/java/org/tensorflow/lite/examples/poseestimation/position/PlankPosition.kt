package org.tensorflow.lite.examples.poseestimation.position

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person

abstract class PlankPosition(name: String,context: Context) : Position(name, context) {

    override fun addPerson(person: Person) {
        if(personList.isNotEmpty()) {
            if(FullBody(person)&&Lie(person)&&Angle(person)){
                count++
            }
            if(count == 20){
                speak("プランクの立ち位置ＯＫです")
                message = "プランク"
                getResult()
            }

//            if(count == 40){
//                speak("プランク開始")
//            }
        }
        personList = personList.plus(person)
    }

    override fun getResult(): String {
        return message
    }



    protected abstract fun FullBody(person: Person): Boolean
    protected abstract fun Lie(person: Person): Boolean
    protected abstract fun Angle(person: Person): Boolean
}