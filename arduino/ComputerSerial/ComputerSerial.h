#ifndef COMPUTER_SERIAL_H
#define COMPUTER_SERIAL_H

#include <Arduino.h>

#define START_BYTE (byte)0xFF
#define NULL_BYTE (byte)0x00

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
		OPCODE_RESPONSE = 0xFE,
		OPCODE_RESET = 0xFF
	};
	
	void commandHandler(byte size, byte opcode, byte flag, byte content[]);
	void ack(byte opcode);
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
	void begin(int baud);
	
	unsigned int getBytesReceived();
};

#endif
