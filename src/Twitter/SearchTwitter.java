package Twitter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class SearchTwitter {
    /**
     * Main entry of this application.
     *
     * @param args
     */
	public DBCollection collection;
	public Mongo mongo;
	public int count = 1;
	public static Twitter twitter = null;
	
	public void LinkMongodb() throws Exception {
		
		/*
		 * Link Mongodb 
		 * build a data named FourS2
		 * build a collection named Foursquare
		 *  
		 */
		mongo = new Mongo("localhost", 27017);
    	DB db = mongo.getDB("TwitterRT");
    	collection = db.getCollection("kittens");
    	System.out.println("Link Mongodb!");
	}
	
	private static void checkRateLimitStatus()  {
		
		/* try {
	            Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
	            for (String endpoint : rateLimitStatus.keySet()) {
	                RateLimitStatus status = rateLimitStatus.get(endpoint);
	                System.out.println("Endpoint: " + endpoint);
	                System.out.println(" Limit: " + status.getLimit());
	                System.out.println(" Remaining: " + status.getRemaining());
	                System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
	                System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());
	            }
	            System.exit(0);
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to get rate limit status: " + te.getMessage());
	            System.exit(-1);
	        }*/
		
		try {
		RateLimitStatus limit = twitter.getRateLimitStatus().get("/search/tweets");
		System.out.print("- limit: "+limit.getRemaining() +"\n");
		if (limit.getRemaining() <= 10) {
			int remainingTime = limit.getSecondsUntilReset() + 10;
			System.out.println("Twitter request rate limit reached. Waiting "+remainingTime/60+" minutes to request again.");
			
			try {
				Thread.sleep(remainingTime*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		} catch (TwitterException te) {
			System.err.println(te.getMessage());
			if (te.getStatusCode()==503) {
				try {
					Thread.sleep(120*1000);// wait 2 minutes
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			
		}
	}
	

    public static void main(String[] args) throws TwitterException, JSONException, InterruptedException {
    	
    	final TwitterSearch pr = new TwitterSearch();
    	
    	try {
    		pr.LinkMongodb();
    	} catch (Exception e) {
			e.printStackTrace();
		}  
    	
    	ConfigurationBuilder cb = new ConfigurationBuilder();
     	cb.setDebugEnabled(true)
     	  .setHttpProxyHost("127.0.0.1")
     	  .setHttpProxyPort(8580)
     	  .setAsyncNumThreads(10)
     	  .setGZIPEnabled(false)
     	  .setHttpConnectionTimeout(10000000)
     	  .setHttpReadTimeout(3600000)
     	  .setHttpRetryCount(100)
     	  .setUseSSL(false)
     	  .setOAuthConsumerKey("1wNzFR8mlvcS3DFX10O3w")
   	  	  .setOAuthConsumerSecret("V9w1vWHUIHf0jm7LA65l4os9OvYvx9S9R4AEFUs")
   	  	  .setOAuthAccessToken("1588047948-Y1uss1iZafQYnBuaOVY3jzPoEBHCgIfst7JbFDw")
   	  	  .setOAuthAccessTokenSecret("6XU1ehBD4BPFmJaGR0VHBcd1yqaaIsqwvQERZzA")
     	  .setJSONStoreEnabled(true);
     	
     	//TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
     	TwitterFactory tf = new TwitterFactory(cb.build());
     	//TwitterStream twitterStream = tf.getInstance();
     	//twitter = TwitterFactory.getSingleton();
     	twitter = tf.getInstance();
     	int num = 0;
     	while (true) {
	     	try {
     			 //Query query=new Query("why Obama look like he about to drop the best album of 2013?");	
        		 //Query query =new Query("The next journal. Sunday at midnight. #rollercoaster #musicmondays http://instagram.com/p/hCaYRZgvmW/");
        		 //Query query = new Query("If you can't make it to the Grove get #StayingStrong at http://bit.ly/StayingStrongBook "); 
        		 //Query query = new Query("Skaters be like \" you can\'t skate\". http://instagram.com/p/hC_qZvAvme/");
        		//Query query = new Query("Less than #4hours until #changeme");
        		Query query = new Query("KITTENS! Click here");
        		query.setCount(100);
	        	QueryResult result = twitter.search(query);
	   			 do {
	   				 checkRateLimitStatus();
	   				 try {
	   					 result = twitter.search(query);	  
	   				 } catch(TwitterException te) {
	   					 te.printStackTrace();
	   			         System.out.println("Failed to search tweets: " + te.getMessage());
	   			         //System.exit(-1);
	   					 continue;
	   				 }
	   				 List<Status> tweets = result.getTweets();			           
	   				 for (Status tweet : tweets) {
	   			     		//Status To JSON String
	   			     		String statusJson = DataObjectFactory.getRawJSON(tweet);
	   			     		//JSON String to JSONObject
	   			     		JSONObject JSON_complete = new JSONObject(statusJson);
	   			     		//We get tweet's text
	   			     		String JSON_text = JSON_complete.getString("text");
	   			     		//We get another JSONObject
	   			     		JSONObject JSON_user = JSON_complete.getJSONObject("user");
	   			     		//We get a field in the second JSONObject
	   			     		String screen_name = JSON_user.getString("screen_name");
	   			     		System.out.println(++num +"\t" + screen_name + "\t" + JSON_text);
	   			     		try {
	   			          	  DBObject dbObject =(DBObject)JSON.parse(statusJson);
	   			          	  pr.collection.save(dbObject);
	   			            } catch (Exception e) {
	   			    			e.printStackTrace();
	   			    		} 
	   				 }
	   			 } while ((query = result.nextQuery()) != null);		
				//System.exit(0);
			 } catch (TwitterException te) {
		            te.printStackTrace();
		            System.out.println("Failed to search tweets: " + te.getMessage());
		            //System.exit(-1);
			 }
     	}
     	
        //pr.mongo.close();     	
    }   
}