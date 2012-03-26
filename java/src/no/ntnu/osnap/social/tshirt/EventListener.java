package no.ntnu.osnap.social.tshirt;

public interface EventListener {
	public void serviceConnected(String name);
	public void serviceDisconnected(String name);
}