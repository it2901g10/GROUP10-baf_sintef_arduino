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