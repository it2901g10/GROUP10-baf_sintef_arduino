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

#include <Arduino.h>

#include "ComputerSerial.h"

void* ComputerSerial::placeHolder(uint8_t flag, uint8_t content[], uint8_t contentSize)
{
	return NULL;
}

ComputerSerial::ComputerSerial(int baud)
{
    //Initialize with baudrate if requested
    if(baud != 0) begin(baud);
}

void ComputerSerial::begin(int baud){
	Serial.begin(baud);

	for (int i = 0; i < NUM_OPCODES; ++i){
		attachFunction(i, &ComputerSerial::placeHolder);
	}
}

void ComputerSerial::commandHandler(uint8_t size, uint8_t opcode, uint8_t flag, uint8_t content[]) {
	switch (opcode) {
		case OPCODE_PING:
			ping();
			break;
		case OPCODE_TEXT:
			text(size, flag, content);
			break;
		case OPCODE_SENSOR:
			sensor(flag);
			break;
		case OPCODE_DATA:
			data(size, flag, content);
			break;
		case OPCODE_PIN_R:
			pinRead(flag);
			break;
		case OPCODE_PIN_W:
			pinWrite(flag, content[0]);
			break;
        case OPCODE_DEVICE_INFO:
            getDeviceInfo();
            break;
		case OPCODE_RESET:
			break;
		default:
			break;
	}
}

void ComputerSerial::setDeviceName(const String &name)
{
    deviceName = name;
}

void ComputerSerial::setDeviceVersion(const String &version)
{
    deviceVersion = version;
}

void ComputerSerial::addDeviceService(const String &service, const String &pin)
{
    //Is the first element in the JSon array? If not we need to add a comma seperator
    if(deviceServices.length() > 0) deviceServices += ", ";

    //Append element to array
    //deviceServices += "\"" + service + "\"";
	deviceServices += "{ \"id\": \"" + service + "\", \"pins\" :\"" + pin + "\"}";

	// sample: {"name":"service",
	//			"pins":"1,2,3" }

}

void ComputerSerial::addDeviceDownloadLink(const String &link, const String &platform)
{
    //Is the first element in the JSon array? If not we need to add a comma seperator
    if(deviceDownloadLinks.length() > 0) deviceDownloadLinks += ", ";

    //Append element to array
    //deviceDownloadLinks += "{" + platform + ": \"" + link + "\"}";

	deviceDownloadLinks += "{\"" + platform + "\": \"" + link + "\"}";

	// sample: {"platform":"link"}
}

void ComputerSerial::getDeviceInfo(){
    //Build the device info JSON object
    String deviceInfo;

    //Device name
	deviceInfo += "{\"name\":\"";
	deviceInfo += deviceName;
	deviceInfo += "\",";

    //Device version
    deviceInfo += "\"version\":\"";
    deviceInfo += deviceVersion;
    deviceInfo += "\",";

    //Device services
	deviceInfo += "\"services\": [";
	deviceInfo += deviceServices;
	deviceInfo += "],";

    //Device links
	deviceInfo += "\"links\": [";
	deviceInfo += deviceName;
	deviceInfo += "]}";

    //send response back
	Serial.write(START_BYTE);
	Serial.write(deviceInfo.length() + 3);
	Serial.write(OPCODE_RESPONSE);
	Serial.write(OPCODE_DEVICE_INFO);
    for(int i = 0; i < deviceInfo.length(); i++) {
        Serial.write(deviceInfo[i]);
    }
}

void ComputerSerial::ack(uint8_t opcode){
	Serial.write(START_BYTE);
	Serial.write(4);
	Serial.write(OPCODE_RESPONSE);
	Serial.write(opcode);
	Serial.write(NULL_BYTE);
}

void ComputerSerial::ack(uint8_t opcode, uint8_t content[], uint8_t contentSize){
	Serial.write(START_BYTE);
	Serial.write(contentSize + 3);
	Serial.write(OPCODE_RESPONSE);
	Serial.write(opcode);
	for (int i = 0; i < contentSize; i++){
		Serial.write(content[i]);
	}
}

void ComputerSerial::ping() {
	// Send ping response
	//Serial.write((byte)0x00);
	//Serial.write((byte)0xFF);
	ack(OPCODE_PING);
}

void ComputerSerial::text(uint8_t size, uint8_t flag, uint8_t content[]) {
	// Print content on display(flag)
	functions[OPCODE_TEXT](flag, content, size-3);
	ack(OPCODE_TEXT);
}

void ComputerSerial::sensor(uint8_t number) {
	// Send value of sensor(number)
	uint8_t content[] = {};
	int *status = (int*)functions[OPCODE_SENSOR](number, content, 0);

	uint16_t value = *status;
	uint8_t replyContent[] = {value >> 8, value};

	ack(OPCODE_SENSOR, replyContent, 2);

	free(status);
}

void ComputerSerial::data(uint8_t size, uint8_t flag, uint8_t content[]) {
	functions[OPCODE_DATA](flag, content, size-3);
	ack(OPCODE_DATA);
}

void ComputerSerial::pinRead(uint8_t pin) {
	// Send pin(pin) value
	pinMode(pin, INPUT);
	int value = digitalRead(pin);
	uint8_t content[] = {value > 0 ? 1 : 0};
	ack(OPCODE_PIN_R, content, 1);
}

void ComputerSerial::pinWrite(uint8_t pin, uint8_t value) {
	// Set value of pin(pin)
	pinMode(pin, OUTPUT);
	digitalWrite(pin, value ? HIGH : LOW);
	ack(OPCODE_PIN_W);
}

void ComputerSerial::reset() {
	// Reset arduino
}

void ComputerSerial::attachFunction(uint8_t opcode,
	void* (*handler)(uint8_t flag, uint8_t content[], uint8_t contentSize)){
		functions[opcode] = handler;
}

void ComputerSerial::serialEvent(){
	static long time = millis();
	static int state = STATE_START;

	static uint8_t size = 0;
	static uint8_t opcode = 0;
	static uint8_t flag = 0;
	static uint8_t content[CONTENT_SIZE];
	static uint8_t content_counter = 0;

	if (millis() - time > TIMEOUT && state != STATE_START){
		state = STATE_START;
	}
	time = millis();

	while(Serial.available()){

		switch (state){
			case STATE_START:
				if (Serial.read() == START_BYTE){
					state = STATE_SIZE;
				}
				break;
			case STATE_SIZE:
				size = Serial.read();
				if (size > CONTENT_SIZE + 3){
					state = STATE_START;
				}
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
