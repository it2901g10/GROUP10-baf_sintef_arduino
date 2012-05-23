/******************************************************************************
 * This example shows how to use the ShiftPWM library to PWM many outputs.
 * All shift registers are chained, so they can be driven with 3 pins from the arduino.
 * A timer interrupt updates all PWM outputs according to their duty cycle setting.
 * The outputs can be inverted by making ShiftPWM_invertOutputs true.
 * 
 * How the library works:
 * The ShiftPWM class keeps a setting for the duty cycle for each output pin, which
 * can be set using the provided functions. It also keeps a counter which it compares 
 * to these duty cycles. This timer continuously runs from 0 to the maximum duty cycle.
 * 
 * A timer interrupt is configured by ShiftPWM.Start(pwmFrequency,maxBrightness).
 * The interrupt frequency is set to pwmFrequency * (maxBrightness+1).
 * Each interrupt all duty cycles are compared to the counter and the corresponding pin
 * is written 1 or 0 based on the result. Then the counter is increased by one.
 * 
 * The duration of the interrupt depends on the number of shift registers (N).
 * T = 112 + 43*N 
 * 
 * The load of the interrupt function on your program can be calculated:
 * L = Interrupt frequency * interrupt duration / clock frequency
 * L = F*(Bmax+1)*(112+43*N)/F_CPU
 * Quick reference for load:
 * 3 registers  255 maxBrightness 75Hz  load = 0.29
 * 6 registers  255 maxBrightness 75Hz  load = 0.45
 * 24 registers 100 maxBrightness 75Hz  load = 0.54
 * 48 registers  64 maxBrightness 75Hz  load = 0.66
 * 96 registers  32 maxBrightness 75Hz  load = 0.66
 * 
 * A higher interrupt load will mean less computional power for your main program,
 * so try to keep it as low as possible and at least below 0.9.
 * 
 * The following functions are available:
 * 
 * ShiftPWM.Start(int ledFrequency, int max_Brightness)		Enable ShiftPWM with desired frequency and brightness levels
 * ShiftPWM.SetAmountOfRegisters(int newAmount)			Set or change the amount of output registers. Can be changed at runtime.
 * ShiftPWM.PrintInterruptLoad()				Print information on timer usage, frequencies and interrupt load
 * ShiftPWM.OneByOneSlow()  				        Fade in and fade out all outputs slowly
 * ShiftPWM.OneByOneFast()					Fade in and fade out all outputs fast
 * ShiftPWM.SetOne(int pin, unsigned char value)		Set the duty cycle of one output
 * ShiftPWM.SetAll(unsigned char value)				Set all outputs to the same duty cycle
 * 
 * ShiftPWM.SetGroupOf2(int group, unsigned char v0, unsigned char v1);
 * ShiftPWM.SetGroupOf3(int group, unsigned char v0, unsigned char v1, unsigned char v2);
 * ShiftPWM.SetGroupOf4(int group, unsigned char v0, unsigned char v1, unsigned char v2, unsigned char v3);
 * ShiftPWM.SetGroupOf5(int group, unsigned char v0, unsigned char v1, unsigned char v2, unsigned char v3, unsigned char v4);
 * 		--> Set a group of outputs to the given values. SetGroupOf3 is useful for RGB LED's. Each LED will be a group.
 * 
 * ShiftPWM.SetRGB(int led, unsigned char r,unsigned char g,unsigned char b);      // Set one LED to an RGB value
 * ShiftPWM.SetAllRGB(unsigned char r,unsigned char g,unsigned char b);            // Set all LED's to an RGB value
 * ShiftPWM.SetHSV(int led, unsigned int hue, unsigned int sat, unsigned int val); // Set one LED to an HSV value
 * ShiftPWM.SetAllHSV(unsigned int hue, unsigned int sat, unsigned int val);       // Set one LED to an HSV value
 * Note: the RGB and HSV functions assume that the outputs are RGBRGBRGB... without gaps. More flexibility in setup will be added soon.
 *
 * Debug information for wrong input to functions is also send to the serial port,
 * so check the serial port when you run into problems.
 * 
 * ShiftPWM v1.1, (c) Elco Jacobs, May 2012.
 * 
 *****************************************************************************/
 
//#include <Servo.h> <-- If you include Servo.h, which uses timer1, ShiftPWM will automatically switch to timer2
#define SHIFTPWM_NOSPI
#include <ComputerSerial.h>

// Clock and data pins are pins from the hardware SPI, you cannot choose them yourself.
// Data pin is MOSI (Arduino: 11, Arduino Mega: 51, Teensy 2.0: 2, Teensy 2.0++: 22) 
// Clock pin is SCK (Arduino: 13, Arduino Mega: 52, Teensy 2.0: 1, Teensy 2.0++: 21)

// You can choose the latch pin yourself.
const int ShiftPWM_dataPin =11;
const int ShiftPWM_clockPin=12;
const int ShiftPWM_latchPin=8;

// If your LED's turn on if the pin is low, set this to 1, otherwise set it to 0.
const bool ShiftPWM_invertOutputs = 0; 

#include <ShiftPWM.h>   // include ShiftPWM.h after setting the pins!

// Here you set the number of brightness levels, the update frequency and the number of shift registers.
// These values affect the load of ShiftPWM.
// Choose them wisely and use the PrintInterruptLoad() function to verify your load.
ComputerSerial comp;
static unsigned long bytes = 0;
unsigned char maxBrightness = 255;
unsigned char pwmFrequency = 75;
int numRegisters = 24;
int numRGBleds = 27;

void* data(byte flag, byte content[], byte contentSize){
  if(contentSize < 27) return NULL;
  
  ShiftPWM.SetRGB(0,content[0],content[1],content[2]);
  ShiftPWM.SetRGB(1,content[3],content[4],content[5]);
  ShiftPWM.SetRGB(2,content[6],content[7],content[8]);
  ShiftPWM.SetRGB(3,content[9],content[10],content[11]);
  ShiftPWM.SetRGB(4,content[12],content[13],content[14]);
  ShiftPWM.SetRGB(5,content[15],content[16],content[17]);
  ShiftPWM.SetRGB(6,content[18],content[19],content[20]);
  ShiftPWM.SetRGB(7,content[21],content[22],content[23]);
  ShiftPWM.SetRGB(8,content[24],content[25],content[26]);
}


void setup()   {       
           comp.begin(9600);
  comp.attachFunction(comp.OPCODE_DATA,&data); 
  pinMode(ShiftPWM_latchPin, OUTPUT);
  pinMode(ShiftPWM_clockPin, OUTPUT);
  pinMode(ShiftPWM_dataPin,  OUTPUT);

  // Sets the number of 8-bit registers that are used.
  ShiftPWM.SetAmountOfRegisters(numRegisters);


  // SetPinGrouping allows flexibility in LED setup. 
  // If your LED's are connected like this: RRRRGGGGBBBBRRRRGGGGBBBB, use SetPinGrouping(4).
  ShiftPWM.SetPinGrouping(1); //This is the default, but I added here to demonstrate how to use the funtion
  
  ShiftPWM.Start(pwmFrequency,maxBrightness);
 
}



void loop()
{    

}

void serialEvent(){
  bytes += Serial.available();
  comp.serialEvent();
}



