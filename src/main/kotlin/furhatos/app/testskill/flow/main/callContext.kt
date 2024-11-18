package furhatos.app.testskill.flow.main
import furhatos.app.testskill.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.gestures.Gesture
import furhatos.gestures.Gestures
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException


fun loadJSONFromFile(filename: String): JSONObject {
    val fileContent = File("C:/Furhat/TestSkill/src/main/kotlin/furhatos/app/testskill/flow/main/dataset.json").readText()
    return JSONObject(fileContent)
}

val AskPreferences = state {
    onEntry {
        furhat.say("Hello! Would you like to know about movies, sports, or places?")
        furhat.listen()
    }

    onResponse {
        val userPreference = it.text.toLowerCase()

        when {
            "movie" in userPreference -> {
                furhat.ask("Which movie are you interested in? For example, Inception or The Matrix.")
                goto(FetchMovieData)
            }
            "sport" in userPreference -> {
                furhat.ask("Which sports event would you like to know about? For example, the Olympics or FIFA World Cup.")
                goto(FetchSportsData)
            }
            "place" in userPreference -> {
                furhat.ask("Which place are you curious about? For example, New York or Paris.")
                goto(FetchPlaceData)
            }

            "science" in userPreference -> {
                furhat.ask("What topic in science are you interested in? Fields, discoveries, scientists, or theories?")
                goto(FetchScienceData)
            }
            "technology" in userPreference -> {
                furhat.ask("What technology topic are you interested in? For example, Artificial Intelligence or Blockchain.")
                goto(FetchTechnologyData)
            }
            "culture" in userPreference -> {
                furhat.ask("What cultural tradition would you like to know about? For example, Diwali or Carnival.")
                goto(FetchCultureData)
            }

            "bye" in userPreference->{
                goto(Idle)
            }
            else -> {
                furhat.say("I'm sorry, I didn’t understand. Could you specify movies, sports, or places?")
                reentry()
            }
        }
    }
}


val FetchMovieData = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val moviesData: JSONObject = loadJSONFromFile("moviesData.json")
        val moviesArray = moviesData.getJSONObject("movies").getJSONArray("collection")

        val userMovie = it.text.toLowerCase()
        var foundMovie: JSONObject? = null

        for (i in 0 until moviesArray.length()) {
            val movie = moviesArray.getJSONObject(i)
            if (userMovie in movie.getString("title").toLowerCase()) {
                foundMovie = movie
                break
            }
        }

        if (foundMovie != null) {
            val title = foundMovie.getString("title")
            val genres = foundMovie.getJSONArray("genres").join(", ")
            val releaseYear = foundMovie.getInt("releaseYear")
            val director = foundMovie.getJSONObject("director").getString("name")
            val imdbRating = foundMovie.getJSONObject("ratings").getDouble("IMDb")

            furhat.say("The movie $title was released in $releaseYear, directed by $director. It belongs to the genres: $genres. It has an IMDb rating of $imdbRating.")
        } else {
            furhat.say("I couldn't find information on that movie.")
            goto(Conversation)
        }


    }
}



val FetchSportsData = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val sportsData = loadJSONFromFile("sportsData.json")
        val eventsArray = sportsData.getJSONObject("sports").getJSONArray("events")

        val userSport = it.text.toLowerCase()
        var foundEvent: JSONObject? = null

        for (i in 0 until eventsArray.length()) {
            val event = eventsArray.getJSONObject(i)
            if (userSport in event.getString("name").toLowerCase()) {
                foundEvent = event
                break
            }
        }

        if (foundEvent != null) {
            val name = foundEvent.getString("name")
            val location = foundEvent.getString("location")
            val type = foundEvent.getString("type")
            val frequency = foundEvent.getString("frequency")
            val famousAthletes = foundEvent.getJSONArray("famousAthletes").join(", ")

            furhat.say("The $name is a $type event held in $location. It takes place $frequency. Famous athletes associated with this event include $famousAthletes.")

        } else {
            furhat.say("I couldn't find information on that sports event.")
            goto(Conversation)
        }

        goto(Idle)
    }
}


