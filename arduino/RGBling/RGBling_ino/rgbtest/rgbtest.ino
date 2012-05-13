void setup(){
	pinMode(11,OUTPUT);
	pinMode(10,OUTPUT);
	pinMode(9,OUTPUT);
}


void loop(){
	analogWrite(11,150);
	analogWrite(10,150);
	analogWrite(9,150);


	delay(1000);
}