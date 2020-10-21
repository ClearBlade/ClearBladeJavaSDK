package com.clearblade.java.api.internal;

import com.clearblade.java.api.*;
import com.clearblade.java.api.MqttClient;

public abstract class PlatformCallback {
	public Collection _collection;
	public DataCallback _callback;
	public InitCallback _initCallback;
	public CodeCallback _codeCallback;
	public MessageCallback _messageCallback;
	public MessageCallback _historyCallback;
	public Item _item;
	public Query _query;
	public User _user;
	public Code _code;
	public MqttClient _mqttClient;
	public History _history;
	
	public PlatformCallback(Query query, DataCallback callback){
		_query = query;
		_callback = callback;
	}
	
	public PlatformCallback(Collection collection, DataCallback callback){
		_collection = collection;
		_callback = callback;
	}
	
	public PlatformCallback(Item item, DataCallback callback){
		_item = item;
		_callback = callback;
	}
	

	public PlatformCallback(User user, InitCallback callback) {
		_user = user;
		_initCallback = callback;
	}
	
	public PlatformCallback(Code code, CodeCallback callback){
		_code = code;
		_codeCallback = callback;
	}

	public PlatformCallback(MqttClient mqttClient, MessageCallback callback){
		_mqttClient = mqttClient;
		_messageCallback = callback;
	}
	
	public PlatformCallback(History history, MessageCallback callback){
		_history = history;
		_messageCallback = callback;
	}
	
	public abstract void done(String response);
	public void error(ClearBladeException exception){
		
	}	
}
