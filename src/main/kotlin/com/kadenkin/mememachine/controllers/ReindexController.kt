package com.kadenkin.mememachine.controllers

import com.kadenkin.mememachine.services.ReindexService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/reindex")
class ReindexController @Autowired constructor(private val reindexService: ReindexService) {
    @PostMapping
    fun doReindex(): ResponseEntity<String> {
        reindexService.reindex()
        return ResponseEntity("reindex done", HttpStatus.OK)
    }
}