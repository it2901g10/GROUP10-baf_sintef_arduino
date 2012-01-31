#ifndef DISPLAY_H
#define DISPLAY_H

#include "Arduino.h"

class Display{
	uint8_t numDisplays;
	uint8_t displayCounter;
	Print** displays;
public:
	Display(uint8_t numDisplays);

	void attachDisplay(Print *display);

	void print(uint8_t display, uint8_t charArray[], uint8_t arrSize);
};

#endif