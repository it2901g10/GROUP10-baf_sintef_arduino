/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

/**
 *
 * @author anders
 */
public interface ComLayerListener {
    public void byteReceived(byte data);
    public void bytesReceived(byte[] data);
}
