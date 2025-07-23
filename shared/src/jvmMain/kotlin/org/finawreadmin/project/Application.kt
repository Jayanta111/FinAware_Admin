package org.finawreadmin.project

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.plugins.callloging.*
import kotlinx.serialization.json.Json
import org.finawreadmin.project.Database.ContentRepository
import org.finawreadmin.project.model.LearningEntry

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(CallLogging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        routing {
            val repository = ContentRepository()

            post("/saveContent") {
                val entry = call.receive<LearningEntry>()
                val result = repository.saveContent(entry.courseId, entry)
                if (result.isSuccess) {
                    call.respond(mapOf("status" to "success"))
                } else {
                    call.respond(mapOf("status" to "error", "message" to result.exceptionOrNull()?.message))
                }
            }
        }
    }.start(wait = true)
}
