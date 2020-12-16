#include "constants.h"
#include "state.h"
#include "initialize.h"
#include "commands.h"
#include "serial.h"

Command parse_command();
void update_state();
void apply_state();

void setup() {
  initialize();
}

void loop() {
  if (Serial.available() > 0) {
    Command command = parse_command();
    update_state(command);
  }

  apply_state();

  /*
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0) {
    String message = Serial.readStringUntil('\n');
    Serial.print(message + "\n");
  }

  //Controlling speed (0 = off and 255 = max speed):
  analogWrite(2, 0); //right
  analogWrite(7, 0); //left
  */

  //Controlling spin direction of motors:
  /*digitalWrite(motor1pin1, HIGH);
  digitalWrite(motor1pin2, LOW);

  digitalWrite(motor2pin1, HIGH);
  digitalWrite(motor2pin2, LOW);
  delay(1000);

  digitalWrite(motor1pin1, LOW);
  digitalWrite(motor1pin2, HIGH);

  digitalWrite(motor2pin1, LOW);
  digitalWrite(motor2pin2, HIGH);
  delay(1000);*/
}
