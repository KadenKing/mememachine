package com.kadenkin.mememachine.services.imagereader

import com.kadenkin.mememachine.suppliers.TessApiFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.bytedeco.javacpp.lept
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.lept.pixDestroy


@Component
class ImageReader
@Autowired constructor(private val tessApiFactory: TessApiFactory) {
    private val log = LoggerFactory.getLogger(ImageReader::class.java)

    fun getText(image: lept.PIX): String {
        log.info("Started reading image")
        val api = tessApiFactory.getTessApi()

        api.SetImage(image)

        lateinit var outText: BytePointer

        runBlocking {
            try {
                withTimeout(5000) { outText = api.GetUTF8Text() }
            } catch (e: Exception) {
                api.End()
                pixDestroy(image)
                return@runBlocking "could not find text"
            }
        }

        log.info("Finished reading image")
        val ans = outText.string
        outText.deallocate()
        api.End()
        pixDestroy(image)

        return ans ?: "could not find text"
    }
}