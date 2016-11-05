import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Code2040 {
	
	private static final String token = "5b2cf80f36d6808fbd3d28e861b50f42";
	
	public static void main(String[] args) {
		String api_url = "http://challenge.code2040.org/api/register";
		step1(api_url);
		
		String receive_url = new String("http://challenge.code2040.org/api/reverse");
		String validate_url = new String("http://challenge.code2040.org/api/reverse/validate");
		step2(receive_url, validate_url);
		
		receive_url = new String ("http://challenge.code2040.org/api/haystack");
		validate_url = new String ("http://challenge.code2040.org/api/haystack/validate");
		step3(receive_url, validate_url);
		
		receive_url = new String ("http://challenge.code2040.org/api/prefix");
		validate_url = new String ("http://challenge.code2040.org/api/prefix/validate");
		step4(receive_url, validate_url);
		
		receive_url = new String ("http://challenge.code2040.org/api/dating");
		validate_url = new String ("http://challenge.code2040.org/api/dating/validate");
		step5(receive_url, validate_url);		
	}
	
	public static String connect(JSONObject json, String apiUrl){
		HttpURLConnection connection;
	
		try {
			//connect to server
			URL url = new URL (apiUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			
			//send the json
			OutputStream out = connection.getOutputStream();
			out.write(json.toString().getBytes("UTF-8"));
			out.flush();
			
			//receive input
			BufferedReader buffread = 
					new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			return buffread.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/* register */
	private static void step1(String url) {
		JSONObject json = new JSONObject();
		json.put("token", token);
		json.put("github", "https://github.com/dolapo-martins/code-2040");
		String response = connect(json, url);
		System.out.println(response);
	}
	
	/* Reverse a string */
	private static void step2(String fromUrl, String toUrl) {
		JSONObject json = new JSONObject();
		json.put("token", token);
		StringBuilder retString = new StringBuilder(connect(json, fromUrl));
		retString.reverse();
		json.put("string", retString.toString());
		
		String response = connect(json, toUrl);
		System.out.println(response);
	}
	
	/* Needle in a haystack */
	private static void step3(String fromUrl, String toUrl){
		JSONObject key_val, json = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONArray haystack;
		String needle = "";
	
		json.put("token", token);
		try {
			key_val = (JSONObject) parser.parse(connect(json, fromUrl));
			needle = (String) key_val.get("needle");
			haystack = (JSONArray) key_val.get("haystack");
		
			json.put("needle", haystack.indexOf(needle));
			System.out.println(connect(json, toUrl)); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/* Prefix */
	public static void step4(String fromUrl, String toUrl){
		JSONObject dictionary, json = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONArray allStrings;
		String prefix = "";
		ArrayList<String> no_prefix = new ArrayList<String>();
		
		json.put("token", token);
		try {
			dictionary = (JSONObject) parser.parse(connect(json, fromUrl));
			prefix = (String) dictionary.get("prefix");
			allStrings = (JSONArray) dictionary.get("array");
			
			for (Object str: allStrings) {
				if (!str.toString().startsWith(prefix)) {
					no_prefix.add(str.toString());
				}
			}
			
			json.put("array", no_prefix);
			System.out.println(connect(json, toUrl));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	/* The dating game */
	public static void step5(String fromUrl, String toUrl){
		JSONObject dictionary, json = new JSONObject();
		JSONParser parser = new JSONParser();
		Calendar date = new GregorianCalendar();
		String datestamp;
		Integer interval;
		
		json.put("token", token);
		try {
			dictionary = (JSONObject) parser.parse(connect(json, fromUrl));
			datestamp = (String) dictionary.get("datestamp");
			interval = Integer.valueOf(((Long) dictionary.get("interval")).intValue());
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	        
	        date.setTime(dateFormat.parse(datestamp));
			date.add(Calendar.SECOND, interval);
			json.put("datestamp", dateFormat.format(date.getTime()));
			System.out.println(connect(json, toUrl));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
}
