package org.tensorflow.lite.examples.poseestimation.position

import android.content.Context
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.training.CountTraining
import kotlin.math.abs

class Squat1(context: Context): SquatPosition("Squat", context) {

    override fun FullBody(person: Person): Boolean {
        var count: Int = 0
        var score: Float = 0.0f

        for(i in 0..16){
            score = person.keyPoints[i].score
            if(score > 0.3f){
                count++
            }
        }

        return count == 17
    }

    override fun Stand(person: Person): Boolean {
        var sho_x: Float = 0.0f
        var sho_y: Float = 0.0f
        var ankle_x: Float = 0.0f
        var ankle_y: Float = 0.0f

        ankle_x = person.keyPoints[16].coordinate.x
        ankle_y = person.keyPoints[16].coordinate.y
        sho_x = person.keyPoints[6].coordinate.x
        sho_y = person.keyPoints[6].coordinate.y

        return abs(sho_x - ankle_x) < abs(sho_y - ankle_y)
    }

    override fun Angle(person: Person): Boolean {
        var lsholder_x: Float = 0.0f
        var rsholder_x: Float = 0.0f
        var lhip_x: Float = 0.0f
        var rhip_x: Float = 0.0f
        var lknee_x: Float = 0.0f
        var rknee_x: Float = 0.0f
        var lankle_x: Float = 0.0f
        var rankle_x: Float = 0.0f

        lsholder_x = person.keyPoints[5].coordinate.x
        rsholder_x = person.keyPoints[6].coordinate.x
        lhip_x = person.keyPoints[11].coordinate.x
        rhip_x = person.keyPoints[12].coordinate.x
        lknee_x = person.keyPoints[13].coordinate.x
        rknee_x = person.keyPoints[14].coordinate.x
        lankle_x = person.keyPoints[15].coordinate.x
        rankle_x = person.keyPoints[16].coordinate.x

        return abs(lsholder_x - rsholder_x) < 30.0f && abs(lhip_x - rhip_x) < 30.0f && abs(lknee_x - rknee_x) < 30.0f && abs(lankle_x - rankle_x) < 30.0f
    }
}