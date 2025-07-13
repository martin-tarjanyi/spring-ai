package com.example.demo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "movies")
data class Movie(
    @Id
    val id: ObjectId,
    val plot: String = "",
    val genres: List<String> = emptyList(),
    val runtime: Int?,
    val cast: List<String> = emptyList(),
    val title: String,
    val languages: List<String> = emptyList(),
    val directors: List<String> = emptyList(),
    val writers: List<String> = emptyList(),
    val awards: Awards,
    val year: String?,
//    val imdb: Imdb,
    val countries: List<String> = emptyList(),
//    val type: String,
    val vectorized: Boolean = false,
//    val tomatoes: Tomatoes
    val embeddings: List<Float> = emptyList(),
)

data class Awards(
    val wins: Int,
    val nominations: Int,
    val text: String
)

data class Imdb(
    val rating: Double,
    val votes: Int,
    val id: Int
)

data class Tomatoes(
    val viewer: Viewer,
    val dvd: Any,
    val website: String,
    val production: String,
    val lastUpdated: Any
)

data class Viewer(
    val rating: Double,
    val numReviews: Int,
    val meter: Int
)
