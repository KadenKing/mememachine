package com.kadenkin.mememachine.services.reddit

import com.google.gson.JsonParser
import org.springframework.stereotype.Component

data class SourceInformation(val sourceName: String, val title: String, val imageUrl: String)

@Component
class RedditParser {

    fun getRedditPosts(jsonString: String): List<SourceInformation> {
        val jsonParser = JsonParser()

        val children = jsonParser.parse(jsonString).asJsonObject
                .getAsJsonObject("data")
                .getAsJsonArray("children")

        val sourceInformations: List<SourceInformation> = children
                .mapNotNull{
                    it.asJsonObject
                            .getAsJsonObject("data")
                            .takeIf {
                                it.has("post_hint") && it.getAsJsonPrimitive("post_hint").asString == "image"
                            }?.let{
                                SourceInformation(
                                        sourceName = "Reddit",
                                        title = it.getAsJsonPrimitive("title").asString,
                                        imageUrl = it.getAsJsonPrimitive("url").asString
                                )
                            }
                }

        return sourceInformations
    }
}