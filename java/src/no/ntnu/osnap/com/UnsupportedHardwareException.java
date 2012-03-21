package no.ntnu.osnap.com;

/**
 * Indicates a problem with the hardware or missing hardware services 
 * requested by the software
 */
public class UnsupportedHardwareException extends Exception {
	private static final long serialVersionUID = 7361286372494041006L;
	
	/**
	 * Default constructor for a new UnsupportedHardwareException
	 * @param detailMessage a more detailed message with information on the exception
	 */
	public UnsupportedHardwareException(String detailMessage) {
		super(detailMessage);
	}
}
