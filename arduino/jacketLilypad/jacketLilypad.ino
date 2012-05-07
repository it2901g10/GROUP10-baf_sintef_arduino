#include <ComputerSerial.h>

//Global variables
static ComputerSerial comp;

//Constants
static const int PIN_VIBRATION = 3;
static const int PIN_SOUND = 9;
static const int PIN_DEBUG_LED = 13;

void* text(byte flag, byte content[], byte contentSize){ 
  for(int i = 0; i < contentSize; i++)
  {
    tone(PIN_SOUND, content[i]*content[i]);
    delay(200);
    noTone(PIN_SOUND);  
  }  
    noTone(PIN_SOUND);
}

void setup()
{
    //Initialize computer serial class
    comp.begin(9600);
    comp.attachFunction(comp.OPCODE_TEXT, &text);
   
    comp.setDeviceName("Jacket Prototype");
    comp.setDeviceVersion("1.3.6");
    
    //Setup pins
    pinMode(PIN_VIBRATION, OUTPUT);	
    comp.addDeviceService("SERVICE_VIBRATION");

    pinMode(PIN_SOUND, OUTPUT);	
    comp.addDeviceService("SERVICE_SPEAKER");

    pinMode(PIN_DEBUG_LED, OUTPUT);    
    comp.addDeviceService("SERVICE_LED_LAMP");
}

void loop()
{
}

void serialEvent()
{
    comp.serialEvent();
}
