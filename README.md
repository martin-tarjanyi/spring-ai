# Intro

Spring application demonstrating capabilities of Spring AI, using OpenAI and Mongo Atlas vector search.

# Usage

* Start Docker Compose
* Populate `AI.movies` collection in Mongo using [sample data](https://github.com/neelabalan/mongodb-sample-dataset/blob/main/sample_mflix/movies.json) 
* Set your OpenAI API key using `SPRING_AI_OPENAI_API_KEY` env variable
* Start the application:
    ```shell
    .\gradlew bootRun
    ```
* Visit [Swagger UI](http://localhost:8080/swagger-ui/index.html)
  * `POST /movies/bulk-embed` - create embeddings for the specified number of movies using OpenAI
  * `GET /movies/search`, `GET /movies/chat` - retrieve your movies using LLM and RAG
