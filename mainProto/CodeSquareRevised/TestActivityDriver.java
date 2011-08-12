import org.json.*;
import java.util.*;


/**
 * This class is only used to test the makeJsonPost() function. Do not import this class, just copy the function.
 * 
 * @author eric.ren
 *
 */
public class TestActivityDriver {


    /**
     * This method takes the list of badges that a person recently obtained
     * and converts the badge data into a nice json object to be posted to
     * the activity stream
     * @param badgesList A string list filled with the data of the recently obtained badges
     * @param name The name of the person who just received the badges
     * @return a String in JSON format that will be posted to the activity stream
     **/
    public static String makeJSONPost(List<Badge> badgesList, String name) throws Exception {
	String jsonString;
	JSONObject jsonObj = new JSONObject();
	JSONArray postAry = new JSONArray();
	
	JSONObject actorObj = new JSONObject();
	actorObj.put("title", name);
	
	for (Badge b : badgesList) {
	    JSONObject curPost = new JSONObject();
	    JSONObject curObject = new JSONObject();
	    
	 
	    String badgeTitle = b.getTitle();
	    
	    curPost.put("verb", "post");
	    //curPost.put("title", "__MSG_feed.title.format__");
	    curPost.put("title", "You received " + badgeTitle + "badge");
	    //curPost.put("body", "__MSG_feed.body.format__");
	    curPost.put("body", "Congratulations! You got a new badge!");
	    
	    curPost.put("actor", actorObj);

	    curObject.put("title", badgeTitle);
	    curObject.put("summary", b.getDesc());
	    curObject.put("mediaLink", b.getImgLink());
	    
	    curPost.put("object", curObject);
	    
	    postAry.put(curPost);
	}
	    
	jsonObj.put("items", postAry);
	jsonString = jsonObj.toString();
	return jsonString;
    }

    // Testing the function
    public static void main(String[] args) {
	List<Badge> bList = new ArrayList<Badge>();
	Badge b1, b2, b3;
	b1 = new Badge("commit 1", "this is a description", "1.png");
	b2 = new Badge("commit 50", "this is another description", "2.png");
	b3 = new Badge("commit 100", "this is yet another description", "3.png");

	bList.add(b1);
	bList.add(b2);
	bList.add(b3);
	
	try {
	    String result = TestActivityDriver.makeJSONPost(bList, "Eric Ren");
	    System.out.println(result);
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
}