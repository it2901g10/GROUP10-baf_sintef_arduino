package source;


import java.io.InputStream;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComLayer implements SerialPortEventListener {

    SerialPort serialPort;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
    };
    /**
     * Buffered input stream from the port
     */
    private InputStream input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;
    
    /*
     *  0 = scanning
     *  1 = active
     */
    private int state = 0;
    
    private final byte[] ack = {(byte)0x00, (byte)0xFF};

    public ComLayer() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        // iterate through, looking for the port
        while (portEnum.hasMoreElements()) {
            portId = (CommPortIdentifier) portEnum.nextElement();
            // Test com port
            
            if (portId == null) {
                System.out.println("Could not find COM port.");
                continue;
            }

            try {
                // open serial port, and use class name for the appName.
                serialPort = (SerialPort) portId.open(this.getClass().getName(),
                        TIME_OUT);

                // set port parameters
                serialPort.setSerialPortParams(DATA_RATE,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                // open the streams
                input = serialPort.getInputStream();
                output = serialPort.getOutputStream();

                // add event listeners
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            } catch (Exception e) {
                continue;
            }
            
            System.out.print("COM port(" + portId.getName() + ") found, pinging for Arduino...");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
            }
            
            // Check for arduino
            sendMsg(ack);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            
            if (state == 0){
                System.out.println(" No response.");
                continue;
            }
            
            break;
        }
        
        if (state == 0){
            System.out.println("ERROR: No arduinos found");
            throw new IllegalArgumentException();
        }
        System.out.println(" Arduino found");
        System.out.println("Com port found: " + portId.getName());
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (state == 1) {
            if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                try {
                    int available = input.available();
                    byte chunk[] = new byte[available];
                    input.read(chunk, 0, available);

                    // Displayed results are codepage dependent
                    System.out.print(new String(chunk));
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        } 
        else {
            if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                try {
                    byte[] response = new byte[2];
                    input.read(response, 0, 2);
                    
                    if (response[0] == ack[0] && response[1] == ack[0]){
                        state = 1;
                    }
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public synchronized void sendMsg(String msg) {
        try {
            output.write(msg.getBytes());
        } catch (IOException e) {
            System.err.println("send error");
        }
    }
    
    public synchronized void sendMsg(byte[] bytes) {
        try {
            output.write(bytes);
        } catch (IOException e) {
            System.err.println("send error");
        }
    }
}