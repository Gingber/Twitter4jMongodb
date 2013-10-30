package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;


public class ReadTxtFile {
    private BufferedReader br ;
    
    public ReadTxtFile(String file)
    {
    	try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

	public Vector<String> read()
	{
		Vector<String> res=new Vector<String>(10);
		try {
			String t;
			int i=0;
			while((t=br.readLine())!=null){
				res.add(t);	
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return res;
	}
    public  Vector<String> ReadToVector( )
    {
    	Vector<String> strs=new Vector<String>();					
		try { 				
			String t;			
			while((t=br.readLine())!=null)
			{
				strs.add(t);					
			}
			//System.out.println("总共的种子数"+i+"个");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strs;
    }
    
	    
   public void close()
   {
	   try {
					
			br.close();								
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
}
