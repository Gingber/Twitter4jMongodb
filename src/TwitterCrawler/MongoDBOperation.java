/**
 * 
 */
package TwitterCrawler;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * @author hadoop
 *
 */
public class MongoDBOperation {
	
	private static DBCollection collection;
	private Mongo mongo;
	private int count = 1;
	

	public void LinkMongodb() throws Exception {
		
		/*
		 * Link Mongodb 
		 * build a data named FourS2
		 * build a collection named Foursquare
		 *  
		 */
		mongo = new Mongo("localhost", 27017);
    	DB db = mongo.getDB("Twitter_131030");
    	collection = db.getCollection("Topic");
    	System.out.println("Link Mongodb!");
	}
	
	public void CloseMongodb() throws Exception {
		
		/*
		 * Link Mongodb 
		 * build a data named FourS2
		 * build a collection named Foursquare
		 *  
		 */
		mongo.close();
    	
	}
	
	public static void InsertMongodb(DBObject dbObject) throws Exception {
		
		/*
		 * Link Mongodb 
		 * build a data named FourS2
		 * build a collection named Foursquare
		 *  
		 */
		collection.insert(dbObject);
    	
	}

	/**
	 * 
	 */
	public MongoDBOperation() {
		// TODO Auto-generated constructor stub
	}

}
