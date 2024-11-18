package furhatos.app.testskill.flow.main

import furhatos.app.testskill.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.gestures.BasicParams
import furhatos.gestures.Gestures
import furhatos.gestures.defineGesture
import furhatos.nlu.SimpleIntent
import furhatos.util.Language

import io.ktor.client.*

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File


class ByeIntent : SimpleIntent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("bye", "goodbye", "see you", "bye bye")
    }
}
@Serializable
data class Part(
    val text: String
)

@Serializable
data class User(
    val name : String,
    val interests : List<String>
)

@Serializable
data class users(
    val users : List<User>
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role : String
)

@Serializable
data class PostRequestBody(
    val contents: List<Content>,
    val generationConfig: GenerationConfig,
    val system_instruction: SystemInstructions
)

@Serializable
data class Candidate(
    val content:Content
)

@Serializable
data class SystemInstructions(
    val parts: Part
)

@Serializable
data class GenerationConfig(
    val max_output_tokens: Int,
    val temperature: Double,
    val topP: Double,
    val topK: Double
)

@Serializable
data class ResponseText(
    val candidates: List<Candidate>
)

var userResponses = 0
var modelResponses = 0


val ThinkingGesture = defineGesture("ThinkingGesture") {
    frame(0.5) {
        BasicParams.GAZE_PAN to 0.2   // Slight gaze to the right
        BasicParams.GAZE_TILT to -0.1 // Slight gaze upward
        BasicParams.BROW_UP_LEFT to 0.3
        BasicParams.BROW_UP_RIGHT to 0.3
    }
    frame(1.0) {
        BasicParams.GAZE_PAN to -0.2  // Slight gaze to the left
        BasicParams.GAZE_TILT to -0.1 // Maintain upward gaze
        BasicParams.BROW_UP_LEFT to 0.0
        BasicParams.BROW_UP_RIGHT to 0.0
    }
    reset(1.5)
}

val Greeting: State = state(Parent) {

    onEntry {
        if(name.equals("")){
        furhat.say("Hello! What is your name?")
        appendDataToFile("model : Hello! What is your name?")
        goto(NameState)}
        else{
            goto(NameState)
        }
    }

}

var finalcontents = mutableListOf<Content>()
val APIKEY = "AIzaSyDr-qXY3N2BUQR19pw03xE76D1nJ77gme8"

val NameState = state {
    onEntry { if(name.equals("")){furhat.listen() }else{goto(newUser)} }
    onResponse {
        userResponses++
        if (it.text.contains("my name is", ignoreCase = true)) {
            val name1 = it.text.substringAfter("name is").trim()
            name = name1
            furhat.gesture(Gestures.Smile)
            furhat.say("Hello, $name! How are you?")
            appendDataToFile(" model : Hello, $name! How are you?")
            goto(SentimentState)
        } else {
            furhat.say("I didn't quite catch that. Can you tell me your name again?")
            furhat.listen()
        }
    }
    onResponse<ByeIntent> {
        furhat.say("It was nice talking to you.")
        goto(feedback)
    }
}

val newUser = state{
    onEntry {
        furhat.say("Hello, $name! How are you?")
        goto(SentimentState)
    }
}
val HowAreYou: State = state(Parent){
    onEntry {
        furhat.say("how can i help you today?")
        appendDataToFile(" model : how can i help you today?")
        val userData = importJsonFile("C:/Furhat/TestSkill/src/main/kotlin/furhatos/app/testskill/flow/main/data.json")
        if (userData != null) {
            val interests : List<String>? = getInterests(userData)
            if (interests != null) {
                furhat.say("It seems like i have history of your interests and preferences like ")
                furhat.say(interests.joinToString(separator = ","))
                furhat.say(" Do you want talk anything from that?")
                appendDataToFile("model : It seems like i have history of your interests and preferences like  ")
                appendDataToFile("model : "+ interests.joinToString(separator = ","))
                appendDataToFile("model : Do you want talk anything from that?")
            }
        }


        goto(Conversation)
    }
}

fun importJsonFile(fileName: String): users? {
    return try {
        val jsonString = File(fileName).readText() // Read JSON file as a string
        Json.decodeFromString<users>(jsonString) // Deserialize JSON into InteractionLog
    } catch (e: Exception) {
        println("Error reading JSON file: ${e.message}")
        null
    }
}

fun getInterests(userList : users): List<String>? {
    val user : User? = userList.users.find { it.name.equals(name, ignoreCase = true) }
    return user?.interests
}



