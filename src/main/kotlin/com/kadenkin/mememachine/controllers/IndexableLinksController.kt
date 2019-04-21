package com.kadenkin.mememachine.controllers

import com.kadenkin.mememachine.repositories.IndexableLink
import com.kadenkin.mememachine.repositories.MongoIndexableLinkRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/sources")
class IndexableLinksController @Autowired constructor(private val indexableLinkRepository: MongoIndexableLinkRepository) {
    @PostMapping
    fun addSource(@RequestBody indexableLink: IndexableLink): ResponseEntity<String> {
        indexableLinkRepository.save(indexableLink)
        return ResponseEntity("saved", HttpStatus.OK)
    }
}