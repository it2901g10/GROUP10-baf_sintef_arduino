package source;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author anders
 */
public class Protocol implements ComLayerListener {
    public static final byte OPCODE_PING      = 0;
    public static final byte OPCODE_TEXT      = 1;
    public static final byte OPCODE_SENSOR    = 2;
    public static final byte OPCODE_PIN_T     = 3;
    public static final byte OPCODE_PIN_R     = 4;
    public static final byte OPCODE_PIN_W     = 5;
    public static final byte OPCODE_RESPONSE  = (byte) 0xFE;
    public static final byte OPCODE_RESET     = (byte) 0xFF;
    
    private ComLayer board;
    private Command currentCommand;
    
    private Byte waitingForAck;

    public Protocol() {
        currentCommand = new Command();
        waitingForAck = null;
        board = new ComLayer(this);
    }
    
    public void print(String text){
        lock();
        int size = text.length() + 4;
        
        byte output[] = new byte[size];
        
        output[0] = (byte)0xFF;
        output[1] = (byte)(size-1);
        output[2] = OPCODE_TEXT;
        output[3] = 0; // Will eventually specify display
        
        for (int i = 4; i < size; ++i){
            output[i] = text.getBytes()[i-4];
        }
        
        waitingForAck = OPCODE_TEXT;
        
        board.sendMsg(output);
        release();
    }
    
    private boolean locked = false;
    private void lock(){
        while (locked){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                
            }
        }
        
        locked = true;
        
        while (waitingForAck != null){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
    private void release(){
        if (!locked){
            throw new IllegalStateException("Already released");
        }
        
        locked = false;
    }
    
    @Override
    public synchronized void byteReceived(byte data){
        if (currentCommand.byteReceived(data)){
            // Process command
            if (currentCommand.isAckFor(waitingForAck)){
                waitingForAck = null;
                currentCommand = new Command();
            }
            else {
                throw new IllegalArgumentException("Received something unexpected");
            }
            
            notify();
        }
    }

    @Override
    public synchronized void bytesReceived(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

class Command {
    
    private final byte START_BYTE       = (byte) 0xFF;
    
    private enum State {
        STATE_START,
        STATE_SIZE,
        STATE_OPCODE,
        STATE_FLAG,
        STATE_CONTENT,
        STATE_DONE
    }
    
    private State state;
    
    private byte size;
    private byte opcode;
    private byte flag;
    private byte[] content;
    
    private int contentCounter;
    
    public Command(){
        state = State.STATE_START;
        contentCounter = 0;
    }
    
    public boolean byteReceived(byte data){
        switch (state){
            case STATE_START:
                if (data == START_BYTE){
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
                if (contentCounter >= size - 3){
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
    
    public boolean isAckFor(byte command){
        return opcode == Protocol.OPCODE_RESPONSE &&
                flag == command;
    }
}
