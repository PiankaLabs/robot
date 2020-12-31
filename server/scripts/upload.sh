#!/bin/sh

gradle shadowJar
scp build/libs/server-1.0-SNAPSHOT-all.jar pi@piankabot.lan:/home/pi/server

date