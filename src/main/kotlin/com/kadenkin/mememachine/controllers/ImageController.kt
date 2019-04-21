package com.kadenkin.mememachine.controllers;

import com.fasterxml.jackson.annotation.JsonProperty
import com.kadenkin.mememachine.repositories.AnalyzedImagesRepository
import com.kadenkin.mememachine.services.ImageService
import com.kadenkin.mememachine.services.reddit.RedditService
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*

@Repository
interface TestDocumentRepository : ElasticsearchRepository<TestDocument, String>

@Document(indexName = "test-index", type = "testy")
data class TestDocument(@JsonProperty("id") var id: String?, @JsonProperty("test") var test: String)

@RestController
@RequestMapping("api/images")
class ImageController @Autowired constructor(val imageService: ImageService,
                                             val redditService: RedditService,
                                             val analyzedImagesRepository: AnalyzedImagesRepository,
                                             val testDocumentRepository: TestDocumentRepository) {

    @GetMapping
    fun search(@RequestParam search: String): ResponseEntity<Any> {
        val imagesFound = analyzedImagesRepository.search(QueryBuilders.multiMatchQuery(search)).toList().take(5)

        return ResponseEntity(imagesFound, HttpStatus.OK)
    }

    @PostMapping("/test")
    fun saveTest(@RequestBody testDocument: TestDocument): String {
        testDocumentRepository.save(testDocument)

        return "cool"
    }

    @GetMapping("/test")
    fun getTest(): TestDocument {
        return testDocumentRepository.findById("123").get()
    }
}
