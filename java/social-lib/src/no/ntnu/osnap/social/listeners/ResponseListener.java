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
package no.ntnu.osnap.social.listeners;

import no.ntnu.osnap.social.Response;

/**
 * An interface for SocialService incoming {@link Response} objects.
 
 * @author Emanuele 'lemrey' Di Santo
 */
public interface ResponseListener {
	/**
	 * Social service response callback function.
	 * @param response the response object returned by the social service
	 */
	void onComplete(Response response);
}
