package com.piankalabs

import org.slf4j.LoggerFactory
import javax.sound.sampled.*
import kotlin.math.log10

object Speaker {

    private val logger = LoggerFactory.getLogger("speaker")

    private val waveform = Waveform()

    private fun audioFormat(): AudioFormat {
        val sampleRate = 16000.0f
        val sampleSizeInBits = 16
        val channels = 1
        val signed = true
        val bigEndian = false

        return AudioFormat(
            sampleRate,
            sampleSizeInBits,
            channels,
            signed,
            bigEndian
        )
    }

    private val line: SourceDataLine by lazy {
        logger.info(audioFormat().toString())

        val info = DataLine.Info(SourceDataLine::class.java, audioFormat())
        val sourceLine = AudioSystem.getLine(info) as SourceDataLine
        sourceLine.open()
        sourceLine.start()

        // half volume
        //http://www.playdotsound.com/portfolio-item/decibel-db-to-float-value-calculator-making-sense-of-linear-values-in-audio-tools/
        val volume = sourceLine.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        volume.value = 20f * log10(0.2).toFloat()

        sourceLine
    }

    fun start() {
        line // initialize
        waveform.start()
    }

    fun play(blob: ByteArray) {
        try {
            line.write(blob, 0, blob.size)
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to play blob", e)
        }

        waveform.sample(blob)
    }

    fun waveform(): Waveform {
        return waveform
    }
}