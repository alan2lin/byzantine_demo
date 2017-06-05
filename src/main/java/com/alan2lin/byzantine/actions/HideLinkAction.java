package com.alan2lin.byzantine.actions;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;

public class HideLinkAction extends Action {
	

	public void exec(Link link){
		
		link.visibled = false;

	}
}
