#include "Display.h"

#include "Arduino.h"

#include <stdio.h>

Display::Display(const uint8_t numDisplays) :numDisplays(numDisplays){
	displayCounter = 0;
	displays = (Print**)malloc(numDisplays * sizeof(&displays));
}

void Display::attachDisplay(Print *display){
	if (displayCounter >= numDisplays) return; // Trying to add more displays than allocated for

	displays[displayCounter] = display;

	displayCounter++;
}

extern void Display::print(uint8_t display, uint8_t charArray[], uint8_t arrSize){
	for (uint8_t i = 0; i < displayCounter; ++i){
		displays[i]->write(charArray, arrSize);
	}
}