package com.kadenkin.mememachine.sources.reddit

import com.kadenkin.mememachine.repositories.MongoAnalyzedImagesRepository
import com.kadenkin.mememachine.repositories.MongoIndexableLinkRepository
import com.kadenkin.mememachine.sources.SourcePost
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.*

abstract class SourceAPI {
    abstract fun getAllPosts(): List<SourcePost>
}

@Component
class RedditApi @Autowired constructor(private val indexableLinkRepository: MongoIndexableLinkRepository,
                                       private val redditParser: RedditParser,
                                       private val mongoAnalyzedImagesRepository: MongoAnalyzedImagesRepository) : SourceAPI() {
    override fun getAllPosts(): List<SourcePost> {
        val subreddits = indexableLinkRepository.findAll()

        val jsonStrings = subreddits.map { getJsonString("${it.url}.json?limit=1000") }

        val parsedPosts = jsonStrings.flatMap { redditParser.getRedditPosts(it) }

        return parsedPosts.filter { !mongoAnalyzedImagesRepository.findByImgUrl(it.imgUrl).isPresent }
    }

    fun getJsonString(redditUrl: String): String {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.accept = Arrays.asList(MediaType.APPLICATION_JSON)
        headers.set("User-Agent", """NING/1.0""")
        val entity = HttpEntity<String>(headers)

        val ans: ResponseEntity<String> = restTemplate.exchange(redditUrl,
                HttpMethod.GET,
                entity)

        val json = ans.body.toString()

        return json
    }
}