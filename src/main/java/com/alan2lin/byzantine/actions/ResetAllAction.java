package com.alan2lin.byzantine.actions;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;
import com.alan2lin.byzantine.util.Constants.Color;

public class ResetAllAction extends Action {
	
	private boolean isVisibled = false;
	//主要是可见性和颜色的重设
	public ResetAllAction(boolean isVisibled){
		
		
		this.isVisibled = isVisibled;
		
	}
	
	public void exec(Node node){
		
		node.color = node.isTraitor? Color.LIGHTGREY :Color.WHITE;
		
		node.visibled = isVisibled;
		

	}
	
public void exec(Link link){
	        link.color = Color.BLACK;
			link.visibled=isVisibled;
	}

}
