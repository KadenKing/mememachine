package com.kadenkin.mememachine.repositories

import com.kadenkin.mememachine.services.AnalyzedImage
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyzedImagesRepository : ElasticsearchRepository<AnalyzedImage, String>