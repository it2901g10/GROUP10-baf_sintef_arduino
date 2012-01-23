
import java.util.Iterator;
import java.util.List;

//import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
//import com.restfb.Parameter;
import com.restfb.types.FacebookType;
//import com.restfb.json.*;



public class test {

	/**a
	 * @param args
	 */
	
	private final FacebookClient facebookClient;
	private final String ac = "AAACEdEose0cBAH6ZCoKRtptYHJT9HJjCNtzTHaKzx7nFYoU1LcbTIkWMhg8unOCHgJyEEfedGkW10TRqrjRwZC7oK1jMPzUKRZA6mK2mZAECpcODtIJu";
	
	public static void main(String[] args) {

		test t = new test();
		
	}
	
	private static class Notification extends FacebookType { 
		
		@Facebook 
		private String title_text;
		
		@Facebook
		private String body_text;

		public String getTitle() { return title_text; }
		public String getBody()  { return body_text; }
	
	} 
	
	test () {
		
		String query;
		int count = 0;
		Notification n = null;
		List<Notification> nlist = null;
		
		facebookClient = new DefaultFacebookClient(ac);
		
		/*System.out.println("Writing something on your wall..");
			
		FacebookType publishMessageResponse = facebookClient.publish("me/feed",
			FacebookType.class, Parameter.with("message", "Weeeeeeee! :D"));
		
		System.out.println("Published message ID: " + publishMessageResponse.getId());*/
		
		
		query = "SELECT title_text, body_text FROM notification " +
				"WHERE recipient_id = me() AND is_unread = 1 AND is_hidden = 0";
		
		try{
			//Execute query
			nlist = facebookClient.executeQuery(query, Notification.class);
			count = nlist.size();
		
		} catch (Exception e) {
			System.out.println("Query execution failed.");
			System.out.println(e.toString());
		}
		
		
		System.out.println("You have " + count + " unread notification!");
		
		if (count > 0) {			
			Iterator<Notification> i = nlist.iterator();
			
			while (i.hasNext()) {
				n = i.next();
				System.out.println("\nnotification data: " + n.getTitle());
				System.out.println(n.getBody());
			} 
		}
	}

}
