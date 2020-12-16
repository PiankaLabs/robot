#include "motors.h"
#include <Arduino.h>

void apply_motor_state(State* state, int low_pin, int high_pin, int speed_pin) {
  int direction = state->direction;
  int speed = state->speed;

  if (direction == Forward) {
    digitalWrite(low_pin,  LOW);
    digitalWrite(high_pin, HIGH);
  } else {
    digitalWrite(low_pin,  HIGH);
    digitalWrite(high_pin, LOW);
  }

  analogWrite(speed_pin, speed);
}

void apply_state() {
  apply_motor_state(&LeftMotor,  LeftMotorPin1, LeftMotorPin2, LeftSpeedPin);
  apply_motor_state(&RightMotor, RightMotorPin1, RightMotorPin2, RightSpeedPin);
}
