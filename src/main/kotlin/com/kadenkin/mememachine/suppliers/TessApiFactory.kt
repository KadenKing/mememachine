package com.kadenkin.mememachine.suppliers

import org.bytedeco.javacpp.tesseract
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.core.io.ClassPathResource



@Component
class TessApiFactory {
    private val log = LoggerFactory.getLogger(TessApiFactory::class.java)

    fun getTessApi(): tesseract.TessBaseAPI {
        log.info("Supplying new instance of api")
        val api = tesseract.TessBaseAPICreate()

        val trainedData = ClassPathResource("static/eng.traineddata").file
        api.Init(trainedData.absolutePath.fixTrainedDataPath(), "eng")

        return api
    }

    private fun String.fixTrainedDataPath(): String {
        val split = this.split("/").toMutableList()
        split.removeAt(split.size - 1)
        return split.joinToString("/")
    }
}