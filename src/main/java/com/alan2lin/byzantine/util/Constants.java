package com.alan2lin.byzantine.util;



public class Constants {
	
	public  enum MODE {
	    BOTH, GOOD, BAD;}
	
	public enum NODETYPE {
		COMMANDER, LIEUTENANT
	}
	
	public enum  Color{
		RED("RED"), WHITE("WHITE"),LIGHTGREY("LIGHTGREY"),BLACK("BLACK");
		
        private final String value;

        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        Color(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
