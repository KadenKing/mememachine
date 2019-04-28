package com.kadenkin.mememachine.repositories

import com.fasterxml.jackson.annotation.JsonProperty
import com.kadenkin.mememachine.sources.AnalyzedImage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

data class IndexableLink(@JsonProperty("id") val id: String? = null,
                         @JsonProperty("url") val url: String,
                         @JsonProperty("source") val source: String)

@Repository
interface MongoIndexableLinkRepository : MongoRepository<IndexableLink, String>

@Repository
interface MongoAnalyzedImagesRepository : MongoRepository<AnalyzedImage, String> {
    fun findByImgUrl(imageUrl: String): Optional<AnalyzedImage>
}

