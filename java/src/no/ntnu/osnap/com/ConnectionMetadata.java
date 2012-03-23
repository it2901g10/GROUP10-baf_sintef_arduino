/*
* Copyright 2012 Anders Eie, Henrik Goldsack, Johan Jansen, Asbjørn 
* Lucassen, Emanuele Di Santo, Jonas Svarvaa, Bjørnar Håkenstad Wold
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package no.ntnu.osnap.com;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;

public class ConnectionMetadata {
	private String name;
	private String address;
	private HashMap<Platform, URI> applicationDownloadLinks;
	private HashSet<String> servicesSupported;
	
	public interface Service {
		String name();
	}
	
	public interface Platform {
		String name();		
	}
	
	/**
	 * List of default platforms the remote device supports. This list can be extended by
	 * creating a new Enum that extends the Platform interface. The names of each enumeration must
	 * be unique.
	 */
	public enum DefaultPlatforms implements Platform {
		PLATFORM_DEFAULT,
		PLATFORM_LINUX,
		PLATFORM_WINDOWS,
		PLATFORM_ANDROID,
		PLATFORM_MACINTOSH
	}
	
	/**
	 * List of default services a remote device can support. This list can be extended by
	 * creating a new Enum that extends the Service interface. The names of each enumeration must
	 * be unique.
	 */
	public enum DefaultServices implements Service {
		SERVICE_LED_LAMP,
		SERVICE_SERVO_MOTOR,
		SERVICE_VIBRATION,
		SERVICE_LCD_SCREEN,
		SERVICE_TEMPERATURE_SESNOR
	}	
	
	/**
	 * Default constructor for a ConnectionMetadata object
	 * @param deviceName the human friendly name of this device
	 * @param deviceAddress an unique address specifier represented as a String (mac address, IP address, phone number, etc.)
	 * @param applicationDownloadLink a list of download links mapped to specific platforms
	 * @param services an arbitrary list of Services this device supports
	 */
	public ConnectionMetadata(String deviceName, String deviceAddress, HashMap<Platform, URI> applicationDownloadLink, Service[] services) {
		this.name = deviceName;
		this.applicationDownloadLinks = applicationDownloadLink;

		//Add each service to the list of supported services
		servicesSupported = new HashSet<String>();
		for(Service service : services){
			servicesSupported.add(service.name());
		}
		
		//Add download links
		this.applicationDownloadLinks = applicationDownloadLink;		
	}
	
	/**
	 * Gets the download link for the application for this device.
	 * @return URI of the default application
	 */
	public URI getDefaultApplicationDownloadLink(){
		return getApplicationDownloadLink(DefaultPlatforms.PLATFORM_DEFAULT);
	}
	
	/**
	 * Gets the download link for the application for this device.
	 * @param platform which platform the application is for
	 * @return URI of the application for this device
	 * @see DefaultPlatforms
	 */
	public URI getApplicationDownloadLink(Platform platform){
		return applicationDownloadLinks.get(platform);
	}
	
	/**
	 * 
	 * @param service which service is requested? 
	 * @return returns true if the specified service is supported by the remote device
	 * @see DefaultServices
	 */
	public boolean isServiceSupported(String service){
		return servicesSupported.contains(service);
	}
	
	/**
	 * Returns a String representation of the name of the remote device
	 * @return the human friendly name of this device
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a unique String representation of the address of the remote device.
	 * Could be an IP address, MAC address, phone number, frequency, etc.
	 * @return String representing the remote device location
	 */
	public String getAddress() {
		return address;
	}
}
