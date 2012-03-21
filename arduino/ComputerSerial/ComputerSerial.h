/*
* Copyright 2012 NTNU
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

#ifndef COMPUTER_SERIAL_H
#define COMPUTER_SERIAL_H

#include <Arduino.h>

#define START_BYTE (byte)0xFF
#define NULL_BYTE (byte)0x00

#define CONTENT_SIZE 250
#define TIMEOUT 2000

class ComputerSerial{
	// SerialEvent state enum
	typedef enum {
		STATE_START,
		STATE_SIZE,
		STATE_OPCODE,
		STATE_FLAG,
		STATE_CONTENT
	};
	
	void commandHandler(uint8_t size, uint8_t opcode, uint8_t flag, uint8_t content[]);
	void ack(uint8_t opcode);
	void ack(uint8_t opcode, uint8_t content[], uint8_t contentSize);
	void ping();
	void text(uint8_t size, uint8_t flag, uint8_t content[]);
	void sensor(uint8_t number);
	void pinPulse(uint8_t pin);
	void pinRead(uint8_t pin);
	void pinWrite(uint8_t pin, uint8_t value);
	void reset();
	
	
	
	unsigned int bytesReceived;
	
public:
	ComputerSerial();
	static void* placeHolder(uint8_t flag, uint8_t content[], uint8_t contentSize);
	void serialEvent();
	void begin(int baud);
	void attachFunction(uint8_t opcode, 
		void* (*handler)(uint8_t flag, uint8_t content[], uint8_t contentSize));
	
	unsigned int getBytesReceived();
	
	// Enum for protocol OPCodes
	typedef enum {
		OPCODE_PING, 	// 0
		OPCODE_TEXT, 	// 1
		OPCODE_SENSOR, 	// 2
		OPCODE_DATA, 	// 3
		OPCODE_PIN_R, 	// 4
		OPCODE_PIN_W, 	// 5
		OPCODE_RESPONSE = 0xFE,
		OPCODE_RESET = 0xFF
	};

private: 
	static const uint8_t NUM_OPCODES = 6;
	void* (*functions[NUM_OPCODES]) (uint8_t flag, uint8_t content[], uint8_t contentSize);
};

#endif
