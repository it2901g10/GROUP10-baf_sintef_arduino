/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.mockups.service;

import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.SocialService;
import no.ntnu.osnap.social.models.Person;

/**
 *
 * @author lemrey
 */
public class MockupService extends SocialService {
	
	@Override
	protected Response handleRequest(Request request) {
		
		Person self = new Person();
		Response response = new Response();
		
		self.setField("displayName", "Marco");
		
		switch (request.getRequestCode()) {
			case SELF: {
				response.bundle(self);
			} break;
		}
		
		return response;
	}
	
}
