package com.kadenkin.mememachine.services

import org.bytedeco.javacpp.lept
import org.bytedeco.javacpp.lept.pixRead
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Service
class ImageDownloader {
    private val log = LoggerFactory.getLogger(ImageDownloader::class.java)

    fun getImage(url: String): lept.PIX? {
        val downloadedImageDestination: Path = Files.createTempFile("image", ".${url.getExtension()}")

        val urlConnection = URL(url)
                .openConnection()
                .also {
                    it.setRequestProperty("User-Agent", """NING/1.0""")
                }

        log.info("Downloading image from $url")
        urlConnection.getInputStream()
                .use { input -> Files.copy(input, downloadedImageDestination, StandardCopyOption.REPLACE_EXISTING) }
        log.info("finished downloading image")

        val tempFile = downloadedImageDestination.toFile()
        val image: lept.PIX? = pixRead(downloadedImageDestination.toFile().absolutePath)

        tempFile.delete()

        return image
    }

    private fun String.getExtension(): String {
        val splitArr = this.split(".")

        return splitArr[splitArr.size -1]
    }
}