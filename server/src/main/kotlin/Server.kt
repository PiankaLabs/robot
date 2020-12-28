package com.piankalabs

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    Camera.start()
    embeddedServer(Netty, 8080) {
        routing {
            static("/") {
                resources("/")
            }
            get("/video") {
                call.respond(Streaming.VideoWriter)
            }
            get("/audio") {
                call.respond(Streaming.AudioWriter)
            }
            get("/robot/{x}/{y}") {
                val x = call.parameters["x"].toString().toInt() * 2 // -100 to 100
                val y = call.parameters["y"].toString().toInt() * 2 // -100 to 100
                Robot.direction(x, y)
                call.respondText("ok", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}

