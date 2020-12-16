#include "state.h"

State LeftMotor = {
  .direction = Forward,
  .speed = 0
};

State RightMotor = {
  .direction = Forward,
  .speed = 0
};

void update_state(Command command) {
  State* motor;

  // choose motor
  switch (command.motor) {
    case Left:
      motor = &LeftMotor;
      break;
    case Right:
      motor = &RightMotor;
      break;
  }

  // update direction
  motor->direction = command.direction;
  motor->speed = (int)(command.speed * 255);
}
