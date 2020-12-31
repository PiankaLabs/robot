package com.piankalabs

import com.google.common.collect.EvictingQueue
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.*

class Waveform {

    private val width = 512;
    private val height = 128;

    private val halfHeight = height / 2

    private val samplesPerPixel = 4
    private val sampleWindowSize = samplesPerPixel * width
    private val sampleWindow = EvictingQueue.create<Short>(sampleWindowSize)

    private val timer = Timer()
    private var image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    fun start() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (sampleWindow.remainingCapacity() == 0) {
                    image = paint()
                }
            }
        }, 1, 100)
    }

    fun sample(bytes: ByteArray) {
        //https://stackoverflow.com/questions/50024875/how-to-convert-a-bytearray-to-a-shortarray-in-kotlin
        val samples = bytes
            .asList()
            .chunked(2)
            .map { (l, h) -> (l.toInt() + h shl 8).toShort() }
            .toShortArray()

        sample(samples)
    }

    fun image(): BufferedImage {
        return image
    }

    private fun sample(samples: ShortArray) {
        sampleWindow.addAll(samples.toCollection(LinkedList<Short>()))
    }

    private fun paint(): BufferedImage {
        val summaries = summaries()
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        g2d.paint = Color.white
        g2d.composite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.5f
        )

        for ((index, summary) in summaries.withIndex()) {
            val min = abs(summary.min * (halfHeight / 32768.0)).toInt()
            val max = abs(summary.max * (halfHeight / 32768.0)).toInt()

            val x = index
            val y = halfHeight - max
            val width = 1
            val height = min + max

            g2d.fillRect(x, y, width, height)
        }

        g2d.dispose()

        return image
    }

    data class Summary(val min: Short, val max: Short)

    private fun summaries(): List<Summary> {
        //TODO: doubt this is efficient
        val chunks =
            sampleWindow
                .toList()
                .chunked(samplesPerPixel)

        return chunks.filterNotNull().map { summarize(it) } // same NPE issue as below
    }

    private fun summarize(samples: List<Short>): Summary {
        val pairs =
            samples
                .filterNotNull() // getting weird NPEs - probably a Guava queue being in beta issue
                .chunked(2)
                .map {
                    val first = it[0]
                    val second = it[1]

                    Summary(minOf(first, second), maxOf(first, second))
                }

        val min = pairs.fold(Short.MAX_VALUE) { acc, sample -> minOf(acc, sample.min) }
        val max = pairs.fold(Short.MIN_VALUE) { acc, sample -> maxOf(acc, sample.max) }

        return Summary(min, max)
    }
}