#ifndef COMPUTER_SERIAL_H
#define COMPUTER_SERIAL_H

#define START_BYTE 0xFF

#include <Arduino.h>

class ComputerSerial{
	// SerialEvent state enum
	typedef enum {
		STATE_START,
		STATE_SIZE,
		STATE_OPCODE,
		STATE_FLAG,
		STATE_CONTENT
	};
	
	// Enum for protocol OPCodes
	typedef enum {
		OPCODE_PING,
		OPCODE_TEXT,
		OPCODE_SENSOR,
		OPCODE_PIN_T,
		OPCODE_PIN_R,
		OPCODE_PIN_W,
		OPCODE_RESET = 0xFF
	};
	
	void commandHandler(byte size, byte opcode, byte flag, byte content[]);
	void ping();
	void text(byte size, byte flag, byte content[]);
	void sensor(byte number);
	void pinToggle(byte pin);
	void pinRead(byte pin);
	void pinWrite(byte pin, byte value);
	void reset();
	
	unsigned int bytesReceived;
	
public:
	ComputerSerial();
	
	void serialEvent();
	
	unsigned int getBytesReceived();
};

#endif
