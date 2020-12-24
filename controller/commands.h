#ifndef COMMANDS
#define COMMANDS

struct command
{
  bool hello;  
  int motor;
  int direction;
  float speed;
};

typedef struct command Command;

#endif
