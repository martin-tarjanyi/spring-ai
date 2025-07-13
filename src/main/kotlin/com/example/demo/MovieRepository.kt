package com.example.demo

import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface MovieRepository : MongoRepository<Movie, ObjectId> {
    fun findByVectorized(vectorized: Boolean?, pageable: Pageable): List<Movie>
}
