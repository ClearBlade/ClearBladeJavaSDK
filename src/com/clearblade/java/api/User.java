package com.clearblade.java.api;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class User {
	
	private static String email;				//users email address
	private static String authToken;			//auth token for user
	
	private RequestEngine request;			//used to make api requests
	
	
	public User(String e){
		if(e == null){
			email = "anonymous";
		}else{
			email = e;
		}
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setAuthToken(String aT){
		authToken = aT;
	}
	
	public String getAuthToken(){
		return authToken;
	}
	
	public void authWithCurrentUser(String password, final InitCallback callback){
		//get auth token with current user
		request = new RequestEngine();
		
		JsonObject payload = new JsonObject();
		payload.addProperty("email", this.getEmail());
		payload.addProperty("password", password);
		
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/user/auth").body(payload).build();
		request.setHeaders(headers);
		
//		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
//			@Override
//			public void done(String response){
//				authToken = getPropertyValueFromJSONString("user_token", response);
//				callback.done(true);
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				callback.error(exception);
//			}
//		});
		
		//asyncFetch.execute(request);
		
		PlatformResponse result = request.execute();
		if(result.getError()){
			Util.logger("CBUserTask", "User call failed: " + result.getData(), true);
			ClearBlade.setInitError(true);
			callback.error(new ClearBladeException("Call to user failed: " + result.getData()));
		}else{
			authToken = getPropertyValueFromJSONString("user_token", (String)result.getData());
			callback.done(true);
		}
		

	}

	public void registerUser(final String password, final InitCallback callback) {
		request = new RequestEngine();
		
		JsonObject payload = new JsonObject();
		payload.addProperty("email", this.getEmail());
		payload.addProperty("password", password);
		
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/user/reg").body(payload).build();
		request.setHeaders(headers);
		
//		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
//			@Override
//			public void done(String response){
//				//user reg successful, now auth as them
//				authWithCurrentUser(password, callback);
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				if(exception.getMessage().toLowerCase().contains("bad request")){
//					callback.error(new ClearBladeException("Unable to register user, email taken"));
//				}else{
//					callback.error(exception);
//				}
//			}
//		});
//		
//		asyncFetch.execute(request);
		
		PlatformResponse result = request.execute();
		if(result.getError()){
			Util.logger("CBUserTask", "User call failed: " + result.getData(), true);
			callback.error( new ClearBladeException("Unable to register user, email taken") );
			
		}else{
			authWithCurrentUser(password, callback);
		}
		
	}

	public void authWithAnonUser(final InitCallback callback) {
		request = new RequestEngine();

		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/user/anon").build();
		request.setHeaders(headers);

//		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
//			@Override
//			public void done(String response){
//				authToken = getPropertyValueFromJSONString("user_token", response);
//				callback.done(true);
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				callback.error(exception);
//			}
//		});
//		
//		asyncFetch.execute(request);
		PlatformResponse result = request.execute();
		if(result.getError()){
			Util.logger("CBUserTask", "User call failed: " + result.getData(), true);
			ClearBlade.setInitError(true);
			callback.error( new ClearBladeException("Unable to authWithAnonUser") );
			
		}else{
			authToken = getPropertyValueFromJSONString("user_token", (String)result.getData());
			callback.done(true);
		}
		
	}
	
	public void checkUserAuth(final InitCallback callback){
		request = new RequestEngine();

		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/user/checkauth").build();
		request.setHeaders(headers);

//		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
//			@Override
//			public void done(String response){
//				String respValue = getPropertyValueFromJSONString("is_authenticated", response);
//				if(respValue.equalsIgnoreCase("true")){
//					callback.done(true);
//				}else{
//					callback.done(false);
//				}
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				callback.error(exception);
//			}
//		});
//
//		asyncFetch.execute(request);
		PlatformResponse result = request.execute();
		if(result.getError()){
			Util.logger("CBUserTask", "User call failed: " + result.getData(), true);
			ClearBlade.setInitError(true);
			callback.error( new ClearBladeException("checkUserAuth") );
			
		}else{
			String respValue = getPropertyValueFromJSONString("is_authenticated", (String)result.getData());
			if(respValue.equalsIgnoreCase("true")){
				callback.done(true);
			}else{
				callback.done(false);
			}
		}
	}

	public void logout(final InitCallback callback){

		request = new RequestEngine();

		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/user/logout").build();
		request.setHeaders(headers);

//		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
//			@Override
//			public void done(String response){
//				authToken = null;
//				callback.done(true);
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				callback.error(exception);
//			}
//		});
//		
//		asyncFetch.execute(request);
		PlatformResponse result = request.execute();
		if(result.getError()){
			Util.logger("CBUserTask", "User call failed: " + result.getData(), true);
			ClearBlade.setInitError(true);
			callback.error( new ClearBladeException("logout") );
			
		}else{
			authToken = null;
			callback.done(true);
		}

	}
	
	
//	public void getUser(final DataCallback callback){
//		request = new RequestEngine();
//
//		RequestProperties headers = new RequestProperties.Builder().method("GET").endPoint("api/v/1/user/info").build();
//		request.setHeaders(headers);
//
//		DataTask asyncFetch = new DataTask(new PlatformCallback(query, callback){
//			@Override
//			public void done(String response){
//				authToken = null;
//				callback.done(true);
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				callback.error(exception);
//			}
//		});
//		
//		asyncFetch.execute(request);
//	}
//	
//	public void getUsers(){
//		request = new RequestEngine();
//
//		RequestProperties headers = new RequestProperties.Builder().method("GET").endPoint("api/v/1/user").build();
//		request.setHeaders(headers);
//
//		UserTask asyncFetch = new UserTask(new PlatformCallback(this, callback){
//			@Override
//			public void done(String response){
//				authToken = null;
//				callback.done(true);
//			}
//			@Override
//			public void error(ClearBladeException exception){
//				ClearBlade.setInitError(true);
//				callback.error(exception);
//			}
//		});
//		
//		asyncFetch.execute(request);
//	}
	
	private String getPropertyValueFromJSONString(String property, String json){
		String value = null;
		
		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject)parser.parse(json);
		
		value = o.get(property).getAsString();
		
		return value;
	}
}
