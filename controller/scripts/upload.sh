#!/bin/sh

scp *.ino *.h *.cpp pi@piankabot.lan:/home/pi/controller
ssh pi@piankabot.lan '/home/pi/bin/arduino-cli compile -b arduino:avr:mega /home/pi/controller'
ssh pi@piankabot.lan '/home/pi/bin/arduino-cli upload -b arduino:avr:mega -p /dev/ttyACM0 /home/pi/controller'
