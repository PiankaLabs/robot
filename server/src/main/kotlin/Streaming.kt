package com.piankalabs

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object Streaming {

    object Writer: OutgoingContent.WriteChannelContent() {
        override val contentType =
            ContentType
                .parse("multipart/x-mixed-replace")
                .withParameter("boundary","stream")

        override suspend fun writeTo(channel: ByteWriteChannel) {
            while (true) {
                pushCurrentFrame(channel)
            }
        }
    }

    private const val clrf = "\r\n"

    suspend fun pushCurrentFrame(channel: ByteWriteChannel) {
        val frame = Camera.currentFrame()
        val bytes = toBytes(frame)
        val headers = headers(bytes)
        val boundary = boundary()

        channel.writeFully(headers)
        channel.writeFully(bytes)
        channel.writeFully(boundary)
    }

    private fun headers(bytes: ByteArray): ByteArray {
        val headers =
            header("Content-Type", "image/jpeg") +
            header("Content-Length", bytes.size) + clrf

        return headers.toByteArray()
    }

    private fun <A>header(key: String, value: A): String {
        return key + ": " + value.toString() + clrf
    }

    private fun boundary(): ByteArray {
        return "$clrf--stream$clrf".toByteArray()
    }

    private fun toBytes(frame: Mat): ByteArray {
        val image = toBufferedImage(frame)
        val stream = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", stream)

        return stream.toByteArray()
    }

    private fun toBufferedImage(frame: Mat): BufferedImage {
        val encoded = MatOfByte()
        Imgcodecs.imencode(".jpg", frame, encoded)
        val bytes = encoded.toArray()
        val stream = ByteArrayInputStream(bytes)

        return ImageIO.read(stream)
    }
}