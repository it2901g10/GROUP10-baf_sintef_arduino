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
  
  if (LCD){
  lcd.begin(DOG_LCD_M163);
  lcd.print("Recvd: -");
  lcd.setCursor(0,1);
  lcd.print("Bytes: 0");
  lcd.setCursor(0, 2);
  lcd.print("Alive: ");
  }
}

void loop(){
  if (LCD && KEYS){
  static int oldkey = -1;
  int adc_key_in = analogRead(0); // read the value from the
  //digitalWrite(13, HIGH); 
  int key = get_key(adc_key_in); // convert into key press 
  if (key != oldkey){ // if keypress is detected
    delay(50); // wait for debounce time 
    adc_key_in = analogRead(0); //read the value from the sensor
    key = get_key(adc_key_in); // convert into key press
    if (key != oldkey) { 
      oldkey = key; 
      if (key >=0){
        lcd.setCursor(8, 0); //line=2, x=0 
        lcd.print(key, DEC);
      }
    }
  }
  //digitalWrite(13, LOW); 
  }
  lcd.setCursor(7, 2);
  lcd.print(millis()/1000);
  lcd.print("s");
  delay(100);
}

void serialEvent(){
	static unsigned long bytes = 0;
	bytes += Serial.available();
	
	comp.serialEvent();
	
	if (LCD){
		lcd.setCursor(7, 1);
		lcd.print(bytes);
	}
}

int get_key(int value){
  const int adc_key_val[5] ={30, 150, 360, 535, 760 };
  const int NUM_KEYS = 5;
  
  for (int i = 0; i < NUM_KEYS; ++i){
    if (value <= adc_key_val[i]){
      return i;
    }
  }
  return -1;
}
