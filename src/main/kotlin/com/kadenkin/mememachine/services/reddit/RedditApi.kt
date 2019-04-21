package com.kadenkin.mememachine.services.reddit

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.*

@Component
class RedditApi {
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