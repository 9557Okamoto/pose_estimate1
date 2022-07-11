package org.tensorflow.lite.examples.poseestimation.training

import org.tensorflow.lite.examples.poseestimation.data.Person

abstract class Training(val name: String){

    var personList : List<Person> = mutableListOf()

    var message1 : String = ""
    var message2 : String = "その調子です"
    var message3 : String = "膝をもっと曲げてください"
    var message4: String = "膝を伸ばし切ってください"


    // 骨格情報を入力
    abstract fun addPerson(person: Person)

    // 注意
    protected abstract fun attention(person: Person): String

    // 結果（途中結果）を渡す
    abstract fun getResult(): String

    // 消費カロリーを渡す
    abstract fun  getKcal(): Float

    // 入力された文字列を読み上げる
    // TODO
    protected fun speak(text: String){

    }

//    abstract fun getMessage(): String

}