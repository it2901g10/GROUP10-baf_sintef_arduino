/*
* Copyright 2012 Anders Eie, Henrik Goldsack, Johan Jansen, Asbj�rn 
* Lucassen, Emanuele Di Santo, Jonas Svarvaa, Bj�rnar H�kenstad Wold
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

package no.ntnu.osnap.com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public abstract class Protocol extends Thread {
	/**
	 * The version number of this ComLib release
	 */
	public final static String LIBRARY_VERSION = "1.2.0";
	
	/**
	 * The unique metadata package for this connection
	 * This is initially set to null and need to be retrieved
	 * in getConnectionData() that is inherited by the connection
	 * implementation.
	 */
	protected ConnectionMetadata connectionMetadata;
	
	private BlockingQueue<ProtocolInstruction> pendingInstructions;
	private ProtocolInstruction currentInstruction;

	private boolean running;
    
	protected static final int TIMEOUT = 2000;
	protected static final int MAX_CONTENT_SIZE = 250;

	//Package private enumeration
    enum OpCode {
    	PING,			//0
    	TEXT,			//1
    	SENSOR,			//2
    	DATA,			//3
    	PIN_R,			//4
    	PIN_W,			//5
    	DEVICE_INFO,	//6
    	RESPONSE(0xFE),	//254
    	RESET(0xFF);	//255
    	
    	//Read only value
    	public final byte value;
    	
    	/** Implicit value constructor */
    	private OpCode(){
    		this.value = (byte) this.ordinal();
    	}
    	
    	/** Explicit value constructor */
    	private OpCode(int value) {
    		this.value = (byte) value;
    	}
    }
    
    private Command currentCommand;
    private OpCode waitingForAck;
    
    private static final byte[] ackProcessors = {
        OpCode.SENSOR.value,
		OpCode.PIN_R.value,
		OpCode.PING.value
    };
	
	private OpCode tempAckProcessor;

    public Protocol() {
        currentCommand = new Command();
        waitingForAck = null;
		pendingInstructions = new LinkedBlockingQueue<ProtocolInstruction>();
		tempAckProcessor = null;
		running = true;
    }
	
    public abstract ConnectionMetadata getConnectionData();
        
    /**
     * Retrieves a single long String of raw unprocessed list of services, platforms and download links supported by the remote device
     * @return a raw String representation of the device info retrieved from the remote device
     * @throws TimeoutException if the remote device used longer than Protocol.TIMEOUT milliseconds to respond
     */
	protected final String getDeviceInfo() throws TimeoutException {
		ProtocolInstruction newInstruction = new ProtocolInstruction(OpCode.DEVICE_INFO, (byte)0, new byte[1]);
		
		// Blocking methodlock();
		lock();

		waitingForAck = OpCode.DEVICE_INFO;
		tempAckProcessor = OpCode.DEVICE_INFO;

		try {
			sendBytes(newInstruction.getInstructionBytes());
		} catch (IOException ex) {
			System.out.println("Send fail");
			//Log.e(getClass().getName(), "Send byte failure: " + ex);	//TODO: should be this format (but only works on Android)
		}
		release();
		
		//Wait until we get a response or a timeout
		long time = System.currentTimeMillis();
		while (waitingForAck != null) {
			if (System.currentTimeMillis() - time > TIMEOUT)
				throw new TimeoutException(Thread.currentThread().getStackTrace()[2].getMethodName() + " has timed out");
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}
		
		tempAckProcessor = null;
		
        String response = new String( currentCommand.getContent() );

        ackProcessingComplete();

        return response;
	}    
	
	public void stopThread() {
		running = false;
	}
	
	@Override
	public void run(){
		while (running){
			synchronized (pendingInstructions) {
				while (pendingInstructions.isEmpty()){
					try {
						pendingInstructions.wait(1000);
					} catch (InterruptedException ex) {
					}
					
					if (!running) return; // Thread is stopped
				}
			}
			
			lock();
			
			currentInstruction = pendingInstructions.poll();
			
			try {
				sendBytes(currentInstruction.getInstructionBytes());
				
				System.out.println("Sent: " + currentInstruction.getOpcode());
				
				waitingForAck = currentInstruction.getOpcode();
			} catch (IOException ex) {
				// TODO: use logger
				System.out.println("Send derp");
			}
			
			release();
		}
	}
	
	private void queueInstruction(ProtocolInstruction instr){
		synchronized (pendingInstructions) {
			pendingInstructions.add(instr);
			System.out.println("Size: " + pendingInstructions.size());
			pendingInstructions.notify();
		}
	}
	
    public final void ping() throws TimeoutException {
        lock();
        
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OpCode.PING, (byte)0, new byte[1]);
		
        try {
            sendBytes(newInstruction.getInstructionBytes());
        } catch (IOException ex) {
            System.out.println("Derp send");
        }
        
        waitingForAck = OpCode.PING;
        
        release();
		
		long time = System.currentTimeMillis();
		while (waitingForAck != null) {
			if (System.currentTimeMillis() - time > TIMEOUT)
				throw new TimeoutException("Timeout");
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}

        ackProcessingComplete();
    }
    
	public final void print(String text) throws TimeoutException{
		print(text, false);
	}
	
	public final void print(String text, boolean blocking) throws TimeoutException{
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OpCode.TEXT, (byte)0, text.getBytes());
		if (!blocking){
			queueInstruction(newInstruction);
		}
		else {
			// Blocking methodlock();
			lock();

			waitingForAck = OpCode.TEXT;
			tempAckProcessor = OpCode.TEXT;

			try {
				sendBytes(newInstruction.getInstructionBytes());
			} catch (IOException ex) {
				System.out.println("Send fail");
				//Log.e(getClass().getName(), "Send byte failure: " + ex);	//TODO: should be this format (but only works on Android)
			}
			release();
			
			long time = System.currentTimeMillis();
			while (waitingForAck != null) {
				if (System.currentTimeMillis() - time > TIMEOUT)
					throw new TimeoutException("Timeout");
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {
				}
			}
			
			tempAckProcessor = null;

			ackProcessingComplete();
		}
	}

    public final int sensor(int sensor) throws TimeoutException {
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OpCode.SENSOR, (byte)sensor, new byte[1]);
		
        lock();

        waitingForAck = OpCode.SENSOR;

        try {
            sendBytes(newInstruction.getInstructionBytes());
        } catch (IOException ex) {
            System.out.println("Send fail");
        }

        release();
		
		long time = System.currentTimeMillis();
		while (waitingForAck != null) {
			if (System.currentTimeMillis() - time > TIMEOUT)
				throw new TimeoutException("Timeout");
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
			}
		}
		

        byte content[] = currentCommand.getContent();

        ackProcessingComplete();

        int sensorValue = (content[0] << 8) + toUnsigned(content[1]);

        return sensorValue;
    }

    public short toUnsigned(byte value) {
        if (value < 0) {
            return (short) ((short) value & (short) 0xFF);
        }
        return (short) value;
    }

    public final void data(int pin, byte[] data) throws TimeoutException {
		data(pin, data, false);
    }
	
	public final void data(int pin, byte[] data, boolean blocking) throws TimeoutException{
		ArrayList<ProtocolInstruction> newInstructions = new ArrayList<ProtocolInstruction>();
		
		//ProtocolInstruction tempInstruction;
		
		if (data.length > MAX_CONTENT_SIZE){
			byte[] tempBytes = new byte[MAX_CONTENT_SIZE];
			
			int restSize = 0;
			
			for (int i = 0; i < data.length; ++i){
				
				tempBytes[i % MAX_CONTENT_SIZE] = data[i];
				if (i % MAX_CONTENT_SIZE == 0){
					newInstructions.add(
						new ProtocolInstruction(OpCode.DATA, (byte)1, tempBytes)
						);
					
					if (data.length - i < MAX_CONTENT_SIZE){
						restSize = data.length - i;
					}
				}
			}
			
			byte[] restBytes = new byte[restSize];
			for (int i = 0; i < restSize; ++i){
				restBytes[i] = tempBytes[i];
			}
			newInstructions.add(
				new ProtocolInstruction(OpCode.DATA, (byte)0, restBytes)
				);
		}
		else {
			newInstructions.add(
				new ProtocolInstruction(OpCode.DATA, (byte)0, data)
				);
		}
		
		if (!blocking){
			for (ProtocolInstruction instr : newInstructions){
				queueInstruction(instr);
			}
		}
		else {

			lock();
			
			for (ProtocolInstruction newInstruction : newInstructions){

				waitingForAck = OpCode.DATA;

				try {
					sendBytes(newInstruction.getInstructionBytes());
				} catch (IOException ex) {
					System.out.println("Send fail");
				}

				long time = System.currentTimeMillis();
				while (waitingForAck != null) {
					if (System.currentTimeMillis() - time > TIMEOUT)
						throw new TimeoutException("Timeout");
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
					}
				}

				ackProcessingComplete();
			}
			
			release();
		}
	}

    public final boolean read(int pin) throws TimeoutException {
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OpCode.PIN_R, (byte)pin, new byte[1]);
		
        lock();

        waitingForAck = OpCode.PIN_R;

        try {
            sendBytes(newInstruction.getInstructionBytes());
        } catch (IOException ex) {
            System.out.println("Send fail");
        }
        release();
		
		long time = System.currentTimeMillis();
		while (waitingForAck != null) {
			if (System.currentTimeMillis() - time > TIMEOUT)
				throw new TimeoutException("Timeout");
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}
		

        byte content[] = currentCommand.getContent();

        ackProcessingComplete();

        return content[0] > 0 ? true : false;
    }

    public final void write(int pin, boolean value) throws TimeoutException {
		write(pin, value, false);
    }
	
	public final void write(int pin, boolean value, boolean blocking) throws TimeoutException{
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OpCode.PIN_W, (byte)pin, new byte[] {value ? (byte)1 : (byte)0});

		if (!blocking){
			queueInstruction(newInstruction);
		}
		else {
			lock();

			waitingForAck = OpCode.PIN_W;

			try {
				sendBytes(newInstruction.getInstructionBytes());
			} catch (IOException ex) {
				System.out.println("Send fail");
			}
			release();
			
			long time = System.currentTimeMillis();
			while (waitingForAck != null) {
				if (System.currentTimeMillis() - time > TIMEOUT)
					throw new TimeoutException("Timeout");
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {
				}
			}

			ackProcessingComplete();
		}
	}
	
    private volatile boolean locked = false;

    private synchronized void lock() {
        while (locked) {
            try {
                this.wait(1000);
            } catch (InterruptedException ex) {
            }
        }

        locked = true;
		
		while (waitingForAck != null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}
		
		while (processingAck){
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}
    }

    private synchronized void release() {
        if (!locked) {
            throw new IllegalStateException("Already released");
        }
        locked = false;
		this.notify();
    }
    private boolean processingAck = false;

    private void ackProcessing() {
        processingAck = true;
		
		waitingForAck = null;
		
		while (processingAck){
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {

			}
		}
    }

    private void ackProcessingComplete() {
        processingAck = false;
    }
    
    protected final void byteReceived(byte data) {
        if (currentCommand.byteReceived(data)) {
        	
            // Process command
            if (currentCommand.isAckFor(waitingForAck)) {
				System.out.println("Ack received for: " + waitingForAck);
                byte tempAck = waitingForAck.value;
                
				boolean hadAckProcessor = false;

				for (byte ack : ackProcessors){
					if (tempAck == ack){
						ackProcessing();
						hadAckProcessor = true;
						break;
					}
				}
				
				if (!hadAckProcessor){
					if (tempAckProcessor != null && tempAck == tempAckProcessor.value){
						ackProcessing();
					}
					else {
						waitingForAck = null;
					}
				}
				
                currentCommand = new Command();
            } else {
                throw new IllegalArgumentException("Received something unexpected");
            }
        }
    }

    protected final void bytesReceived(byte[] data) {
        for (byte item : data) {
            byteReceived(item);
        }
    }

    protected abstract void sendBytes(byte[] data) throws IOException;

    private enum State {
        STATE_START,
        STATE_SIZE,
        STATE_OPCODE,
        STATE_FLAG,
        STATE_CONTENT,
        STATE_DONE
    }
    
    private class Command {

        private final byte START_BYTE = (byte) 0xFF;

        private State state;
        private byte size;
        private byte opcode;
        private byte flag;
        private byte[] content;
        private int contentCounter;

        public Command() {
            state = State.STATE_START;
            contentCounter = 0;
        }

        public boolean byteReceived(byte data) {
            switch (state) {
                case STATE_START:
                    if (data == START_BYTE) {
                        state = State.STATE_SIZE;
                    }
                    break;
                case STATE_SIZE:
                    size = data;
                    content = new byte[size];
                    state = State.STATE_OPCODE;
                    break;
                case STATE_OPCODE:
                    opcode = data;
                    state = State.STATE_FLAG;
                    break;
                case STATE_FLAG:
                    flag = data;
                    state = State.STATE_CONTENT;
                    break;
                case STATE_CONTENT:
                    content[contentCounter++] = data;
                    if (contentCounter >= size - 3) {
                        state = State.STATE_DONE;
                        return true;
                    }
                    break;
                case STATE_DONE:
                    throw new IndexOutOfBoundsException("Command already finished");
                default:
                    break;
            }

            return false;
        }

        /*public byte getOpcode() {
            return opcode;
        }*/

        public byte[] getContent() {
            return content;
        }

        public boolean isAckFor(OpCode command) {
            return opcode == OpCode.RESPONSE.value && flag == command.value;
        }
    }

}

