package furhatos.app.testskill.flow.main

import furhatos.app.testskill.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.gestures.Gesture
import furhatos.gestures.Gestures
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.io.IOException

var name :String = ""

val Conversation : State = state(Parent){
    onEntry {
        furhat.listen()
    }

    onNoResponse {
        furhat.say("Can you please repeat again?")
        furhat.listen()
    }

    onResponse<ByeIntent> {
        userResponses++
        furhat.say("It was nice talking to you.")
        goto(feedback)
    }

    onResponse{
        userResponses++
        val res = it.text.toString()
        runBlocking {
            if(res.contains("hello", ignoreCase = true))
            {
                furhat.gesture(Gestures.Nod)
                name = it.text.substringAfter("this is").trim()
                furhat.say(name +" wants to have a chat. Do you want me to attend?")
                goto(Confirmation)
            }
            furhat.gesture(ThinkingGesture)
            furhat.gesture(Gestures.Blink)
            furhat.gesture(Gestures.BrowRaise)
            val result = postPlacesRequest(it.text.toString()) // Call the suspend function inside a coroutine
            println(result) // Print the server response
            val json = Json { ignoreUnknownKeys = true }
            val response: ResponseText = json.decodeFromString(result)
            val data = response.candidates[0].content.parts[0].text
            if(data.contains("great",ignoreCase = true) || data.contains("good",ignoreCase = true) || data.contains("awesome",ignoreCase = true)){
                furhat.gesture(Gestures.Smile)
            }
            if(data.contains("oh",ignoreCase = true)){
                furhat.gesture(Gestures.Oh)
            }
            modelResponses++
            furhat.say(data)
            appendDataToFile(name+" : "+it.text.toString(),"conversation_log.txt")
            appendDataToFile("model : "+data,"conversation_log.txt")
            val content = Content(
                role = "model",
                parts = listOf(Part(text=data))
            )

            finalcontents.add(content)
            goto(checkNextConv)
        }
    }
}

val checkNextConv : State = state(Parent){
    onEntry {
        goto(Conversation)
    }
}

fun appendDataToFile(data: String, fileName: String = "conversation_log.txt") {
    try {
        // Specify an absolute path for clarity during debugging
        val file = File("C:/Furhat/TestSkill/src/main/kotlin/furhatos/app/testskill/flow/main/"+fileName)

        FileWriter(file, true).use { writer -> // 'true' opens the file in append mode
            writer.appendLine(data) // Adds data on a new line
            println("Data written successfully: $data") // Debugging output
        }
    } catch (e: IOException) {
        println("An error occurred while writing to the file: ${e.message}")
    }
}

