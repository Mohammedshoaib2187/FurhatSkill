package furhatos.app.testskill.flow.main

import furhatos.app.testskill.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileWriter
import java.io.IOException


@Serializable
data class response(
    val godspeed : Int,
    val rossrating : Int,
    val userresponses : Int,
    val modelresponses : Int
)

var godSpeedRating  = 3
var rossRating = 3
val feedback = state{
    onEntry {
        furhat.say("Thank you. Could you provide the feedback about the conversation. Rate from 1 - 5")
        furhat.listen()
    }
    onResponse {
        try{
            val res = it.text.toString().toInt()
            furhat.say("Thank you for "+ res+" rating")
            if(res <= 3){
                furhat.gesture(Gestures.ExpressSad)
            }else{
                furhat.gesture(Gestures.Smile)
            }

            godSpeedRating = res
            goto(rossrating)
        }catch(E : Exception){
            furhat.say("Can you just give me a rating number from 1 to 5")
            furhat.listen()
        }

    }
}

val rossrating = state{
    onEntry {
        furhat.say("Can you please rate the quality of the conversation")
        furhat.listen()
    }
    onResponse {
        try{
            val res = it.text.toString().toInt()
            rossRating = res
            furhat.say("Thank you for "+ res+" rating")
            if(res <= 3){
                furhat.gesture(Gestures.ExpressSad)
            }else{
                furhat.gesture(Gestures.Smile)
            }
            furhat.say("Have a good day!")
            saveFeedback()

        }catch(E : Exception){
            furhat.say("Can you just give me a rating number from 1 to 5")
            furhat.listen()
        }

    }
}

fun saveFeedback(){
    val csvFile = File("C:/Furhat/TestSkill/src/main/kotlin/furhatos/app/testskill/flow/main/response.csv")
    try {
        FileWriter(csvFile, true).use { writer ->
            val csvLine = "$godSpeedRating,$rossRating,$userResponses,$modelResponses,$name\n"
            writer.append(csvLine)
        }
    } catch (e: IOException) {
        println("An error occurred while writing to the CSV file: ${e.message}")
    }
}


