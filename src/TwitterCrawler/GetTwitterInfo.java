package TwitterCrawler;

import java.util.List;
import java.util.LinkedList;
import twitter4j.PagableResponseList;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class GetTwitterInfo {
	Twitter twitter;
	int remian=100;
	static int sleepTime=8*1000;
	
	public GetTwitterInfo(Twitter twitter)
	{
		this.twitter=twitter;
	}
	
	/**
	 * sleep
	 */
	private void sleep( int time)
	{
		if(time <= 0 ) time = 500;
		try {
			Thread.currentThread().sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前剩余的api请求次数
	 */
    public void getSituation()
	{  
		try 
		{
			RateLimitStatus limit  = twitter.getRateLimitStatus().get("/application/rate_limit_status");
			this.remian = limit.getRemaining();
		    if(this.remian <= 50)
			 { 
				 System.out.println(" sleep 10 minute");
				 this.sleep(600000); 					
			 }
			 else
				 sleep(sleepTime);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

