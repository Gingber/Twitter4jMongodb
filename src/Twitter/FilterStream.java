/**
 * 
 */
package Twitter;

/**
 * @author Gingber
 *
 */
/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/**
 * <p>This is a code example of Twitter4J Streaming API - filter method support.<br>
 * Usage: java twitter4j.examples.stream.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class FilterStream {
    /**
     * Main entry of this application.
     *
     * @param args follow(comma separated user ids) track(comma separated filter terms)
     * @throws twitter4j.TwitterException
     */
	
	public DBCollection collection;  
    public Mongo mongo;  
    public int count = 1;  
      
    public void LinkMongodb() throws Exception {  
          
        /*  
         * Link Mongodb   
         * build a data named FourS2  
         * build a collection named Foursquare  
         *    
         */  
        mongo = new Mongo("localhost", 27017);  
        DB db = mongo.getDB("TwitterRT");  
        collection = db.getCollection("tweet2");  
        System.out.println("Link Mongodb!");  
    }  
	
    public static void main(String[] args) throws TwitterException {
    	
    	System.getProperties().put("http.proxyHost", "127.0.0.1");
    	System.getProperties().put("http.proxyPort", "8580");

       /* if (args.length < 1) {
            System.out.println("Usage: java twitter4j.examples.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]");
            System.exit(-1);
        }*/

        final FilterStream fs = new FilterStream();
        
    	try {
    		fs.LinkMongodb();
    	}  catch (Exception e) {
			e.printStackTrace();
		}  
    	
    	
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                String str = DataObjectFactory.getRawJSON(status);              
                try {  
                    //JSONObject nnstr = new JSONObject(newstr);  
                    DBObject dbObject =(DBObject)JSON.parse(str);  
                    fs.collection.insert(dbObject);  
                    //System.out.println(dbObject);  
                    fs.count++;  
                    if(fs.count>900000000) {  
                        fs.mongo.close();  
                        System.exit(0);  
                    }  
                }  catch (Exception e) {  
                    e.printStackTrace();  
                }   
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
     	cb.setDebugEnabled(true)
     	  .setOAuthConsumerKey("7ZVgfKiOvBDcDFpytRWSA")
     	  .setOAuthConsumerSecret("JmeJVeym78arzmGthrDUshQyhkq6nWA9tWLUKxc")
     	  .setOAuthAccessToken("321341780-Zy7LptVYBZBVvAeQ5GFJ4aKFw8sdqhWBnvA3pDuO")
     	  .setOAuthAccessTokenSecret("foi8FnQCeN0J5cdwad05Q6d7dbytFayQn1ZOvmhF6Qc");
     	cb.setJSONStoreEnabled(true);

     	TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());

        TwitterStream twitterStream = tf.getInstance();
        twitterStream.addListener(listener);
        ArrayList<Long> follow = new ArrayList<Long>();
        ArrayList<String> track = new ArrayList<String>();
        
        //String[] keywords = {"RT @justinbieber"};
        String[] keywords = {"27260086"}; // user_id(justinbieber)
        for (String arg : keywords) {
            if (isNumericalArgument(arg)) {
                for (String id : arg.split(",")) {
                    follow.add(Long.parseLong(id));
                }
            } else {
                track.addAll(Arrays.asList(arg.split(",")));
            }
        }
        long[] followArray = new long[follow.size()];
        for (int i = 0; i < follow.size(); i++) {
            followArray[i] = follow.get(i);
        }
        String[] trackArray = track.toArray(new String[track.size()]);

        // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        //twitterStream.filter(new FilterQuery(0, followArray, trackArray));
        twitterStream.filter(new FilterQuery(followArray));
    }

    private static boolean isNumericalArgument(String argument) {
        String args[] = argument.split(",");
        boolean isNumericalArgument = true;
        for (String arg : args) {
            try {
                Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                isNumericalArgument = false;
                break;
            }
        }
        return isNumericalArgument;
    }
}

