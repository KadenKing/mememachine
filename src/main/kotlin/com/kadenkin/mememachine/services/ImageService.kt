package com.kadenkin.mememachine.services

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.kadenkin.mememachine.repositories.elasticsearch.AnalyzedImagesRepository
import com.kadenkin.mememachine.repositories.MongoAnalyzedImagesRepository
import com.kadenkin.mememachine.repositories.MongoIndexableLinkRepository
import com.kadenkin.mememachine.services.imagereader.ImageReader
import com.kadenkin.mememachine.services.reddit.SourceInformation
import com.kadenkin.mememachine.services.reddit.RedditService
import org.bytedeco.javacpp.lept
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Document(indexName = "analyzed-images", type = "analyzed-image")
data class AnalyzedImage(@JsonProperty("id") var id: String? = null,
                         @JsonProperty("title") val title: String,
                         @JsonProperty("imgUrl") val imgUrl: String,
                         @JsonProperty("imageText") val imageText: String,
                         @JsonFormat (shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
                         @JsonProperty("date") val date: Date? = Date())
@EnableScheduling
@Service
class ImageService
@Autowired
constructor(private val imageDownloader: ImageDownloader,
            private val redditService: RedditService,
            private val imageReader: ImageReader,
            private val analyzedImagesRepository: AnalyzedImagesRepository,
            private val mongoAnalyzedImagesRepository: MongoAnalyzedImagesRepository,
            private val mongoIndexableLinkRepository: MongoIndexableLinkRepository) {
    private val log = LoggerFactory.getLogger(ImageService::class.java)

    @Scheduled(cron = "0 */2 * * * *")
    fun readTextFromRedditImages() {
        val redditPosts = mongoIndexableLinkRepository.findAll().map { it.url.addTopOneHundred() }.flatMap { redditService.getRedditPosts(it) }
        val analyzedImages = mutableListOf<AnalyzedImage>()

        redditPosts.forEach {
            if (!mongoAnalyzedImagesRepository.findByImgUrl(it.imageUrl).isPresent) {
                val image = it.getAnalyzedImage()
                image?.run{
                    mongoAnalyzedImagesRepository.save(image)
                    analyzedImagesRepository.save(image)
                    analyzedImages.add(image)
                }
            } else {
                log.info("skipping ${it.imageUrl}, already indexed")
            }
        }
    }

    private fun String.addTopOneHundred(): String {
        return "$this.json?limit=1000"
    }

    private fun SourceInformation.getAnalyzedImage(): AnalyzedImage? {
        val image = this.downloadImage()
        checkNotNull(image) {
            log.error("can't use gifs")
            return null
        }

        val text = image.getImageText()

        return AnalyzedImage(id = null, title = this.title, imgUrl = this.imageUrl, imageText = text)
    }

    private fun SourceInformation.downloadImage(): lept.PIX? {
        return imageDownloader.getImage(this.imageUrl)
    }

    private fun lept.PIX.getImageText(): String {
        return imageReader.getText(this)
    }
}