#ifndef INITIALIZE
#define INITIALIZE

void initialize() {
  Serial.begin(9600);

  pinMode(LeftMotorPin1, OUTPUT);
  pinMode(LeftMotorPin2, OUTPUT);
  pinMode(LeftSpeedPin, OUTPUT);
  
  pinMode(RightMotorPin1, OUTPUT);
  pinMode(RightMotorPin2, OUTPUT);
  pinMode(RightSpeedPin, OUTPUT);
}

#endif
