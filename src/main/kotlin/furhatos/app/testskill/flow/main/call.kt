package furhatos.app.testskill.flow.main

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

suspend fun postPlacesRequest(request : String): String {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // This allows flexibility in handling unknown JSON fields
            })
        }
    }

    val bodycontent = Content(parts = listOf(Part(text = request)),
        role = "user"
    )
    finalcontents.add(bodycontent)
    val requestBody = PostRequestBody(
        system_instruction = SystemInstructions(
            parts = Part(
                text = "You are human respond like one. Avoid using emojis in responses. Keep your responses to minimum and just give key points"
            )
        ),
        contents = finalcontents,
        generationConfig = GenerationConfig(
            max_output_tokens = 50,
            temperature = 2.0,
            topP = 1.0,
            topK = 1.0
        )
    )

    return try {
        // POST request with request body
        val response: HttpResponse = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key="+ APIKEY) {
            contentType(ContentType.Application.Json) // Set content type to JSON
            setBody(requestBody) // Send the JSON body

        }

        // Get the response body as a string
        response.bodyAsText()
    } catch (e: Exception) {
        "Failed to post data: ${e.message}"
    } finally {
        client.close() // Close the client to free resources
    }
}