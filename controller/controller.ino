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

    //TODO: leaky SRP
    if (command.hello) {
      Serial.println("hello");
    }
    
    update_state(command);
  }

  apply_state();
}
