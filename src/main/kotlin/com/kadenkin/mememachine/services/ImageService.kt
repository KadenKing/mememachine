package com.kadenkin.mememachine.services

import com.kadenkin.mememachine.sources.ImageSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@EnableScheduling
@Service
class ImageService @Autowired constructor(private val sources: List<ImageSource>) {
    @Scheduled(cron = "0 */2 * * * *")
    fun saveFromSources() {
        sources.forEach { it.indexImages() }
    }
}