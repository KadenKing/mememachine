package com.kadenkin.mememachine.repositories.elasticsearch

import com.kadenkin.mememachine.sources.AnalyzedImage
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyzedImagesRepository : ElasticsearchRepository<AnalyzedImage, String>