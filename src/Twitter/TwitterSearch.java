package Twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
//import twitter4j.Tweet;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import java.util.List;
import java.util.Map;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class TwitterSearch {
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
    	DB db = mongo.getDB("TwitterMe");
    	collection = db.getCollection("DreamD");
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
	

    public static void main(String[] args) throws TwitterException {
    	
    	System.getProperties().put("http.proxyHost", "127.0.0.1");
        System.getProperties().put("http.proxyPort", "8580");
        
    	final TwitterSearch pr = new TwitterSearch();
    	
    	try {
    		pr.LinkMongodb();
    	}  catch (Exception e) {
			e.printStackTrace();
		}  
    	
    	ConfigurationBuilder cb = new ConfigurationBuilder();
     	cb.setDebugEnabled(true)
     	  .setOAuthConsumerKey("7ZVgfKiOvBDcDFpytRWSA")
     	  .setOAuthConsumerSecret("JmeJVeym78arzmGthrDUshQyhkq6nWA9tWLUKxc")
     	  .setOAuthAccessToken("321341780-Zy7LptVYBZBVvAeQ5GFJ4aKFw8sdqhWBnvA3pDuO")
     	  .setOAuthAccessTokenSecret("foi8FnQCeN0J5cdwad05Q6d7dbytFayQn1ZOvmhF6Qc");
     	cb.setJSONStoreEnabled(true);
     	
     	//TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
     	TwitterFactory tf = new TwitterFactory(cb.build());
     	//TwitterStream twitterStream = tf.getInstance();
     	twitter = tf.getInstance();
     	int num = 0;
     	try
		 {
			 Query query=new Query("why Obama look like he about to drop the best album of 2013?");	
			 query.setCount(100);
			 QueryResult result;
			 do{
				 checkRateLimitStatus();
				 result=twitter.search(query);	           
				 List<Status> tweets = result.getTweets();			           
				 for (Status tweet : tweets) {
			     		System.out.println(++num + "\t" + tweet);
			     		String str = DataObjectFactory.getRawJSON(tweet);
			     		System.out.println(str);
			     		try {
			          	  DBObject dbObject =(DBObject)JSON.parse(str);
			          	  pr.collection.save(dbObject);
			            } catch (Exception e) {
			    			e.printStackTrace();
			    		} 
				 }	
			 }while((query = result.nextQuery()) != null);
		 }
		catch(Exception e)
		{
			e.printStackTrace();	
		}	
     	
        pr.mongo.close();     	
    }   
}