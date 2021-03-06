#define LCD 0
#define LCD_LINES 2
#define LCD_COLUMNS 16

#if LCD==0
	#include <LiquidCrystal.h>
#elif LCD==1
	#include <DogLcd.h>
#elif LCD==2
	#include <LiquidCrystal.h>
#endif

#include <ComputerSerial.h>

#if LCD==0
	LiquidCrystal lcd(8, 7, 12, 11, 10, 9);
#elif LCD==1
	DogLcd lcd(12, 11, 9, 10);
#elif LCD==2
	LiquidCrystal lcd(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2);
#endif

ComputerSerial comp;

void* text(byte flag, byte content[], byte contentSize){
#ifdef LCD
	lcd.setCursor(7, 0);
	lcd.print("         ");
	lcd.setCursor(7, 0);
	lcd.print((char*)content);
#else
	static bool toggle = false;
	digitalWrite(13, toggle ? HIGH : LOW);
	toggle = !toggle;
#endif
} 

void* buttons(byte flag, byte content[], byte contentSize){
	int *status = (int*)malloc(sizeof(int));
	*status = analogRead(flag < 6 ? flag : 0);
	return status;
}

void* data(byte flag, byte content[],byte contentSize){
	lcd.setCursor(7, 0);
	lcd.print("         ");
	lcd.setCursor(7,0);
	lcd.print(content[0]);
}

static unsigned long bytes = 0;
void setup(){
	comp.begin(9600);
	comp.attachFunction(comp.OPCODE_TEXT, &text);
	comp.attachFunction(comp.OPCODE_SENSOR, &buttons);
	comp.attachFunction(comp.OPCODE_DATA, &data);
	
	
	//comp.setDeviceName("Derp");
	//comp.setDeviceVersion("0.1");
	//comp.addDeviceService("LED", "good");
	//comp.addDeviceDownloadLink("DerpOS", "http://google.com");

  	pinMode(13, OUTPUT);
	
#if LCD==0
	lcd.begin(LCD_COLUMNS, LCD_LINES);
#elif LCD==1
	lcd.begin(DOG_LCD_M163);
	lcd.setCursor(0, 2);
	lcd.print("Bytes: ");
#elif LCD==2
	lcd.begin(LCD_COLUMNS, LCD_LINES);
#endif
	
#ifdef LCD
	lcd.setCursor(0, 0);
	lcd.print("Recvd: -");
	lcd.setCursor(0,1);
	//lcd.print("Bytes: 0");
	//lcd.setCursor(0, 2);
	lcd.print("Alive: ");
#endif
}

void loop(){
	static unsigned long time = millis();
	static unsigned long lastBytes = bytes;
	if (millis() > time + 1000){
		//lcd.setCursor(7, 0);
		//lcd.print(bytes-lastBytes);
		//lcd.print("b/s");
		lastBytes = bytes;
		time = millis();
#ifdef LCD		
  		lcd.setCursor(7, 1);
		lcd.print(millis()/1000);
		lcd.print("s");
#endif
#if LCD==1
		lcd.setCursor(7, 2);
		lcd.print(bytes);
#endif
	}
}

void serialEvent(){
	bytes += Serial.available();

	static boolean flag = false;
        digitalWrite(13, flag ? HIGH : LOW);
        flag = !flag;

	comp.serialEvent();
}
