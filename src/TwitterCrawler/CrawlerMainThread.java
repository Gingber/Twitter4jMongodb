package TwitterCrawler;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.auth.Authorization;

public class CrawlerMainThread  implements Runnable
{
	List lt = new ArrayList<Thread>();
	static int count=7;
	public void run() 
	{
		for(int i = 1; i <=count; i++)
		{			
			/*OAuthTwitter oAuthTwitter=new OAuthTwitter("config/oauth"+i+".properties");
			Twitter twitter=oAuthTwitter.oAuthLogin();*/
			Twitter twitter = null;
			/*if(twitter == null)
				continue;*/			
						
			try
			{
				TwitterGrabThread t = new TwitterGrabThread(twitter, "config/oauth"+i+".properties", "config/minganci"+i+".txt");
				Thread thread = new Thread(t,"thread"+i);
				
				//启动一个线程开始抓取
				thread.start();
				lt.add(t);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				MyLogger.getInstance().log(e.toString());
			}
		
		}											
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		System.getProperties().put("http.proxyHost", "127.0.0.1");
        System.getProperties().put("http.proxyPort", "8580");
        
		CrawlerMainThread m = new CrawlerMainThread();
		new Thread(m).start();
	}

}
