package com.kadenkin.mememachine.services.imagereader

import com.kadenkin.mememachine.suppliers.TessApiFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.bytedeco.javacpp.lept
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ImageReader
@Autowired constructor(private val tessApiFactory: TessApiFactory) {
    private val log = LoggerFactory.getLogger(ImageReader::class.java)

    fun getText(image: lept.PIX): String {
        log.info("Started reading image")
        val api = tessApiFactory.getTessApi()

        api.SetImage(image)

        var ans: String? = null
        runBlocking {
            val deferred =  async { api.GetUTF8Text().string }
            try {
                ans = withTimeout(5000) { deferred.await() } ?: "timed out"
            } catch (e: Exception) {
            }

        }
        log.info("Finished reading image")

        return ans ?: "could not find text"
    }
}