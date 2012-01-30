#ifndef COMPUTER_SERIAL_H
#define COMPUTER_SERIAL_H

#include <Arduino.h>

#define START_BYTE (byte)0xFF
#define NULL_BYTE (byte)0x00

#define CONTENT_SIZE 250

class ComputerSerial{
	// SerialEvent state enum
	typedef enum {
		STATE_START,
		STATE_SIZE,
		STATE_OPCODE,
		STATE_FLAG,
		STATE_CONTENT
	};
	
	void commandHandler(byte size, byte opcode, byte flag, byte content[]);
	void ack(byte opcode);
	void ack(byte opcode, byte content[], byte contentSize);
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
	void attachFunction(byte opcode, 
		void (*handler)(byte flag, byte content[], byte contentSize));
	
	unsigned int getBytesReceived();
	
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

private: 
	static const byte NUM_OPCODES = 6;
	int (*functions[NUM_OPCODES]) (byte flag, byte content[], byte contentSize);
};

#endif
