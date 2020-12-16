#ifndef STATE
#define STATE

#include "constants.h"
#include "commands.h"

struct state
{
   int direction;
   int speed;
};

typedef struct state State;

extern State LeftMotor;
extern State RightMotor;

void update_state(Command command);

#endif
