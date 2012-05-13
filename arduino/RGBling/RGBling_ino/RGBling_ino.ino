#include <ComputerSerial.h>

ComputerSerial comp;
static unsigned long bytes = 0;
void* data(byte flag, byte content[], byte contentSize){
	if(contentSize < 6) return NULL;

  analogWrite(11,content[0]);
  analogWrite(10,content[1]);
  analogWrite(9,content[2]);
  analogWrite(6,content[3]);
  analogWrite(5,content[4]);
  analogWrite(3,content[5]);
  
}

void setup(){
	comp.begin(9600);
	comp.attachFunction(comp.OPCODE_DATA,&data);
        pinMode(11,OUTPUT);
        pinMode(10,OUTPUT);
        pinMode(9,OUTPUT);
        pinMode(6,OUTPUT);
        pinMode(5,OUTPUT);
        pinMode(3,OUTPUT);
}
void loop(){
	/*static unsigned long time = millis();
	static unsigned long lastBytes = bytes;
	if (millis() > time + 1000){
		//lcd.setCursor(7, 0);
		//lcd.print(bytes-lastBytes);
		//lcd.print("b/s");
		lastBytes = bytes;
		time = millis();
	}*/
	delay(20);

}


void serialEvent(){
	bytes += Serial.available();
	comp.serialEvent();
}
