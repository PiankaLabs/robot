package com.piankalabs

import java.util.*
import nu.pattern.OpenCV
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture

object Camera {

    init {
        OpenCV.loadShared()
    }

    private val timer = Timer()
    private val video = VideoCapture()

    private var frame = Mat()

    fun start() {
        video.open(0)

        timer.schedule(object : TimerTask() {
            override fun run() {
                if (!video.read(frame)) {
                    timer.cancel()
                }
            }
        }, 1, 1)
    }

    fun currentFrame(): Mat {
        return frame
    }
}