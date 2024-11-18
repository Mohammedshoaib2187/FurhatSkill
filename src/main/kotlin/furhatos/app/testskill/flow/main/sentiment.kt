package furhatos.app.testskill.flow.main

import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.gestures.Gestures

val SentimentState = state {
    onEntry { furhat.listen() }
    onResponse {
        userResponses++
        val sentiment = analyzeSentiment(it.text)

        when (sentiment) {
            "positive" -> {
                furhat.gesture(Gestures.Smile)
                furhat.say("Good to hear!")
                appendDataToFile(name + " : " +it.text.toString())
                appendDataToFile(" model : Good to hear")
                goto(HowAreYou)
            }
            "negative" -> {
                furhat.gesture(Gestures.ExpressSad)
                furhat.say("I'm sorry to hear that.")
                appendDataToFile(name + " : " +it.text.toString())
                appendDataToFile(" model : I'm sorry to hear that.")
                goto(HowAreYou)
            }
            else -> {
                furhat.say("Thanks for sharing!")
                goto(HowAreYou)
            }
        }

    }
    onResponse<ByeIntent> {
        furhat.say(" It was nice talking to you.")
        goto(feedback)
    }
}

fun analyzeSentiment(input: String): String {
    return when {
        (input.contains("good", ignoreCase = true) || input.contains("great", ignoreCase = true)) && !(input.contains("not",ignoreCase = true)) -> "positive"
        (input.contains("bad", ignoreCase = true) || input.contains("not good", ignoreCase = true)) && (input.contains("not",ignoreCase = true)) -> "negative"
        else -> "neutral"
    }
}