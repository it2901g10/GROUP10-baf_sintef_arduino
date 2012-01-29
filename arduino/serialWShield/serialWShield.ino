#define LCD 1
#define KEYS 0

//#include <LiquidCrystal.h>
#include <DogLcd.h>

//LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
DogLcd lcd(12, 11, 9, 10);

void setup(){
  Serial.begin(9600);
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

typedef enum {
	OPCODE_PING,
	OPCODE_TEXT,
	OPCODE_SENSOR,
	OPCODE_PIN_T,
	OPCODE_PIN_R,
	OPCODE_PIN_W,
	OPCODE_RESET = 0xFF
};

void commandHandler(byte size, byte opcode, byte flag, byte content[]) {
	switch (opcode) {
		case OPCODE_PING:
			ping();
			break;
		case OPCODE_TEXT:
			text(size, flag, content);
			break;
		case OPCODE_SENSOR:
			break;
		case OPCODE_PIN_T:
			break;
		case OPCODE_PIN_R:
			break;
		case OPCODE_PIN_W:
			break;
		case OPCODE_RESET:
			break;
		default:
			Serial.println("commandHandler herp derp");
	}
}

void ping() {
	Serial.write((byte)0x00);
	Serial.write((byte)0xFF);
}

void text(byte size, byte flag, byte content[]) {
	if (LCD){
	int counter = 7;
	lcd.setCursor(counter,0);
	for(int i = 0; i < size-3 ; i++) {
		lcd.print((char)content[i]);
		lcd.setCursor(++counter,0);
	}
	}
}

void sensor(byte number) {
	
}

void pinToggle(byte pin) {
	
}

void pinRead(byte pin) {
	
}

void pinWrite(byte pin, byte value) {
	
}

void reset() {
	
}

// SerialEvent state enum
typedef enum {
	STATE_START,
	STATE_SIZE,
	STATE_OPCODE,
	STATE_FLAG,
	STATE_CONTENT
};

void serialEvent(){
	static int state = STATE_START;
	
	static byte size = 0;
	static byte opcode = 0;
	static byte flag = 0;
	static byte content[60];
	static byte content_counter = 0;
	
	static int bytes = 0;
	
	while(Serial.available()){
		
		switch (state){
			case STATE_START:
				if (Serial.read() == (byte)0xFF){
					state = STATE_SIZE;
				}
				else{
					//digitalWrite(13, HIGH);
				}
				break;
			case STATE_SIZE:
				size = Serial.read();
				state = STATE_OPCODE;
				break;
			case STATE_OPCODE:
				opcode = Serial.read();
				state = STATE_FLAG;
				break;
			case STATE_FLAG:
				flag = Serial.read();
				state = STATE_CONTENT;
				break;
			case STATE_CONTENT:
				content[content_counter] = Serial.read();
				content_counter++;
				if (content_counter+3 == size) {
					commandHandler(size, opcode, flag, content);
					content_counter = 0;
					state = STATE_START;
				} 
				break;
			default:
				break;
		}
		bytes++;
	}
	
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
