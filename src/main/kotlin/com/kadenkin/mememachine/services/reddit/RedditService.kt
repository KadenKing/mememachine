package com.kadenkin.mememachine.services.reddit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RedditService @Autowired constructor(private val redditApi: RedditApi,
                                           private val redditParser: RedditParser) {
    fun getRedditPosts(url: String): List<SourceInformation> {
        val json = redditApi.getJsonString(url)

        return redditParser.getRedditPosts(json)
    }
}