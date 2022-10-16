package org.tensorflow.lite.examples.poseestimation.training

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.math.*

class Squat(context: Context): CountTraining("Squat", context) {

    private var point1: Int = 0
    private var point2: Int = 0
    private var count1: Int = 0
    private var count2: Int = 0
    private var count3: Int = 0
    private var count4: Int = 0

    // 足を曲げたとき
    override fun isCount(person: Person): Boolean {

//        println("sho_x:${person.score.}")

        var sho_x = 0.0f
        var sho_y = 0.0f
        var hip_x = 0.0f
        var hip_y = 0.0f
        var knee_x = 0.0f
        var knee_y = 0.0f
        var ankle_x = 0.0f
        var ankle_y = 0.0f
        var nume = 0.0f
        var deno = 0.0f
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

        var dis = 0.0f

        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y
        knee_x = person.keyPoints[14].coordinate.x
        knee_y = person.keyPoints[14].coordinate.y
        ankle_x = person.keyPoints[16].coordinate.x
        ankle_y = person.keyPoints[16].coordinate.y
        hip_x = person.keyPoints[12].coordinate.x
        hip_y = person.keyPoints[12].coordinate.y

        nume = (hip_x - knee_x)*(ankle_x - knee_x) + (hip_y - knee_y)*(ankle_y - knee_y)
        deno1_1 = sqrt((ankle_x - knee_x)*(ankle_x - knee_x) + (ankle_y - knee_y)*(ankle_y - knee_y))
        deno1_2 = sqrt((hip_x - knee_x)*(hip_x - knee_x) + (hip_y - knee_y)*(hip_y - knee_y))
        deno = deno1_1 * deno1_2
        cos = nume/deno
        rad = acos(cos)
        deg = Math.toDegrees(rad.toDouble()).toFloat()

        nume2 = (sho_x - hip_x)*(knee_x - hip_x) + (sho_y - hip_y)*(knee_y - hip_y)
        deno2_1 = sqrt((sho_x - hip_x)*(sho_x - hip_x) + (sho_y - hip_y)*(sho_y - hip_y))
        deno2_2 = sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))
        deno2 = deno2_1 * deno2_2
        cos2 = nume2/deno2
        rad2 = acos(cos2)
        deg2 = Math.toDegrees(rad2.toDouble()).toFloat()

        dis = sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))/2f

