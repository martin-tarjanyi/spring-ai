package com.example.demo

import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/movies")
class MovieController(
    private val chatClient: ChatClient,
    private val embedding: EmbeddingModel,
    private val movieRepository: MovieRepository,
    private val vectorStore: VectorStore,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/{id}")
    fun getMovie(@PathVariable id: String): Movie? {
        return movieRepository.findById(ObjectId(id)).getOrNull()
    }

    @PostMapping("/{id}/embed")
    fun embedMovie(@PathVariable id: String) {
        movieRepository.findById(ObjectId(id)).getOrNull()
            ?.takeIf { it.vectorized.not() }
            ?.let { movie -> embed(movie) }
    }

    @PostMapping("/bulk-embed")
    fun embedMovies(@RequestParam(required = false, defaultValue = "100") count: Int) {
        val movies = movieRepository.findByVectorized(vectorized = null, Pageable.ofSize(count))
        movies.forEach { movie -> embed(movie) }
    }

    @GetMapping("/search")
    fun searchMovies(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Any {
        val documents = vectorStore.similaritySearch(SearchRequest.builder()
            .query(query)
            .topK(limit)
            .build())
            .orEmpty()
        val ids = documents.mapNotNull { it.metadata["id"] as? String }
        val moviesById = movieRepository.findAllById(ids.map { ObjectId(it) }).associateBy { it.id.toHexString() }
        return documents.associateWith { moviesById[it.metadata["id"]] }.map {
            mapOf(
                "score" to it.key.score,
                "movie" to it.value
            )
        }
    }

    @GetMapping("/chat")
    fun chatMovies(
        @RequestParam query: String
    ): String? {
        return chatClient.prompt("You are a movie expert, recommend movies based on the context and the user's request. " +
                "If the context doesn't contain any matching movies, simply answer that you have no knowledge about this topic. " +
                "Only list the titles of the movies, one per line.")
            .user(query)
            .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().topK(10).build())
                .build())
            .call()
            .content()
    }

    private fun embed(movie: Movie) {
        val content = vectorContent(movie)
        vectorStore.add(listOf(Document(content, mapOf("id" to movie.id.toHexString()))))
        movieRepository.save(movie.copy(vectorized = true))
        logger.info("Movie \"${movie.title}\" - ${movie.id} embedded successfully.")
    }

    private fun vectorContent(movie: Movie): String = """
                        Title: ${movie.title}
                        Plot: ${movie.plot}
                        Genres: ${movie.genres.joinToString(", ")}
                        Runtime: ${movie.runtime ?: "unknown"} minutes
                        Cast: ${movie.cast.joinToString(", ")}
                        Release Year: ${movie.year?.filter { it.isDigit() }?.takeIf { it.isNotEmpty() } ?: "unknown"}
                    """.trimIndent()
}
