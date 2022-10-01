package org.tensorflow.lite.examples.poseestimation.training

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.math.abs
import kotlin.math.acos

class Plank(context: Context): KeepTraining("Plank", context) {

    private var count: Int = 0

    //横になっているか
    override fun Lie(person: Person): Boolean {
        var sho_x = 0.0f
        var sho_y = 0.0f
        var ankle_x = 0.0f
        var ankle_y = 0.0f

        ankle_x = person.keyPoints[16].coordinate.x
        ankle_y = person.keyPoints[16].coordinate.y
        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y


        return abs(sho_x - ankle_x) > abs(sho_y - ankle_y)
    }

    // カウントする条件
    override fun isKeep(person: Person): Boolean {
        var knee_x = 0.0f
        var sho_x = 0.0f
        var hip_x = 0.0f
        var knee_y = 0.0f
        var sho_y = 0.0f
        var hip_y = 0.0f
        var elb_x = 0.0f
        var elb_y = 0.0f
        var wrist_x = 0.0f
        var wrist_y = 0.0f

        var nume = 0.0f
        var deno1 = 0.0f
        var deno1_1 = 0.0f
        var deno1_2 = 0.0f
        var cos = 0.0f
        var deg = 0.0f
        var rad = 0.0f

        var nume2 = 0.0f
        var deno2 = 0.0f
        var deno2_1 = 0.0f
        var deno2_2 = 0.0f
        var cos2 = 0.0f
        var deg2 = 0.0f
        var rad2 = 0.0f

        var nume3 = 0.0f
        var deno3 = 0.0f
        var deno3_1 = 0.0f
        var deno3_2 = 0.0f
        var cos3 = 0.0f
        var deg3 = 0.0f
        var rad3 = 0.0f

        knee_x = person.keyPoints[14].coordinate.x
        knee_y = person.keyPoints[14].coordinate.y
        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y
        hip_x = person.keyPoints[12].coordinate.x
        hip_y = person.keyPoints[12].coordinate.y

        nume = (sho_x - hip_x)*(knee_x - hip_x) + (sho_y - hip_y)*(knee_y - hip_y)
        deno1_1 = kotlin.math.sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))
        deno1_2 = kotlin.math.sqrt((sho_x - hip_x)*(sho_x - hip_x) + (sho_y - hip_y)*(sho_y - hip_y))
        deno1 = deno1_1 * deno1_2
        cos = nume/deno1
        rad = acos(cos)
        deg = Math.toDegrees(rad.toDouble()).toFloat()

        elb_x = person.keyPoints[8].coordinate.x
        elb_y = person.keyPoints[8].coordinate.y
        wrist_x = person.keyPoints[10].coordinate.x
        wrist_y = person.keyPoints[10].coordinate.y

        nume2 = (wrist_x - elb_x)*(sho_x - elb_x) + (wrist_y - elb_y)*(sho_y - elb_y)
        deno2_1 = kotlin.math.sqrt((wrist_x - elb_x)*(wrist_x - elb_x) + (wrist_y - elb_y)*(wrist_y - elb_y))
        deno2_2 = kotlin.math.sqrt((sho_x - elb_x)*(sho_x - elb_x) + (sho_y - elb_y)*(sho_y - elb_y))
        deno2 = deno2_1 * deno2_2
        cos2 = nume2/deno2
        rad2 = acos(cos2)
        deg2 = Math.toDegrees(rad2.toDouble()).toFloat()

        nume3 = (elb_x - sho_x)*(hip_x - sho_x) + (elb_y - sho_y)*(hip_y - sho_y)
        deno3_1 = kotlin.math.sqrt((elb_x - sho_x)*(elb_x - sho_x) + (elb_y - sho_y)*(elb_y - sho_y))
        deno3_2 = kotlin.math.sqrt((hip_x - sho_x)*(hip_x - sho_x) + (hip_y - sho_y)*(hip_y - sho_y))
        deno3 = deno3_1 * deno3_2
        cos3 = nume3/deno3
        rad3 = acos(cos3)
        deg3 = Math.toDegrees(rad3.toDouble()).toFloat()

        return (deg >= 165) && deg2 in 70.0f..90.0f && deg3 in 70.0f..90.0f
    }

    // 腰が曲がっていないか？
    override fun attention(person: Person): String {
        var knee_x = 0.0f
        var sho_x = 0.0f
        var hip_x = 0.0f
        var knee_y = 0.0f
        var sho_y = 0.0f
        var hip_y = 0.0f
        var nume = 0.0f
        var deno = 0.0f
        var deno1 = 0.0f
        var deno2 = 0.0f
        var cos = 0.0f
        var deg = 0.0f
        var rad = 0.0f

        knee_x = person.keyPoints[14].coordinate.x
        knee_y = person.keyPoints[14].coordinate.y
        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y
        hip_x = person.keyPoints[12].coordinate.x
        hip_y = person.keyPoints[12].coordinate.y

        nume = (sho_x - hip_x)*(knee_x - hip_x) + (sho_y - hip_y)*(knee_y - hip_y)
        deno1 = kotlin.math.sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))
        deno2 = kotlin.math.sqrt((sho_x - hip_x)*(sho_x - hip_x) + (sho_y - hip_y)*(sho_y - hip_y))
        deno = deno1 * deno2
        cos = nume/deno
        rad = acos(cos)
        deg = Math.toDegrees(rad.toDouble()).toFloat()

        if(deg < 165){
            count++
        }else{
            count = 0
        }

        return if(count == 20){
            count=0
            "腰が曲がっています"
        }else{
            "その調子です"
        }
//        return if(deg < 165){
//            "腰が曲がっています"
//        }else{
//            "その調子です"
//        }
    }

    // 消費カロリーの計算
//    override fun getKcal(): Float {
//        var count = getResult().toFloat()
//        return count/10.0f
//    }

}