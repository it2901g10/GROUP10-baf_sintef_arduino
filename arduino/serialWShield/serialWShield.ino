#define LCD 1
#define KEYS 0

#include <LiquidCrystal.h>
//#include <DogLcd.h>

#include <ComputerSerial.h>

LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
//DogLcd lcd(12, 11, 9, 10);

ComputerSerial comp;

void text(byte flag, byte content[], byte contentSize){
	lcd.setCursor(0, 0);
	lcd.print("YAY");
}

static unsigned long bytes = 0;
void setup(){
	comp.begin(9600);
	//comp.attachFunction(comp.OPCODE_TEXT, &text);
	
  	pinMode(13, OUTPUT);
	
	//lcd.begin(DOG_LCD_M163);
	lcd.begin(16, 2);
	lcd.setCursor(0, 0);
	lcd.print("Recvd: -");
	lcd.setCursor(0,1);
	lcd.print("Bytes: 0");
	//lcd.setCursor(0, 2);
	//lcd.print("Alive: ");
}

void loop(){
	static unsigned long time = millis();
	static unsigned long lastBytes = bytes;
	if (millis() > time + 1000){
		lcd.setCursor(7, 0);
		lcd.print("         ");
		lcd.setCursor(7, 0);
		lcd.print(bytes-lastBytes);
		lcd.print("b/s");
		lastBytes = bytes;
		time = millis();
		
  		//lcd.setCursor(7, 2);
		//lcd.print(millis()/1000);
		//lcd.print("s");

		lcd.setCursor(7, 1);
		lcd.print(bytes);
	}
}

void serialEvent(){
	bytes += Serial.available();
	
	comp.serialEvent();
}
