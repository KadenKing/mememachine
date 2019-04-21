package com.kadenkin.mememachine.services

import com.fasterxml.jackson.annotation.JsonProperty
import com.kadenkin.mememachine.repositories.AnalyzedImagesRepository
import com.kadenkin.mememachine.services.imagereader.ImageReader
import com.kadenkin.mememachine.services.reddit.SourceInformation
import com.kadenkin.mememachine.services.reddit.RedditService
import org.bytedeco.javacpp.lept
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.stereotype.Service

@Document(indexName = "analyzed-images", type = "analyzed-image")
data class AnalyzedImage(@JsonProperty("_id") var id: String?,
                         @JsonProperty("title") val title: String,
                         @JsonProperty("imgUrl") val imgUrl: String,
                         @JsonProperty("imageText") val imageText: String)

@Service
class ImageService
@Autowired
constructor(private val imageDownloader: ImageDownloader,
            private val redditService: RedditService,
            private val imageReader: ImageReader,
            private val analyzedImagesRepository: AnalyzedImagesRepository) {
    private val log = LoggerFactory.getLogger(ImageService::class.java)


    fun readTextFromRedditImages(): List<AnalyzedImage>   {
        val redditPosts = redditService.getRedditPosts()

        val analyzedImages = redditPosts
                .mapNotNull { it.getAnalyzedImage() }

        analyzedImagesRepository.saveAll(analyzedImages)

        return analyzedImages
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