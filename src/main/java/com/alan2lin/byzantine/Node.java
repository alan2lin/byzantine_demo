package com.alan2lin.byzantine;

import java.util.ArrayList;

import com.alan2lin.byzantine.util.Constants.Color;
import com.alan2lin.byzantine.util.Constants.NODETYPE;





public class Node {

	public Node(String index, String displayName, boolean isTraitor, NODETYPE type) {
		this.index = index;
		this.dispalyName = displayName;
		this.isTraitor = isTraitor;
		this.type = type;
		if(isTraitor) this.color = Color.LIGHTGREY;
		
		
		
	}

	public String index;
	public String dispalyName;
	public ArrayList<Link> outLinkSet = new ArrayList<Link>();
	public boolean isTraitor = false;
	public NODETYPE type = NODETYPE.LIEUTENANT;
	public String receivedOrder = "a";
	public boolean visibled=false;
	public Color color = Color.BLACK;
	public Link parent = null;
	public int age = -1;

	public void addLink(Link link) {
		this.outLinkSet.add(link);
	}
	
	public void setParent (Link parent){
		this.parent = parent;
	}
	
	public String getDotSource(){
		if(this.visibled){
			
			return String.format("%s[label=\"%s\" %s ]\n", this.index,this.dispalyName, "fillcolor = \""+ this.color.name() +"\" ");
		}else{
			
			return String.format("%s[label=\"%s\" %s %s]\n", this.index,this.dispalyName, "fillcolor = \""+ this.color.name() +"\" ", " style=\"dashed\" ");
		}		
	}
}