#define LCD 0
#define LCD_LINES 2
#define LCD_COLUMNS 16

#if LCD==0
	#include <LiquidCrystal.h>
#elif LCD==1
	#include <DogLcd.h>
#endif

#include <ComputerSerial.h>

#if LCD==0
	LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
#elif LCD==1
	DogLcd lcd(12, 11, 9, 10);
#endif

ComputerSerial comp;

void* text(byte flag, byte content[], byte contentSize){
	lcd.setCursor(7, 0);
	lcd.print((char*)content);
}

void* buttons(byte flag, byte content[], byte contentSize){
	int *status = (int*)malloc(sizeof(int));
	*status = analogRead(0);
	return status;
}

static unsigned long bytes = 0;
void setup(){
	comp.begin(9600);
	comp.attachFunction(comp.OPCODE_TEXT, &text);
	comp.attachFunction(comp.OPCODE_SENSOR, &buttons);
	
  	pinMode(13, OUTPUT);
	
#if LCD==0
	lcd.begin(LCD_COLUMNS, LCD_LINES);
#elif LCD==1
	lcd.begin(DOG_LCD_M163);
#endif
	
	lcd.setCursor(0, 0);
	lcd.print("Recvd: -");
	lcd.setCursor(0,1);
	//lcd.print("Bytes: 0");
	//lcd.setCursor(0, 2);
	lcd.print("Alive: ");
}

void loop(){
	static unsigned long time = millis();
	static unsigned long lastBytes = bytes;
	if (millis() > time + 1000){
		lcd.setCursor(7, 0);
		lcd.print("         ");
		//lcd.setCursor(7, 0);
		//lcd.print(bytes-lastBytes);
		//lcd.print("b/s");
		lastBytes = bytes;
		time = millis();
		
  		lcd.setCursor(7, 1);
		lcd.print(millis()/1000);
		lcd.print("s");

		//lcd.setCursor(7, 1);
		//lcd.print(bytes);
	}
}

void serialEvent(){
	bytes += Serial.available();
	
	comp.serialEvent();
}