//        if(deg in 60.0f..80.0f && deg2 in 60.0f..90.0f) {
//            println("dis:${dis}")
//            println("ankle_x:${ankle_x}")
//            println("knee_x:${knee_x}")
//        }

        return deg in 60.0f..80.0f && deg2 in 60.0f..90.0f && (ankle_x + dis) > knee_x
    }

    // 足が伸びてるか
    override fun isCountRelease(person: Person): Boolean {

        var sho_x = 0.0f
        var sho_y = 0.0f
        var hip_x = 0.0f
        var hip_y = 0.0f
        var knee_x = 0.0f
        var knee_y = 0.0f
        var ankle_x = 0.0f
        var ankle_y = 0.0f
        var nume = 0.0f
        var deno = 0.0f
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


        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y
        knee_x = person.keyPoints[14].coordinate.x
        knee_y = person.keyPoints[14].coordinate.y
        ankle_x = person.keyPoints[16].coordinate.x
        ankle_y = person.keyPoints[16].coordinate.y
        hip_x = person.keyPoints[12].coordinate.x
        hip_y = person.keyPoints[12].coordinate.y

        nume = (hip_x - knee_x)*(ankle_x - knee_x) + (hip_y - knee_y)*(ankle_y - knee_y)
        deno1_1 = sqrt((ankle_x - knee_x)*(ankle_x - knee_x) + (ankle_y - knee_y)*(ankle_y - knee_y))
        deno1_2 = sqrt((hip_x - knee_x)*(hip_x - knee_x) + (hip_y - knee_y)*(hip_y - knee_y))
        deno = deno1_1 * deno1_2
        cos = nume/deno
        rad = acos(cos)
        deg = Math.toDegrees(rad.toDouble()).toFloat()

        nume2 = (sho_x - hip_x)*(knee_x - hip_x) + (sho_y - hip_y)*(knee_y - hip_y)
        deno2_1 = sqrt((sho_x - hip_x)*(sho_x - hip_x) + (sho_y - hip_y)*(sho_y - hip_y))
        deno2_2 = sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))
        deno2 = deno2_1 * deno2_2
        cos2 = nume2/deno2
        rad2 = acos(cos2)
        deg2 = Math.toDegrees(rad2.toDouble()).toFloat()

        return deg >= 160.0f && deg2 >= 160.0f
    }

    // 注意
    override fun attention(person: Person): String{

        var message: String = message1

        var hip_x = 0.0f
        var hip_y = 0.0f
        var knee_x = 0.0f
        var knee_y = 0.0f
        var ankle_x = 0.0f
        var ankle_y = 0.0f
        var nume = 0.0f
        var deno1 = 0.0f
        var deno1_1 = 0.0f
        var deno1_2 = 0.0f
        var cos = 0.0f
        var deg = 0.0f
        var rad = 0.0f

        var hip_x2 = 0.0f
        var hip_y2 = 0.0f
        var knee_x2 = 0.0f
        var knee_y2 = 0.0f
        var ankle_x2 = 0.0f
        var ankle_y2 = 0.0f
        var nume2 = 0.0f
        var deno2 = 0.0f
        var deno2_1 = 0.0f
        var deno2_2 = 0.0f
        var cos2 = 0.0f
        var deg2 = 0.0f
        var rad2 = 0.0f

        var sho_x = 0.0f
        var sho_y = 0.0f
        var nume3 = 0.0f
        var deno3 = 0.0f
        var deno3_1 = 0.0f
        var deno3_2 = 0.0f
        var cos3 = 0.0f
        var deg3 = 0.0f
        var rad3 = 0.0f

        var dis = 0.0f

        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y
        knee_x = person.keyPoints[14].coordinate.x
        knee_y = person.keyPoints[14].coordinate.y
        ankle_x = person.keyPoints[16].coordinate.x
        ankle_y = person.keyPoints[16].coordinate.y
        hip_x = person.keyPoints[12].coordinate.x
        hip_y = person.keyPoints[12].coordinate.y

        knee_x2 = personList.last().keyPoints[14].coordinate.x
        knee_y2 = personList.last().keyPoints[14].coordinate.y
        ankle_x2 = personList.last().keyPoints[16].coordinate.x
        ankle_y2 = personList.last().keyPoints[16].coordinate.y
        hip_x2 = personList.last().keyPoints[12].coordinate.x
        hip_y2 = personList.last().keyPoints[12].coordinate.y

//        println("knee_x:${knee_x}")
//        println("knee_x2:${knee_x2}")

//        膝の角度
        nume = (hip_x - knee_x)*(ankle_x - knee_x) + (hip_y - knee_y)*(ankle_y - knee_y)
        deno1_1 = sqrt((ankle_x - knee_x)*(ankle_x - knee_x) + (ankle_y - knee_y)*(ankle_y - knee_y))
        deno1_2 = sqrt((hip_x - knee_x)*(hip_x - knee_x) + (hip_y - knee_y)*(hip_y - knee_y))
        deno1 = deno1_1 * deno1_2
        cos = nume/deno1
        rad = acos(cos)
        deg = Math.toDegrees(rad.toDouble()).toFloat()

//        ひとつ前の膝の角度
        nume2 = (hip_x2 - knee_x2)*(ankle_x2 - knee_x2) + (hip_y2 - knee_y2)*(ankle_y2 - knee_y2)
        deno2_1 = sqrt((ankle_x2 - knee_x2)*(ankle_x2 - knee_x2) + (ankle_y2 - knee_y2)*(ankle_y2 - knee_y2))
        deno2_2 = sqrt((hip_x2 - knee_x2)*(hip_x2 - knee_x2) + (hip_y2 - knee_y2)*(hip_y2 - knee_y2))
        deno2 = deno2_1 * deno2_2
        cos2 = nume2/deno2
        rad2 = acos(cos2)
        deg2 = Math.toDegrees(rad2.toDouble()).toFloat()

//        腰の角度
        nume3 = (sho_x - hip_x)*(knee_x - hip_x) + (sho_y - hip_y)*(knee_y - hip_y)
        deno3_1 = sqrt((sho_x - hip_x)*(sho_x - hip_x) + (sho_y - hip_y)*(sho_y - hip_y))
        deno3_2 = sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))
        deno3 = deno3_1 * deno3_2
        cos3 = nume3/deno3
        rad3 = acos(cos3)
        deg3 = Math.toDegrees(rad3.toDouble()).toFloat()

        dis = sqrt((knee_x - hip_x)*(knee_x - hip_x) + (knee_y - hip_y)*(knee_y - hip_y))/2f

        if(deg <= 80.0f && deg2 > 80.0f){
            point1 = 1
        }
        if(deg >= 160.0f && deg2 < 160.0f){
            point2 = 1
            count1++
        }
        if(deg >= 160.0f && deg2 < 160.0f && point1 == 0){
            message = message3
        }
        if(deg <= 80.0f && deg2 > 80.0f && point2 == 0 && count1 != 0) {
            message = message4
        }
        if(deg >= 160.0f && deg2 < 160.0f && point1 != 0){
            point1 = 0
        }
        if(deg <= 80.0f && deg2 > 80.0f && point2 != 0){
            point2 = 0
        }

        if(message == message1){
            if(deg >= 160.0f && deg3 <=160.0f){
                count2++
                if(count2==15) {
                    message = message5
                    count2 = 0
                }
//                }else{
//                    count2 = 0
//                }
            }
            if((ankle_x + dis) < knee_x){
                count3++
                if(count3==15) {
                    message = message9
                    count3 = 0
                }
//                }else{
//                    count3 = 0
//                }
            }else if(deg < 80.0f && deg3 < 60.0f){
                count4++
                if(count4==15) {
                    message = message5
                    count4 = 0
                }
//                }else{
//                    count4 = 0
//                }
            }
        }

        return when (message) {
            message3 -> {
                message3
            }
            message4 -> {
                message4
            }
            message5 -> {
                message5
            }
            message9 -> {
                message9
            }
            else -> {
                message1
            }
        }
    }
}