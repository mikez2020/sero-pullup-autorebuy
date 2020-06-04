package com.serofans.autorebuy;

import java.io.IOException;
import java.math.BigDecimal;

import org.json.JSONObject;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoRebuy {
	
	private static Logger logger; 
	private final static String loggingPropertiesPath = "c:/autorebuy/logging.properties";
	private final static String logFileName = "c:/autorebuy/autorebuy.log";
	private final static String token = "#REBUY#";
	
	public static void logInit()
	{
		// Set logging.properties (default:jre/lib/). Use System.setProperty instead of VM parameter -D
		System.setProperty("java.util.logging.config.file", loggingPropertiesPath);
		
		// Set logger, must after set logging.properties, then the settings will take effect.
		logger = Logger.getLogger("MyLogger");
		
		// Add handler
        try
        {
	        FileHandler fileHandler = new FileHandler(logFileName); 
	        fileHandler.setLevel(Level.INFO);
	        logger.addHandler(fileHandler);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	public static String HttpPostWithJson(String url, String json) {
		String returnValue = "Failed";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try{
			// Step1: create HttpClient object
		    httpClient = HttpClients.createDefault();
		 	
		 	// Step2: create HttpPost object
	        HttpPost httpPost = new HttpPost(url);
	        
	        // Step3: set JSON parameter for httpPost
	        StringEntity requestEntity = new StringEntity(json,"utf-8");
	        requestEntity.setContentEncoding("UTF-8");    	        
	        httpPost.setHeader("Content-type", "application/json");
	        httpPost.setEntity(requestEntity);
	       
	        // Step 4: send httpPost, get return value
	       returnValue = httpClient.execute(httpPost, responseHandler);
	      
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		finally {
	       try {
		       httpClient.close();
		   } catch (IOException e) {
			    e.printStackTrace();
		   }
	    }
		
		return returnValue;
	}
	
	public static int buyShare(String from, String pool, String amount)
	{
	    String postBody = "{\"base\": {\"timestamp\":1234567,\"sign\":\"67ff54447b89f06fe4408b89902e585167abad291ec41118167017925e24e320\"},\"biz\": {" +
		"\"From\": " + "\"" + from + "\", " + 
		"\"Vote\": \"\", " +
		"\"Password\": " + "\"" + token + "\", " +
		"\"Pool\": " + "\"" + pool + "\", " + 
		"\"Amount\": " + "\"" + amount + "\", " +
		"\"GasPrice\": \"1000000000\"},\"page\":{}}";
		System.out.println("Post body: " + postBody);
		
		String returnValue = HttpPostWithJson("http://127.0.0.1:2345/stake/buyShare", postBody);
		String jsonString = returnValue;
		
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject jsonObjectBase = jsonObject.getJSONObject("base");
		String code = jsonObjectBase.getString("code");
		String desc = jsonObjectBase.getString("desc");
		System.out.println("Return code: " + code + ", desc: " + desc);
		
		if (code.equals("SUCCESS"))
		{
			return 0;
		}
		else
		{
			return 1;
		}
		
		
	}

	public static void main(String[] args)
    {
		if (args.length != 4)
		{
			System.out.println("Wrong argument count, expected four: from, pool, interval, threshold!");
			return;
		}
		
		String from = args[0];
		String pool = args[1];
		int intervalMinutes = Integer.valueOf(args[2]);
		int thresholdAmt =  Integer.valueOf(args[3]);
		System.out.println("from=" + from);
		System.out.println("pool=" + pool);
		System.out.println("intervalMinutes=" + intervalMinutes);
		System.out.println("thresholdAmt=" + thresholdAmt);
		
		logInit();
				
		while (true)
		{
			/* get sero account balance */
			
			String returnValue = HttpPostWithJson("http://127.0.0.1:2345/account/balance", "{\"base\": {\"timestamp\":1234567,\"sign\":\"67ff54447b89f06fe4408b89902e585167abad291ec41118167017925e24e320\"},\"biz\": {\"PK\":\"" + from + "\"},\"page\":{}}");
			String jsonString = returnValue;
			//System.out.println("jsonString: " + jsonString);
			
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject jsonObjectBiz = jsonObject.getJSONObject("biz");
			JSONObject jsonObjectBalance = jsonObjectBiz.getJSONObject("balance");
			BigDecimal originalbalance = jsonObjectBalance.getBigDecimal("SERO");
			BigDecimal balance = originalbalance.divide(new BigDecimal("1000000000000000000"));
			System.out.println("SERO balance for " + from.substring(0, 32) +"... : " + balance.intValue());
			
			/* buy share */
			
			try
			{
				if (balance.intValue() >= thresholdAmt)
				{
					BigDecimal buyAmount = new BigDecimal(Integer.valueOf(balance.intValue()).toString()).multiply(new BigDecimal("1000000000000000000"));
					int r = buyShare(from, pool, buyAmount.toString());
					if (0 == r)
					{
					    logger.info("buy using " + balance.intValue());
					}
					else
					{
						logger.info("buy share failed! ");
					}
				}				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					Thread.sleep(intervalMinutes*60*1000);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			
		}
        
        
    }

}
