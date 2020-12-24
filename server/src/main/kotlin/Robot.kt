package com.piankalabs

import com.fazecast.jSerialComm.SerialPort
import java.io.PrintWriter
import kotlin.math.abs
import kotlin.math.round

object Robot {

    private val port = SerialPort.getCommPorts().filter { port ->
        val name = port.systemPortName
        name.contains("ttyACM") || name.contains("tty.usbmodem")
    }.map { port ->
        port.setComPortParameters(9600, 8, 1, 0)
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        port.openPort()

        Thread.sleep(2000)

        port
    }.first()

    private val writer: PrintWriter by lazy {
        PrintWriter(port.outputStream, true)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    fun direction(x: Int, y: Int) {
        /*
            Calculate R+L (Call it V): V =(100-ABS(X)) * (Y/100) + Y
            Calculate R-L (Call it W): W= (100-ABS(Y)) * (X/100) + X
            Calculate R: R = (V+W) /2
            Calculate L: L= (V-W)/2
         */

        if (x == 0 && y == 0) {
            writer.println("lf0")
            writer.println("rf0")
            return
        }

        val nx = x * -1.0

        val v = (100.0 - abs(nx)) * (y / 100.0) + y
        val w = (100.0 - abs(y)) * (nx / 100.0) + nx
        val l = ((v - w) / 2.0)
        val r = ((v + w) / 2.0)

        val ld = if (l >= 0.0) "f" else "b"
        val rd = if (l >= 0.0) "f" else "b"

        val ls = ((abs(l / 100.0) * .6) + .4).round(1)
        val rs = ((abs(r / 100.0) * .6) + .4).round(1)

        writer.println("l$ld$ls")
        writer.println("r$rd$rs")
    }
}