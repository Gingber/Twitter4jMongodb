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

package TwitterCrawler;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import com.mongodb.*;
import com.mongodb.util.JSON;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class PrintSampleStream {
    /**
     * Main entry of this application.
     *
     * @param args
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
    	DB db = mongo.getDB("Twitter_131026");
    	collection = db.getCollection("BarackObama");
    	System.out.println("Link Mongodb!");
	}
	

	final static PrintSampleStream pr = new PrintSampleStream();
	
    public static void main(String[] args) throws TwitterException {
    	
    	System.getProperties().put("http.proxyHost", "127.0.0.1");
        System.getProperties().put("http.proxyPort", "8580");
    	
    	//final PrintSampleStream pr = new PrintSampleStream();
    	
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
     	
     	TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

     	StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception ex) {
                // TODO Auto-generated method stub
            	ex.printStackTrace();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                // TODO Auto-generated method stub
            	System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                // TODO Auto-generated method stub
            	System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStatus(Status status) {
            	 //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());  
                //System.out.println(status);  
                String str = DataObjectFactory.getRawJSON(status);              
                try {  
                    //JSONObject nnstr = new JSONObject(newstr);  
                    DBObject dbObject =(DBObject)JSON.parse(str);  
                    pr.collection.insert(dbObject);  
                    System.out.println(dbObject);  
                    pr.count++;  
                   /* if(pr.count>300000) {  
                        pr.mongo.close();  
                        System.exit(0);  
                    }  */
                }  catch (Exception e) {  
                    e.printStackTrace();  
                }   
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                // TODO Auto-generated method stub
            	System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);

            }

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

        };
        twitterStream.addListener(listener);  
       
        //String[] Track = {"why Obama look like he about to drop the best album of 2013?"}; 
        String[] trackArray = {"Mac", "App", "Store"};  
        //trackArray[1] = ;  
        
        // Create a FilterQuery object and then set the items to track
        FilterQuery filter = new FilterQuery(); 
        
        // Create an array of items to track
        //String[] itemsToTrack = {"±¡ÎõÀ´"}; 
        // Set the items to track using FilterQuerys' track method.
        //filter.track(Track); 
        filter.track(trackArray); 
        
        // Assuming you have already created Twitter/TwitterStream object, 
        // use the filter method to start streaming using the FilterQuery object just created.
        twitterStream.filter(filter); 
        
       /* String[] username = {"katyperry"};
        twitterStream.addListener(userlistener);  
        twitterStream.user();*/
        //pr.mongo.close();
    }
    
    
    static UserStreamListener userlistener = new UserStreamListener() {
        @Override
        public void onStatus(Status status) {
        	String str = DataObjectFactory.getRawJSON(status);              
            try {  
                //JSONObject nnstr = new JSONObject(newstr);  
                DBObject dbObject =(DBObject)JSON.parse(str);  
                pr.collection.insert(dbObject);  
                System.out.println(dbObject);  
                pr.count++;  
                /*if(pr.count>300000) {  
                    pr.mongo.close();  
                    System.exit(0);  
                } */ 
            }  catch (Exception e) {  
                e.printStackTrace();  
            }   
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {
            System.out.println("Got a direct message deletion notice id:" + directMessageId);
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
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
        public void onFriendList(long[] friendIds) {
            System.out.print("onFriendList");
            for (long friendId : friendIds) {
                System.out.print(" " + friendId);
            }
            System.out.println();
        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
            System.out.println("onFavorite source:@"
                    + source.getScreenName() + " target:@"
                    + target.getScreenName() + " @"
                    + favoritedStatus.getUser().getScreenName() + " - "
                    + favoritedStatus.getText());
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
            System.out.println("onUnFavorite source:@"
                    + source.getScreenName() + " target:@"
                    + target.getScreenName() + " @"
                    + unfavoritedStatus.getUser().getScreenName()
                    + " - " + unfavoritedStatus.getText());
        }

        @Override
        public void onFollow(User source, User followedUser) {
            System.out.println("onFollow source:@"
                    + source.getScreenName() + " target:@"
                    + followedUser.getScreenName());
        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {
            System.out.println("onDirectMessage text:"
                    + directMessage.getText());
        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
            System.out.println("onUserListMemberAddition added member:@"
                    + addedMember.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
            System.out.println("onUserListMemberDeleted deleted member:@"
                    + deletedMember.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
            System.out.println("onUserListSubscribed subscriber:@"
                    + subscriber.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
            System.out.println("onUserListUnsubscribed subscriber:@"
                    + subscriber.getScreenName()
                    + " listOwner:@" + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {
            System.out.println("onUserListCreated  listOwner:@"
                    + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {
            System.out.println("onUserListUpdated  listOwner:@"
                    + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {
            System.out.println("onUserListDestroyed  listOwner:@"
                    + listOwner.getScreenName()
                    + " list:" + list.getName());
        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {
            System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
        }

        @Override
        public void onBlock(User source, User blockedUser) {
            System.out.println("onBlock source:@" + source.getScreenName()
                    + " target:@" + blockedUser.getScreenName());
        }

        @Override
        public void onUnblock(User source, User unblockedUser) {
            System.out.println("onUnblock source:@" + source.getScreenName()
                    + " target:@" + unblockedUser.getScreenName());
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
            System.out.println("onException:" + ex.getMessage());
        }
    };
       
}
