package com.alan2lin.byzantine.actions;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;

public class ShowAllAction extends Action {
	
	public void exec(Node node){
		
		node.visibled = true;

	}
	
public void exec(Link link){
			link.visibled=true;
	}

}
