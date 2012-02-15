package no.ntnu.osnap.com;

public class UnsupportedHardwareException extends Exception {
	private static final long serialVersionUID = 7361286372494041006L;
	
	public UnsupportedHardwareException(String message) {
		super(message);
	}
}
