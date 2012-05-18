#include <ComputerSerial.h>
ComputerSerial comp;

void* analogTemp(byte flag, byte content[], word contentSize)
{
  int *status = (int*)malloc(sizeof(int));
  *status = analogRead(flag < 2 ? flag : 0);
  return status;
}

static unsigned long bytes = 0;
void setup()
{
  comp.begin(9600);
  comp.setDeviceName("Temperature prototype");
  comp.setDeviceVersion("2.0.3");

  comp.addDeviceService("TEMP_SENSOR", "0,1");
  comp.attachFunction(comp.OPCODE_SENSOR, &analogTemp);
  
  comp.addDeviceService("LED_LAMP", "13");
  pinMode(13, OUTPUT);
  
  comp.addDeviceDownloadLink("http://folk.ntnu.no/svarvaa/utils/pro2www/#appId2", "Temp Measure");
}

void loop()
{
  static long timeout = 0;
  static boolean toggle = false;
  if(millis() > timeout)
  {
    timeout = millis() + 500;
    digitalWrite(13, toggle ? HIGH : LOW);
    toggle = !toggle;
  }
  
}


void serialEvent()
{
  comp.serialEvent();
}

