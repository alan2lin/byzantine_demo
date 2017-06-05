package com.alan2lin.byzantine.actions;

import java.util.ArrayList;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;

public abstract class Action {
	
	public Action( ){};
	public Action(ArrayList<Link> links ){};
	
	//递归时的
	public void exec(Node node){};
	public void exec(Link link){};
	
	public void postExec(Node node ){};
	public void postExec(Link link ){};

	//递归调用前后 的回调入口  仅用于快照功能
	public void beforeRecurse(){};
	public void afterRecurse(){};
	

}
