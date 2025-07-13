package com.example.demo

import org.springframework.ai.chat.client.ChatClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class DemoApplication {
	@Bean
	fun chatClient(builder: ChatClient.Builder): ChatClient {
		return builder.build()
	}

//	@Bean
//	fun searchIndexCreator(template: MongoTemplate) = CommandLineRunner {
//		val index = VectorIndex("vector_index")
//			.addVector("embeddings") { it.dimensions(1536).similarity(VectorIndex.SimilarityFunction.COSINE) };
//		template.searchIndexOps(Movie::class.java)
//			.createIndex(index)
//	}
}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
