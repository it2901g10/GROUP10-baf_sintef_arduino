/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.ntnu.osnap.mockups.service;

import android.os.Bundle;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.SocialService;
import no.ntnu.osnap.social.models.Person;

/**
 * A mockup application to test the SocialService class.
 * @author Emanuele 'Lemrey' Di Santo
 */
public class MockupService extends SocialService {
	
	@Override
	public void onCreate() {
		super.onCreate();
		setServiceName("Mockup Network");
	}
	
	@Override
	protected Response handleRequest(Request request) {
		
		Person self = new Person();
		Response response = new Response();
		
		self.setField("displayName", "Marco");
		
		switch (request.getRequestCode()) {
			case SELF: {
				response.bundle(self);
			} break;
				
			default: {
				response.setStatus(Response.Status.NOT_SUPPORTED);
			}
		}
		
		return response;
	}
	
}
