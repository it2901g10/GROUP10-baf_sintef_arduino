#include <LiquidCrystal.h>

LiquidCrystal lcd(8, 9, 4, 5, 6, 7);

void setup(){
  Serial.begin(9600);
  pinMode(13, OUTPUT);
  
  lcd.begin(16, 2);
  lcd.print("button: -");
  lcd.setCursor(0,1);
  lcd.print("Bytes: 0");
}

void loop(){
  static int oldkey = -1;
  int adc_key_in = analogRead(0); // read the value from the
  digitalWrite(13, HIGH); 
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
  digitalWrite(13, LOW); 
}

void serialEvent(){
  static byte prevByte = 0x01;
  static int bytes = 0;
  static boolean toggle = false;
  //digitalWrite(13, toggle);
  while(Serial.available()){
    byte currByte = Serial.read();
    
    if (currByte == 0xFF && prevByte == 0x00){
      Serial.write((byte)0x00);
      Serial.write((byte)0xFF);
    }
    else {
      Serial.write(currByte);
    }
    bytes++;
    prevByte = currByte;
  }
  toggle = !toggle;
  lcd.setCursor(7, 1);
  lcd.print(bytes);
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
