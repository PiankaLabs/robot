package com.piankalabs

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import io.ktor.websocket.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CallLogging)
    install(WebSockets)

    Camera.start()
    Speaker.start()

    /**
     * APIs are named from the perspective of the client
     * Classes and writers are named from the perspective of the robot
     */
    routing {
        static("/") {
            resources("/web/")
        }
        get("/video") {
            call.respond(Streaming.VideoWriter)
        }
        get("/audio") {
            call.respond(Streaming.AudioWriter)
        }
        get("/audio/waveform") {
            call.respond(Streaming.MicrophoneWaveformWriter)
        }
        webSocket("/microphone") {
            for (frame in incoming) {
                frame as? Frame.Binary ?: continue
                Speaker.play(frame.data)
            }
        }
        get("/microphone/waveform") {
            call.respond(Streaming.SpeakerWaveformWriter)
        }
        get("/robot/{x}/{y}") {
            val x = call.parameters["x"].toString().toInt() * 2 // -100 to 100
            val y = call.parameters["y"].toString().toInt() * 2 // -100 to 100
            Robot.direction(x, y)
            call.respondText("ok", ContentType.Text.Html)
        }
    }
}

