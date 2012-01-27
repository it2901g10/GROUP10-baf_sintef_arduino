void setup(){
	Serial.begin(9600);
	pinMode(13, OUTPUT);
}

void loop(){
	delay(500);
}

void serialEvent(){
	digitalWrite(13, HIGH);
	while (Serial.available()){
		Serial.write(Serial.read());
	}
}