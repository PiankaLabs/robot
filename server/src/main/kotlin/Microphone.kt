package com.piankalabs

import java.io.OutputStream
import javax.sound.sampled.*

object Microphone {

    private val waveform = Waveform()

    private fun audioFormat(): AudioFormat {
        val sampleRate = 16000.0f
        val sampleSizeInBits = 16
        val channels = 2
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

    fun start(stream: OutputStream) {
        val format: AudioFormat = audioFormat()
        val info = DataLine.Info(TargetDataLine::class.java, format)

        if (!AudioSystem.isLineSupported(info)) {
            println("Line not supported")
            return
        }

        val line = AudioSystem.getLine(info) as TargetDataLine
        line.open(format)
        line.start()

        waveform.start()

        while (true) {
            val data = ByteArray(line.bufferSize / 5)
            val size = line.read(data, 0, data.size)
            stream.write(data, 0, size)
            waveform.sample(data)
        }
    }

    fun waveform(): Waveform {
        return waveform
    }
}