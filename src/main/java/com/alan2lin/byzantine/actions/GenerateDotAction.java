package com.alan2lin.byzantine.actions;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;

public class GenerateDotAction extends Action {
	

	StringBuffer sb = new StringBuffer();
	
	public void clearContent(){
		this.sb = new StringBuffer();
	}
	
	public void exec(Node node){
		sb.append(node.getDotSource());
		
	}
	
	public void exec(Link link){
		sb.append(link.getDotSource());
		
		
	}
	
	public String getResult(){
		
		String head ="digraph G {  node[fontname=\"FangSong\"]; \n edge[fontname=\"FangSong\"]; \n graph[fontname=\"FangSong\"];\n node [style = filled , fillcolor = \"WHITE\"  ];\n compound=true;";
		String tail = "}";
		
		String dotFile = String.format("%s  %s %s", head, sb.toString() ,tail);
		
		return dotFile;
	}

}
