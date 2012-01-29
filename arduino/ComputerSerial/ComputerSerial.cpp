#include <Arduino.h>

#include "ComputerSerial.h"

ComputerSerial::ComputerSerial(){
	
}

void ComputerSerial::begin(int baud){
	Serial.begin(baud);
}

void ComputerSerial::commandHandler(byte size, byte opcode, byte flag, byte content[]) {
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

void ComputerSerial::ack(byte opcode){
	Serial.write(START_BYTE);
	Serial.write(4);
	Serial.write(OPCODE_RESPONSE);
	Serial.write(opcode);
	Serial.write(NULL_BYTE);
}

void ComputerSerial::ping() {
	// Send ping response
	Serial.write((byte)0x00);
	Serial.write((byte)0xFF);
}

void ComputerSerial::text(byte size, byte flag, byte content[]) {
	// Print content on display(flag)
	ack(OPCODE_TEXT);
}

void ComputerSerial::sensor(byte number) {
	// Send value of sensor(number)
}

void ComputerSerial::pinToggle(byte pin) {
	// Toggle pin(pin)
}

void ComputerSerial::pinRead(byte pin) {
	// Send pin(pin) value
}

void ComputerSerial::pinWrite(byte pin, byte value) {
	// Set value of pin(pin)
}

void ComputerSerial::reset() {
	// Reset arduino
}


void ComputerSerial::serialEvent(){
	static int state = STATE_START;
	
	static byte size = 0;
	static byte opcode = 0;
	static byte flag = 0;
	static byte content[60];
	static byte content_counter = 0;
	
	while(Serial.available()){
		
		switch (state){
			case STATE_START:
				if (Serial.read() == START_BYTE){
					state = STATE_SIZE;
				}
				else{
					// DERP
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
		bytesReceived++;
	}
}

unsigned int ComputerSerial::getBytesReceived(){
	return bytesReceived;
}
