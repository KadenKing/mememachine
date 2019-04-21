package com.kadenkin.mememachine.suppliers

import org.bytedeco.javacpp.tesseract
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TessApiFactory {
    private val log = LoggerFactory.getLogger(TessApiFactory::class.java)

    fun getTessApi(): tesseract.TessBaseAPI {
        log.info("Supplying new instance of api")
        val api = tesseract.TessBaseAPICreate()
        api.Init("/Users/kadenking/Documents/ocr/", "eng")

        return api
    }
}