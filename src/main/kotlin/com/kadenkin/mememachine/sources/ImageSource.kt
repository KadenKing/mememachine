package com.kadenkin.mememachine.sources

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.kadenkin.mememachine.repositories.MongoAnalyzedImagesRepository
import com.kadenkin.mememachine.repositories.elasticsearch.AnalyzedImagesRepository
import com.kadenkin.mememachine.sources.imagereader.ImageReader
import com.kadenkin.mememachine.sources.reddit.RedditApi
import com.kadenkin.mememachine.sources.reddit.SourceAPI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import java.util.*

data class SourcePost(@JsonProperty("sourceName") val sourceName: String,
                      @JsonProperty("title") val title: String,
                      @JsonProperty("imgUrl") val imgUrl: String)

@Document(indexName = "analyzed-images", type = "analyzed-image")
data class AnalyzedImage(@JsonProperty("sourceName") val sourceName: String,
                         @JsonProperty("title") val title: String,
                         @JsonProperty("imgUrl") val imgUrl: String,
                         @JsonProperty("id") var id: String? = null,
                         @JsonProperty("imageText") val imageText: String,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
                            @JsonProperty("date") val date: Date? = Date())

@Component
class RedditMongoAndElasticRepository @Autowired constructor(override val elasticsearchRepository: AnalyzedImagesRepository,
                                                             override val mongoRepository: MongoAnalyzedImagesRepository) : MongoAndElasticRepository()
@Component
class RedditImageSource @Autowired constructor(override val sourceApi: RedditApi,
                                               override val imageDownloader: ImageDownloader,
                                               override val mongoAndElasticRepository: RedditMongoAndElasticRepository,
                                               override val imageReader: ImageReader) : ImageSource()

abstract class ImageSource {
    protected abstract val sourceApi: SourceAPI
    protected abstract val imageDownloader: ImageDownloader
    protected abstract val imageReader: ImageReader
    protected abstract val mongoAndElasticRepository: MongoAndElasticRepository

    fun indexImages() {
        val allPostInformation = sourceApi.getAllPosts()

        allPostInformation.forEach { processPostInformation(it) }
    }

    private fun processPostInformation(sourcePost: SourcePost) {
        val image = imageDownloader.getImage(sourcePost.imgUrl)
        image ?: return

        val text = imageReader.readTextAndFreeImage(image)

        val analyzedImage = AnalyzedImage(sourceName = sourcePost.sourceName,
                title = sourcePost.title,
                imgUrl = sourcePost.imgUrl,
                imageText = text)

        mongoAndElasticRepository.saveAnalyzedImage(analyzedImage)
    }
}

abstract class MongoAndElasticRepository {
    protected abstract val elasticsearchRepository: ElasticsearchRepository<AnalyzedImage, String>
    protected abstract val mongoRepository: MongoRepository<AnalyzedImage, String>

    fun saveAnalyzedImage(analyzedImage: AnalyzedImage) {
        elasticsearchRepository.save(analyzedImage)
        mongoRepository.save(analyzedImage)
    }
}