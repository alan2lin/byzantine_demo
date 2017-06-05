package com.alan2lin.byzantine;

import com.alan2lin.byzantine.util.Constants.Color;

public class Link {

	public Link(Node start, Node end, boolean visibled, Color color) {
		this.start = start;
		this.end = end;
		this.visibled = visibled;
		this.color = color;
	}

	public Node start;
	public Node end;
	public boolean visibled = false;
	public Color color = Color.BLACK;
	public String order;

	
	public String getDotSource(){
		if(this.visibled){
			return String.format("%s -> %s [label=\"%s\" color=\"%s\"]\n", this.start.index,this.end.index,this.order,this.color);
		}else{
			//return "";
			return String.format("%s -> %s [label=\"%s\" style=\"dashed\"  color=\"%s\"]\n", this.start.index,this.end.index,this.order,this.color);
			
		}		
	}
}