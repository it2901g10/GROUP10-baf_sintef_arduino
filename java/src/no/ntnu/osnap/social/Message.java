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

package no.ntnu.osnap.social;

import android.util.Log;

//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import java.util.ArrayList;
//import java.net.URL;
//import java.net.MalformedURLException;


/**
 * Represents a message.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Message extends Model {
	
	/** Constructs a Message from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Message (String json) throws JSONException {
		super(json);
	}
	
	/** Constructs a Message from a {@code JSONObject} instance.
	 * 
	 * @param object a JSONObject
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */	
	public Message (JSONObject object) throws JSONException {
		super(object);
	}
	
	/** Gets the sender of this post.
	 * 
	 * @return the value of the key 'from' as a {@code JSONObject}
	 * or {@code null} if the key doesn't exist.
	 */
	private JSONObject getSender() {
		JSONObject ret;
		ret = optJSONObject("from");
		
		// raise an exception maybe?
		if (ret == null) {
			Log.d(APP_TAG, "key 'from' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the sender of this post as a Person
	 * 
	 * @return the value of the key 'from' as a {@link Person}
	 * or {@code null} if the key doesn't exist.
	 * @see Person
	 */
	public Person getSenderAsPerson() {
		Person p = null;
		try {
			p = new Person(getSender());
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return p;
	}
	
	/** Gets the text of this message as a string.
	 * 
	 * @return the value of the key 'message' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getText() {
		String ret;
		ret = optString("message");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'message' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the comments made on this post.
	 * 
	 * @return the value of the key 'data' in the key 'comments'
	 * as a {@code JSONArray} or {@code null} if the key doesn't exist.
	 */
	/*public JSONArray getComments() {
		JSONArray ret;
		ret = optJSONObject("comments").optJSONArray("data");
		if (ret == null) {
			Log.d(APP_TAG, "key 'comments' doesn't exist.");
		}
		return ret;
	}*/
	
	/** Gets the people who liked this post.
	 * 
	 * @return the value of the key 'likes' as a {@code JSONArray}
	 * or {@code null} if the key doesn't exist.
	 */	
	/*public JSONArray getLikes() {
		JSONArray ret;
		ret = optJSONObject("likes").optJSONArray("data");
		if (ret == null) {
			Log.d(APP_TAG, "key 'likes' doesn't exist.");
		}
		return ret;
	}*/
	
	/** Gets the number of likes for this post.
	 * 
	 * @return 
	 */
	/*public int getLikesCount() {
		return getLikes().length();
	}*/
	
	/** Returns a list of comments to this post.
	 * 
	 * @return the value of the key 'data' in the key 'comments'
	 * as an {@code ArrayList} of {@link Comment} or {@code null}
	 * if the key doesn't exist.
	 * @see Comment
	 */
	/*public ArrayList<Comment> getCommentsAsList() {
		Comment c;
		JSONArray comments = getComments();
		ArrayList<Comment> ret = new ArrayList<Comment>();
		try {
			for (int i =  0; i < comments.length(); i++) {
				c = new Comment(comments.getJSONObject(i));
				ret.add(c);
			}			
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return ret;
	}*/
	
	/** Gets the list of people who liked this post.
	 * 
	 * @return the value of the key 'likes' as an {@code ArrayList}
	 * of {@link Person} or {@code null} if the key doesn't exist.
	 * @see Person
	 */	
	/*public ArrayList<Person> getLikesAsList() {
		Person p;
		JSONArray likes = getLikes();
		ArrayList<Person> ret = new ArrayList<Person>();
		try {
			for (int i =  0; i < likes.length(); i++) {
				p = new Person(likes.getJSONObject(i));
				ret.add(p);
			}			
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return ret;
	}*/
	
	/** Gets the link associated with this post.
	 * 
	 * @return the value of the key 'link' as a {@code URL} or
	 * {@code null} if the key doesn't exist.
	 */
	/*public URL getLink() {
		URL link = null;
		try {
			link = new URL(optString("link"));
		} catch (MalformedURLException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return link;
	}*/
	
	/** Gets the picture associated with this post.
	 * 
	 * @return the value of the key 'link' as a {@code URL} or
	 * {@code null} if the key doesn't exist.
	 */
	/*public URL getPictureURL() {
		URL link = null;
		try {
			link = new URL(optString("link"));
		} catch (MalformedURLException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return link;
	}*/
}
