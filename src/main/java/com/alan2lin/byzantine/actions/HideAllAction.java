package com.alan2lin.byzantine.actions;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;

public class HideAllAction extends Action {
	
	public void exec(Node node){
		
		node.visibled = false;

	}
	public void exec(Link link){
		
		link.visibled = false;

	}
}
