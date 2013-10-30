package TwitterCrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.Map;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.PagableResponseList;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;
import util.ReadTxtFile;


//import twitter4j.Paging;
public class TwitterGrabThread implements Runnable
{
	private  String oauthProperties;
	private  String searchfileName;
	private static int num=5;
	
	private Vector<String> zhongziUser;	
	
	private Map<String,Integer> initMap=new HashMap<String,Integer>();
	private static Map<String,Integer> zhongziMap=new HashMap<String,Integer>();
		
	private PagableResponseList<User> userwithstatus=null;
	GetTwitterInfo getTwiInfo;			
	Twitter twitter = null;
	static MongoDBOperation MongoDBOpe;

	public TwitterGrabThread(Object twitter,String oauthProperty,String minganciFile) 
	{
		this.twitter = (Twitter)twitter;
		zhongziUser=new Vector<String>();		
		getTwiInfo=new GetTwitterInfo(this.twitter);
		/*toSqlStr=new InfoToSqlstr();*/
		MongoDBOpe = new MongoDBOperation();
		oauthProperties=oauthProperty;
		searchfileName=minganciFile;
	}
		
	/**
	 * 初始化
	 * @throws IOException 
	 */
	public Properties init() throws IOException
	{		
		Properties properties=new Properties();
		
			//properties.load(ClassLoader.getSystemResourceAsStream(fileName));
		File file= new File(oauthProperties);
		FileInputStream  myin  = new FileInputStream(file);	 
		properties.load(myin);
		return properties;
	}		
		
	public void Start() throws TwitterException, IOException
	{
		Properties properties = init();
		/*System.out.println(properties.getProperty("consumer.key"));
		System.out.println(properties.getProperty("consumer.secret"));
		System.out.println(properties.getProperty("access.token.key"));
		System.out.println(properties.getProperty("access.token.secret"));*/
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
	 	cb.setDebugEnabled(true)
	 	  .setOAuthConsumerKey(properties.getProperty("consumer.key"))
	 	  .setOAuthConsumerSecret(properties.getProperty("consumer.secret"))
	 	  .setOAuthAccessToken(properties.getProperty("access.token.key"))
	 	  .setOAuthAccessTokenSecret(properties.getProperty("access.token.secret"));
	 	  
	 	 /* .setOAuthConsumerKey("7ZVgfKiOvBDcDFpytRWSA")
	 	  .setOAuthConsumerSecret("JmeJVeym78arzmGthrDUshQyhkq6nWA9tWLUKxc")
	 	  .setOAuthAccessToken("321341780-Zy7LptVYBZBVvAeQ5GFJ4aKFw8sdqhWBnvA3pDuO")
	 	  .setOAuthAccessTokenSecret("foi8FnQCeN0J5cdwad05Q6d7dbytFayQn1ZOvmhF6Qc");*/
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
                    MongoDBOperation.InsertMongodb(dbObject);  
                    System.out.println(dbObject);  
                    //pr.count++;  
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
        ReadTxtFile readFile = new ReadTxtFile(searchfileName);						
		Vector<String> mingancis=readFile.ReadToVector();
		readFile.close();
        String[] TrackArray = new String[mingancis.size()];
        // fill the array from the vector
        mingancis.toArray(TrackArray); 
		FilterQuery filter = new FilterQuery(); 
		filter.track(TrackArray); 
		twitterStream.filter(filter);
	}
		
	public void run() 
	{		
		//System.out.println("run to run()");	
		if(zhongziMap!=null)
		{	
			try {
				MongoDBOpe.LinkMongodb();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
			try {
				Start();
			} catch (TwitterException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	// 开始执行抓取

			//MongoDBOpe.CloseMongodb();
			
		}
	}															
}
