package source;

import java.io.IOException;

/**
 *
 * @author anders
 */
public interface ComLayerInterface {
    public void setListener(ComLayerListener listener);
    public void sendBytes(byte[] data) throws IOException;
}
