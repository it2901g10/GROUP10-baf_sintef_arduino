#include <ComputerSerial.h>

ComputerSerial comp;

void* analogTemp(byte flag, byte content[], word contentSize){
  int *status = (int*)malloc(sizeof(int));
  *status = analogRead(flag < 2 ? flag : 0);
  return status;
}

static unsigned long bytes = 0;
void setup(){
  comp.begin(9600);
  comp.setDeviceName("Temperature prototype");
  comp.setDeviceVersion("2.0");

  comp.attachFunction(comp.OPCODE_SENSOR, &analogTemp);

}

void loop(){

}


void serialEvent(){
  bytes += Serial.available();

  comp.serialEvent();
}

