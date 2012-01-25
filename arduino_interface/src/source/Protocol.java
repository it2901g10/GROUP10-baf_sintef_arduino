package source;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author anders
 */
public class Protocol {
    private ComLayer board;
    
    private final byte OPCODE_PING      = 0;
    private final byte OPCODE_TEXT      = 1;
    private final byte OPCODE_SENSOR    = 2;
    private final byte OPCODE_PIN_T     = 3;
    private final byte OPCODE_PIN_R     = 4;
    private final byte OPCODE_PIN_W     = 5;
    private final byte OPCODE_RESET     = (byte) 0xFF;

    public Protocol() {
        board = new ComLayer();
    }
    
    public void print(String text){
        int size = text.length() + 3;
        
        byte output[] = new byte[size];
        
        output[0] = (byte)size;
        output[1] = OPCODE_TEXT;
        output[2] = 0;
        
        for (int i = 3; i < size; ++i){
            output[i] = text.getBytes()[i-3];
        }
        
        board.sendMsg(output);
    }
}
