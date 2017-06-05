package com.alan2lin.byzantine.util;

import java.util.Hashtable;

public class Context {
	
    private static Context instance;  
    private Hashtable<String,Object> store = new Hashtable<String,Object>();
   //没有多线程，不考虑同步
    public static Context getInstance() {  
    if (instance == null) {  
        instance = new Context();  
    }  
    return instance;
    }
    
	private Context(){
		
	}
	
	public void set(String key,Object v){
		store.put(key, v);
	}
	
	public Object get(String key){
		return store.get(key);
	}

}
