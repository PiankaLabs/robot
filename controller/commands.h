#ifndef COMMANDS
#define COMMANDS

#include "constants.h"

struct command
{
   int motor;
   int direction;
   float speed;
};

typedef struct command Command;

#endif
