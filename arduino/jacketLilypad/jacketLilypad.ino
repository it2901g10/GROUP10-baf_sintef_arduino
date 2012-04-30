#include <ComputerSerial.h>

//Global variables
static ComputerSerial comp;

//Constants
static const int PIN_VIBRATION = 3;
static const int PIN_SOUND = 9;
static const int PIN_DEBUG_LED = 13;

void* text(byte flag, byte content[], byte contentSize){
    static bool toggle = false;
    digitalWrite(PIN_DEBUG_LED, toggle ? HIGH : LOW);
    toggle = !toggle;  
  
  for(int i = 0; i < contentSize; i++)
  {
//    tone(PIN_SOUND, content[i]*100);
    tone(PIN_SOUND, 400);
    delay(200);
    noTone(PIN_SOUND);  
  }
  
    tone(PIN_SOUND, 400);
    delay(200);
    noTone(PIN_SOUND);  
}

void* deviceInfo(byte flag, byte content[], byte contentSize)
{
    static byte response[] = "{NAME:\"Jacket Prototype\", VERSION:\"1.3.0\","
         "SERVICES:[\"SERVICE_LED_LAMP\", \"SERVICE_LCD_SCREEN\", \"SERVICE_VIBRATION\"],"
         "LINKS:[{\"DEFAULT\":\"No download link\"}]}";
         
    return &response;
}

void setup()
{
    //Initialize computer serial class
    comp.begin(9600);
    comp.attachFunction(comp.OPCODE_TEXT, &text);
    comp.attachFunction(comp.OPCODE_DEVICE_INFO, &deviceInfo);
    
  
    //Setup pins
    pinMode(PIN_VIBRATION, OUTPUT);	
    pinMode(PIN_SOUND, OUTPUT);	
    pinMode(PIN_DEBUG_LED, OUTPUT);    
}

void loop()
{
}

void serialEvent()
{
    comp.serialEvent();
}
