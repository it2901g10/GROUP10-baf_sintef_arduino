#define LCD 1
#define KEYS 0

//#include <LiquidCrystal.h>
#include <DogLcd.h>

#include <ComputerSerial.h>

//LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
DogLcd lcd(12, 11, 9, 10);

ComputerSerial comp;

void setup(){
	comp.begin(9600);
	
  	pinMode(13, OUTPUT);
	
	lcd.begin(DOG_LCD_M163);
	lcd.print("Recvd: -");
	lcd.setCursor(0,1);
	lcd.print("Bytes: 0");
	lcd.setCursor(0, 2);
	lcd.print("Alive: ");
}

void loop(){
  	lcd.setCursor(7, 2);
	lcd.print(millis()/1000);
	lcd.print("s");
	delay(100);
}

void serialEvent(){
	static unsigned long bytes = 0;
	bytes += Serial.available();
	
	comp.serialEvent();
	
	lcd.setCursor(7, 1);
	lcd.print(bytes);
}