val FetchPlaceData = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val placesData = loadJSONFromFile("placesData.json")
        val locationsArray = placesData.getJSONObject("places").getJSONArray("locations")

        val userCity = it.text.toLowerCase()
        var foundCity: JSONObject? = null

        for (i in 0 until locationsArray.length()) {
            val city = locationsArray.getJSONObject(i)
            if (userCity in city.getString("city").toLowerCase()) {
                foundCity = city
                break
            }
        }

        if (foundCity != null) {
            val cityName = foundCity.getString("city")
            val placesArray = foundCity.getJSONArray("places")
            val placeDescriptions = mutableListOf<String>()

            for (j in 0 until placesArray.length()) {
                val place = placesArray.getJSONObject(j)
                val placeName = place.getString("name")
                val description = place.getString("description")
                placeDescriptions.add("$placeName: $description")
            }

            furhat.say("In $cityName, you can visit the following places: ${placeDescriptions.joinToString(", ")}.")
        } else {
            furhat.say("I couldn't find information on that city.")
            goto(Conversation)
        }

        goto(Idle)
    }
}



val FetchScienceData = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val scienceData = loadJSONFromFile("scienceData.json")
        val userTopic = it.text.toLowerCase()

        when {
            "field" in userTopic -> {
                furhat.ask("Which field? For example, Physics or Biology.")
                goto(FetchScienceField)
            }
            else -> {
                furhat.say("Please specify fields, discoveries, scientists, or theories in science.")
                reentry()
            }
        }
    }
}

// Example for science fields
val FetchScienceField = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val scienceData = loadJSONFromFile("scienceData.json")
        val fieldsArray = scienceData.getJSONObject("science").getJSONArray("fields")

        val userField = it.text.toLowerCase()
        var foundField: JSONObject? = null

        for (i in 0 until fieldsArray.length()) {
            val field = fieldsArray.getJSONObject(i)
            if (userField in field.getString("name").toLowerCase()) {
                foundField = field
                break
            }
        }

        if (foundField != null) {
            val fieldName = foundField.getString("name")
            val description = foundField.getString("description")
            val subfieldsArray = foundField.getJSONArray("subfields")
            val subfieldsList = mutableListOf<String>()
            for (j in 0 until subfieldsArray.length()) {
                subfieldsList.add(subfieldsArray.getString(j))
            }

            furhat.say("The field of $fieldName involves $description. Some subfields include: ${subfieldsList.joinToString(", ")}.")
        } else {
            furhat.say("I couldn't find information on that field.")
            goto(Conversation)
        }

        goto(Idle)
    }
}

val FetchTechnologyData = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val technologyData = loadJSONFromFile("technologyData.json")
        val trendsArray = technologyData.getJSONObject("technology").getJSONArray("trends")

        val userTech = it.text.toLowerCase()
        var foundTech: JSONObject? = null

        for (i in 0 until trendsArray.length()) {
            val tech = trendsArray.getJSONObject(i)
            if (userTech in tech.getString("name").toLowerCase()) {
                foundTech = tech
                break
            }
        }

        if (foundTech != null) {
            val techName = foundTech.getString("name")
            val applicationsArray = foundTech.getJSONArray("applications").join(", ")
            val industryImpact = foundTech.getJSONObject("industryImpact").toString()

            furhat.say("The technology $techName is applied in areas such as $applicationsArray.")
            furhat.say("It impacts industries in the following ways: $industryImpact.")
        } else {
            furhat.say("I couldn't find information on that technology.")
            goto(Conversation)
        }

        goto(Idle)
    }
}


val FetchCultureData = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val cultureData = loadJSONFromFile("cultureData.json")
        val traditionsArray = cultureData.getJSONObject("culture").getJSONArray("traditions")

        val userTradition = it.text.toLowerCase()
        var foundTradition: JSONObject? = null

        for (i in 0 until traditionsArray.length()) {
            val tradition = traditionsArray.getJSONObject(i)
            if (userTradition in tradition.getString("name").toLowerCase()) {
                foundTradition = tradition
                break
            }
        }

        if (foundTradition != null) {
            val traditionName = foundTradition.getString("name")
            val origin = foundTradition.getString("origin")
            val description = foundTradition.getString("description")
            val practicesArray = foundTradition.getJSONArray("practices").join(", ")
            val significance = foundTradition.getString("significance")

            furhat.say("$traditionName originates from $origin. It is known as $description.")
            furhat.say("Common practices include: $practicesArray.")
            furhat.say("Its significance is: $significance.")
        } else {
            furhat.say("I couldn't find information on that cultural tradition.")
            goto(Conversation)
        }

        goto(Idle)
    }
}





