package no.ntnu.osnap.com;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.TimeoutException;

public abstract class Protocol extends Thread {
	private ArrayDeque<ProtocolInstruction> pendingInstructions;
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

    public Protocol() {
        currentCommand = new Command();
        waitingForAck = null;
		pendingInstructions = new ArrayDeque<ProtocolInstruction>();
		running = true;
    }
	
	@Override
	public void run(){
		while (running && !interrupted()){
			synchronized (pendingInstructions) {
				while (pendingInstructions.isEmpty()){
					try {
						pendingInstructions.wait(1000);
					} catch (InterruptedException ex) {
					}
				}
			}
			
			lock();
			
			currentInstruction = pendingInstructions.pop();
			
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
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_TEXT, (byte)0, text.getBytes());
		
		queueInstruction(newInstruction);
	}

    public final int sensor(int sensor) {
        lock();
        int size = 5;

        byte output[] = new byte[size];

        output[0] = (byte) 0xFF;
        output[1] = (byte) (size - 1);
        output[2] = OPCODE_SENSOR;
        output[3] = (byte) sensor;
        output[4] = (byte) 0;

        waitingForAck = OPCODE_SENSOR;

        try {
            sendBytes(output);
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
		ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_PIN_PULSE, (byte)pin, new byte[1]);
		
		queueInstruction(newInstruction);
    }

    public final boolean read(int pin) {
        lock();
        int size = 5;

        byte output[] = new byte[size];

        output[0] = (byte) 0xFF;
        output[1] = (byte) (size - 1);
        output[2] = OPCODE_PIN_R;
        output[3] = (byte) pin;
        output[4] = (byte) 0;

        waitingForAck = OPCODE_PIN_R;

        try {
            sendBytes(output);
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
        ProtocolInstruction newInstruction =
				new ProtocolInstruction(OPCODE_PIN_W, (byte)pin, new byte[] {value ? (byte)1 : (byte)0});
		
		queueInstruction(newInstruction);
    }
    private boolean locked = false;

    private void lock() {
        while (locked) {
            try {
                Thread.sleep(10);
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
		
    }

    private void release() {
        if (!locked) {
            throw new IllegalStateException("Already released");
        }
        locked = false;
    }
    private Boolean processingAck = false;

    private void ackProcessing() {
        processingAck = true;
		
		synchronized (processingAck) {
			while (processingAck){
				try {
					processingAck.wait(10);
				} catch (InterruptedException ex) {

				}
			}
		}
    }

    private void ackProcessingComplete() {
        processingAck = false;
    }
    
    protected final synchronized void byteReceived(byte data) {
        if (currentCommand.byteReceived(data)) {
            // Process command
            if (currentCommand.isAckFor(waitingForAck)) {
				System.out.println("Ack received for: " + waitingForAck);
                byte tempAck = waitingForAck;
                
				
				waitingForAck = null;
				
                for (byte ack : ackProcessors){
                    if (tempAck == ack){
                        ackProcessing();
                        break;
                    }
                }
                currentCommand = new Command();
            } else {
                throw new IllegalArgumentException("Received something unexpected");
            }
        }
    }

    protected final synchronized void bytesReceived(byte[] data) {
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

