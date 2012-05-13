#include <ComputerSerial.h>
#include <LiquidCrystal.h>

//Constants
static const int PIN_LED = 5;
static const int PIN_SOUND = 6;
static const int PIN_VIBRATION = 4;
static const int PIN_DEBUG_LED = 13;

static const int PIN_SCREEN_A = 7;
static const int PIN_SCREEN_B = 8;
static const int PIN_SCREEN_1 = 9;
static const int PIN_SCREEN_2 = 10;
static const int PIN_SCREEN_3 = 11;
static const int PIN_SCREEN_4 = 12;

//Global variables
static ComputerSerial comp;
static LiquidCrystal lcd(PIN_SCREEN_A, PIN_SCREEN_B, PIN_SCREEN_1, PIN_SCREEN_2, PIN_SCREEN_3, PIN_SCREEN_4);

void* text(byte flag, byte content[], word contentSize)
{
  for(int i = 0; i < contentSize; i++)
  {
    lcd.setCursor(i, 0);
    lcd.print( (char) content[i] );
  }
}

void* data(byte flag, byte data[], word dataSize)
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
    comp.setDeviceVersion("1.4.2");
    
    //Setup pins
    comp.addDeviceService("VIBRATION", "4");
    pinMode(PIN_VIBRATION, OUTPUT);

    comp.addDeviceService("SPEAKER", "6");
    comp.attachFunction(comp.OPCODE_DATA, &data);
    pinMode(PIN_SOUND, OUTPUT);	
    
    comp.addDeviceService("LED_SCREEN", "");
    comp.attachFunction(comp.OPCODE_TEXT, &text);
    lcd.begin(16, 2);
    
    comp.addDeviceService("LED_LAMP", "5,13");
    pinMode(PIN_LED, OUTPUT);
    pinMode(PIN_DEBUG_LED, OUTPUT);    
    
    //Download links
    comp.addDeviceDownloadLink("NONE", "DEFAULT");
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
