package no.ntnu.osnap.com;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public abstract class Protocol implements Runnable {
	/**
	 * The version number of this ComLib release
	 */
	public final static String LIBRARY_VERSION = "1.0.2";
	
	/**
	 * The unique metadata package for this connection
	 * This is initially set to null and need to be retrieved
	 * in getConnectionData() that is inherited by the connection
	 * implementation.
	 */
	protected ConnectionMetadata connectionMetadata;
	
	private LinkedList<ProtocolInstruction> pendingInstructions;
	private ProtocolInstruction currentInstruction;

	public boolean running;
    
	protected static final int PING_TIMEOUT = 2000;
	
    public static final byte OPCODE_PING = 0;
    public static final byte OPCODE_TEXT = 1;
    public static final byte OPCODE_SENSOR = 2;
    public static final byte OPCODE_PIN_PULSE = 3;
    public static final byte OPCODE_PIN_R = 4;
    public static final byte OPCODE_PIN_W = 5;
    public static final byte OPCODE_RESPONSE = (byte) 0xFE;
    public static final byte OPCODE_RESET = (byte) 0xFF;
    
    private Command currentCommand;
    private Byte waitingForAck;
    
    private static final byte[] ackProcessors = {
        OPCODE_SENSOR,
		OPCODE_PIN_R,
		OPCODE_PING
    };
	
	private Byte tempAckProcessor;

    public Protocol() {
        currentCommand = new Command();
        waitingForAck = null;
		pendingInstructions = new LinkedList<ProtocolInstruction>();
		tempAckProcessor = null;
		running = true;
    }
	
    public abstract ConnectionMetadata getConnectionData();
	
	@Override
	public void run(){
		while (running){
			synchronized (pendingInstructions) {
				while (pendingInstructions.isEmpty()){
					try {
						pendingInstructions.wait(1000);
					} catch (InterruptedException ex) {
					}
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
				new ProtocolInstruction(OPCODE_PING, (byte)0, new byte[1]);
		
        try {
            sendBytes(newInstruction.getInstructionBytes());
        } catch (IOException ex) {
            System.out.println("Derp send");
        }
        
        waitingForAck = OPCODE_PING;
        
        release();
		
		long time = System.currentTimeMillis();
		while (waitingForAck != null) {
			if (System.currentTimeMillis() - time > PING_TIMEOUT)
				throw new TimeoutException("Ping timed out");
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}

        ackProcessingComplete();
    }
    
	public final void print(String text){
		print(text, false);
	}
	
	public final void print(String text, boolean blocking){
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_TEXT, (byte)0, text.getBytes());
		if (!blocking){
			queueInstruction(newInstruction);
		}
		else {
			// Blocking methodlock();
			lock();

			waitingForAck = OPCODE_TEXT;
			tempAckProcessor = OPCODE_TEXT;

			try {
				sendBytes(newInstruction.getInstructionBytes());
			} catch (IOException ex) {
				System.out.println("Send fail");
			}
			release();
			
			while (waitingForAck != null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {
				}
			}
			
			tempAckProcessor = null;

			ackProcessingComplete();
		}
	}

    public final int sensor(int sensor) {
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_SENSOR, (byte)sensor, new byte[1]);
		
        lock();

        waitingForAck = OPCODE_SENSOR;

        try {
            sendBytes(newInstruction.getInstructionBytes());
        } catch (IOException ex) {
            System.out.println("Send fail");
        }

        release();
		
		while (waitingForAck != null) {
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

    public final void pulse(int pin) {
		pulse(pin, false);
    }
	
	public final void pulse(int pin, boolean blocking){
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_PIN_PULSE, (byte)pin, new byte[1]);
		
		if (!blocking){
			queueInstruction(newInstruction);
		}
		else {

			lock();

			waitingForAck = OPCODE_PIN_PULSE;

			try {
				sendBytes(newInstruction.getInstructionBytes());
			} catch (IOException ex) {
				System.out.println("Send fail");
			}
			release();

			while (waitingForAck != null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {
				}
			}

			ackProcessingComplete();
		}
	}

    public final boolean read(int pin) {
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_PIN_R, (byte)pin, new byte[1]);
		
        lock();

        waitingForAck = OPCODE_PIN_R;

        try {
            sendBytes(newInstruction.getInstructionBytes());
        } catch (IOException ex) {
            System.out.println("Send fail");
        }
        release();
		
		while (waitingForAck != null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			}
		}
		

        byte content[] = currentCommand.getContent();

        ackProcessingComplete();

        return content[0] > 0 ? true : false;
    }

    public final void write(int pin, boolean value) {
		write(pin, value, false);
    }
	
	public final void write(int pin, boolean value, boolean blocking){
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_PIN_W, (byte)pin, new byte[] {value ? (byte)1 : (byte)0});

		if (!blocking){
			queueInstruction(newInstruction);
		}
		else {
			lock();

			waitingForAck = OPCODE_PIN_W;

			try {
				sendBytes(newInstruction.getInstructionBytes());
			} catch (IOException ex) {
				System.out.println("Send fail");
			}
			release();

			while (waitingForAck != null) {
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
                this.wait();
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
                byte tempAck = waitingForAck;
                
				boolean hadAckProcessor = false;

				for (byte ack : ackProcessors){
					if (tempAck == ack){
						ackProcessing();
						hadAckProcessor = true;
						break;
					}
				}
				
				if (!hadAckProcessor){
					if (tempAckProcessor != null && tempAck == tempAckProcessor){
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

        public byte getOpcode() {
            return opcode;
        }

        public byte[] getContent() {
            return content;
        }

        public boolean isAckFor(byte command) {
            return opcode == Protocol.OPCODE_RESPONSE
                    && flag == command;
        }
    }

}

