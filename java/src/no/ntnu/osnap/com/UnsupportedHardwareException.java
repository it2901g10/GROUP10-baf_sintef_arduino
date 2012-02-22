package no.ntnu.osnap.com;

/**
 * Indiciates a problem with the hardware or missing hardware services 
 * requested by the software
 */
public class UnsupportedHardwareException extends Exception {
	private static final long serialVersionUID = 7361286372494041006L;
	
	public UnsupportedHardwareException(String detailMessage) {
		super(detailMessage);
	}
}
