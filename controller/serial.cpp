#include "serial.h"
#include "constants.h"
#include <HardwareSerial.h>

/* format: [motor][direction][speed]
     motor = [l, r]
     direction = [f, b]
     speed = [0.0, 1.0] */
Command parse_command() {
  Command command;
  String message = Serial.readStringUntil('\n');

  //TODO: generalize out commands (or to/from communications)
  if (message == "hello") {
    command.hello = true;
    return command;
  }

  command.hello = false;

  // parse structure
  char motor = message.charAt(0);
  char direction = message.charAt(1);
  String speed = message.substring(2, 5);

  // interpret motor
  switch (motor) {
    case 'l':
      command.motor = Left;
      break;
    case 'r':
      command.motor = Right;
      break;
  }
  
  // interpret direction
  switch (direction) {
    case 'f':
      command.direction = Forward;
      break;
    case 'b':
      command.direction = Backward;
      break;
  }

  // set speed
  command.speed = speed.toFloat();

  return command;
}
