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