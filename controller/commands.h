#ifndef COMMANDS
#define COMMANDS

struct command
{ 
  int motor;
  int direction;
  float speed;
};

typedef struct command Command;

#endif
