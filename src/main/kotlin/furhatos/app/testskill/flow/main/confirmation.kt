package furhatos.app.testskill.flow.main

import furhatos.app.testskill.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures

val Confirmation : State = state(Parent){
    onEntry {
        furhat.listen()
    }
    onNoResponse {
        furhat.say("Can you please repeat again?")
        furhat.listen()
    }
    onResponse{
        val response = it.text.toString()
        if(response.contains("yes",ignoreCase = true)){
            furhat.gesture(Gestures.Nod)
            saveFeedback()
            userResponses = 0
            modelResponses = 0
            finalcontents = mutableListOf()
            goto(Greeting)
        }
        else{
            furhat.say("OK, We can continue..")
            goto(Conversation)
        }
    }
    onResponse<ByeIntent> {
        furhat.say("It was nice talking to you.")
        goto(feedback)
    }
}