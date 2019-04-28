package com.kadenkin.mememachine.sources.reddit

import com.google.gson.JsonParser
import com.kadenkin.mememachine.sources.SourcePost
import org.springframework.stereotype.Component


@Component
class RedditParser {

    fun getRedditPosts(jsonString: String): List<SourcePost> {
        val jsonParser = JsonParser()

        val children = jsonParser.parse(jsonString).asJsonObject
                .getAsJsonObject("data")
                .getAsJsonArray("children")

        val sourceSources: List<SourcePost> = children
                .mapNotNull{
                    it.asJsonObject
                            .getAsJsonObject("data")
                            .takeIf {
                                it.has("post_hint") && it.getAsJsonPrimitive("post_hint").asString == "image"
                            }?.let{
                                SourcePost(
                                        sourceName = "Reddit",
                                        title = it.getAsJsonPrimitive("title").asString,
                                        imgUrl = it.getAsJsonPrimitive("url").asString
                                )
                            }
                }

        return sourceSources
    }
}