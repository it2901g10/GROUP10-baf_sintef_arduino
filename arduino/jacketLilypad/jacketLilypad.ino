#include <ComputerSerial.h>
#include <LiquidCrystal.h>

//Constants
static const int PIN_LED = 2;
static const int PIN_SOUND = 3;
static const int PIN_VIBRATION = 4;
static const int PIN_DEBUG_LED = 13;

static const int PIN_SCREEN_A = 6;
static const int PIN_SCREEN_B = 7;
static const int PIN_SCREEN_1 = 8;
static const int PIN_SCREEN_2 = 9;
static const int PIN_SCREEN_3 = 10;
static const int PIN_SCREEN_4 = 11;

//Global variables
static ComputerSerial comp;
static LiquidCrystal lcd(PIN_SCREEN_A, PIN_SCREEN_B, PIN_SCREEN_1, PIN_SCREEN_2, PIN_SCREEN_3, PIN_SCREEN_4);

void* text(byte flag, byte content[], byte contentSize)
{
  for(int i = 0; i < contentSize; i++)
  {
    lcd.setCursor(i, 0);
    lcd.print( (char) content[i] );
  }
}

void* data(byte flag, byte data[], byte dataSize)
{
  for(int i = 0; i < dataSize; i++)
  {
    tone(PIN_SOUND, data[i]*data[i]);
    delay(200);
  }  
  noTone(PIN_SOUND);
}

void setup()
{
    //Initialize computer serial class
    comp.begin(9600);   
    comp.setDeviceName("Jacket Prototype");
    comp.setDeviceVersion("1.4.0");
    
    //Setup pins
    comp.addDeviceService("SERVICE_VIBRATION");
    pinMode(PIN_VIBRATION, OUTPUT);	

    comp.addDeviceService("SERVICE_SPEAKER");
    comp.attachFunction(comp.OPCODE_DATA, &data);
    pinMode(PIN_SOUND, OUTPUT);	

    comp.addDeviceService("SERVICE_LED_LAMP");
    pinMode(PIN_DEBUG_LED, OUTPUT);    
    
    comp.addDeviceService("SERVICE_LED_SCREEN");
    comp.attachFunction(comp.OPCODE_TEXT, &text);
    lcd.begin(16, 2);
    
    comp.addDeviceService("SERVICE_LED");
    pinMode(PIN_LED, OUTPUT);
}

void loop()
{
  lcd.setCursor(0, 1);
  lcd.print("Time: ");
  lcd.setCursor(6, 1);
  lcd.print(millis()/1000);
}

void serialEvent()
{
    comp.serialEvent();
}
