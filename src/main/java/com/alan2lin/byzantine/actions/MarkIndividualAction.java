package com.alan2lin.byzantine.actions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;
import com.alan2lin.byzantine.util.Constants.Color;
import com.alan2lin.byzantine.util.Context;

public class MarkIndividualAction extends Action {
	
	private ArrayList<Link> links = null;
	private ArrayList<Node> nodes = new ArrayList<Node>();
	public StringBuffer sb = new StringBuffer();
	
	
    public ArrayList<ArrayList<String>> textChain  = new ArrayList<ArrayList<String>>();
	
	public MarkIndividualAction(ArrayList<Link> links){
		this.links = links;
		links.forEach((x)-> nodes.add(x.end));
	}
	
	public void exec(Node node){
		
		//如果当前节点符合link的箭头指向，设置为红色，并将该路径设置为红色。
		
		if(nodes.contains(node)){
			
			node.color=Color.RED;
			//node.visibled = true;
			
			Node tmp = node;
			
			String order = node.receivedOrder;
			
			ArrayList<String> record = new ArrayList<String>();
			ResourceBundle msg = (ResourceBundle) Context.getInstance().get("msg");
			String said = msg.getString("SAID");
			while(tmp.parent!=null){
				tmp.visibled = true;
				tmp.parent.visibled=true;
				tmp.parent.color=Color.RED;
				tmp = tmp.parent.start;
				
				sb.append(tmp.dispalyName + " "+said+" " );
				
				record.add(tmp.dispalyName + " "+said+" ");
			}
			
			sb.append( order +"\n");
			record.add(order );
			
			textChain.add(record);
		}

	}
	
public void exec(Link link){
			//link.visibled=true;
	}



public void sortRecord(){
	//给记录排序
	this.textChain.sort(
			new Comparator<ArrayList<String>>() {

				@Override
				public int compare(ArrayList<String> o1, ArrayList<String> o2) {
					int a = o1.size();
					int b = o2.size();
					
					if ( a > b ){
						return 1;
					}else if (a<b){
						return -1;
					}else {
						//按抛出最后一个元素的 公共字串来排序
						int size = o1.size();
						for(int i=size - 2;i>=0;i--){
							int t = o1.get(i).compareTo(o2.get(i));
							if (t!=0)
								return t;

						}
						
						
						
						return 0;
					}
					
				}
	} 
	
	);
	

	

	
	
}

public String getWords(){
	
	//处理
	
	this.sortRecord();
	
   //输出排序后的信息
   sb.append("===============================================================\n");
   
   for(ArrayList<String> record : textChain){
	   record.forEach((x)->sb.append(x));
	   sb.append("\n");
   }
	
   //输出majority函数形式
   sb.append("===============================================================\n");
   
   
   
	
	return sb.toString();
}

}
