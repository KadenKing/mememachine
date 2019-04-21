package com.kadenkin.mememachine.services

import com.kadenkin.mememachine.repositories.MongoAnalyzedImagesRepository
import com.kadenkin.mememachine.repositories.elasticsearch.AnalyzedImagesRepository
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.client.Client
import org.elasticsearch.client.ElasticsearchClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReindexService @Autowired constructor(private val mongoAnalyzedImagesRepository: MongoAnalyzedImagesRepository,
                                            private val analyzedImagesRepository: AnalyzedImagesRepository,
                                            private val client: Client) {
    fun reindex() {
        client.delete(DeleteRequest("analyzed-images"))
        val mongoImages = mongoAnalyzedImagesRepository.findAll()
        val checkedMongoImages = mongoImages.map {
            when(it.date) {
                null -> it.copy()
                else -> it
            }
        }

        analyzedImagesRepository.saveAll(checkedMongoImages)
    }
}