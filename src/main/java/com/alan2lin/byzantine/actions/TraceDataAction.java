package com.alan2lin.byzantine.actions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.alan2lin.byzantine.Link;
import com.alan2lin.byzantine.Node;
import com.alan2lin.byzantine.util.Constants.Color;

public class TraceDataAction extends Action {
	
	class KVPair{
		public KVPair(String key,String value){
			this.key = key;
			this.value = value;
		}
		public String key;
		public String value;
		public Record subRecord;
	}
	
	class Record{
		public Record(Node node){
			this.index = node.index;
			this.age = node.age;
			this.displayName = node.dispalyName;
			this.receiveOrder = node.receivedOrder;
			this.sender = node.parent==null?"": node.parent.start.dispalyName;
			
		}
		String index;
		int age;
		String displayName;
		String sender;
		String  receiveOrder;
		ArrayList<KVPair> otherOrders = new ArrayList<KVPair>();
		ArrayList<KVPair> calcOrders = new ArrayList<KVPair>();
		String majority;
		
		boolean postFlag = false;
		
		
		public String calcMajority(){
			String ret = "";
			int count = this.calcOrders.contains("a")?1:0;
			
			
			for(KVPair kvp : this.calcOrders){
				count += kvp.value.contains("a")?1:0;
			}
			int compare = count*2 - this.calcOrders.size();
			if ( compare >0 ){
				ret = "a";
			}else{
				ret ="r";
			}
			return ret;
		}
		
		public String toString(){
			if(this.otherOrders.size()==0) return "";
					
			StringBuffer sbOtherOrders = new StringBuffer();
			StringBuffer sbLink = new StringBuffer();
			
			StringBuffer sbMajority = new StringBuffer();
			StringBuffer sbCalcOrders = new StringBuffer();
			
			
			//分递归前和递归后的两种情况。
			//先处理公共的情况		
			
			//递归前处理  主要是 received value		
			
			//递归后处理 是 

			sbMajority.append("majority("+this.sender+"="+this.receiveOrder+",");
			
			for(int i =0;i<this.otherOrders.size();i++){
				KVPair kvpRec = this.otherOrders.get(i);
				
				//设置锚点
				if(!postFlag){
					sbOtherOrders.append(String.format("|<f%d> %s  %s",i+1,kvpRec.key,kvpRec.value ));
					
				}else{
					sbOtherOrders.append(String.format("| %s  %s",kvpRec.key,kvpRec.value ));
					
					KVPair kvpCalc = this.calcOrders.get(i);
					sbCalcOrders.append(String.format("| <f%d> %s  %s",i+1,kvpCalc.key,kvpCalc.value ));
					
					sbMajority.append( kvpCalc.key+"="+kvpCalc.value +",");
					//sbMajority.append(String.format("|<f%d> %s  %s",i+1,kvpCalc.key,kvpCalc.value ));
				}
				
				
		
				//构造链接
				if (kvpRec.subRecord!=null && kvpRec.subRecord.otherOrders.size()!=0){					
					    sbLink.append(String.format("%s:<f%d>->%s ;\n", this.index,i+1,kvpRec.subRecord.index));

				}
								
				
			}		

			//去除尾部
			if(this.postFlag){
				sbMajority.deleteCharAt(sbMajority.length()-1);
							
				sbMajority.append(")="+this.majority);
			}
			
			String recordString  = null;
			if(this.postFlag){
				//  OM(x) nodeName
				//  received value label
				//  received value list
				//  majority formula
				//  calc value label 
				// calc value list
				recordString  = String.format("%s[label=\"{OM(%d): %s | received value:|{  %s:%s %s} | { %s } |calc value:|{<f0> %s:%s %s}  }\"  shape=\"record\"] ;", this.index ,this.age,this.displayName, this.sender,this.receiveOrder,sbOtherOrders.toString(),sbMajority.toString(),this.sender,this.receiveOrder,sbCalcOrders.toString());
				
			}else{
				//  OM(x) nodeName
				//  received value label
				//  received value list

				recordString  = String.format("%s[label=\"{OM(%d): %s | received value:|{ <f0> %s:%s %s}    }\"  shape=\"record\"] ;", this.index ,this.age,this.displayName, this.sender,this.receiveOrder,sbOtherOrders.toString());
				
			}
				
			
			return recordString+sbLink.toString();
		}
	}
	
	//以节点index作为key，便于寻找对象	
	Hashtable<String,Record> hshDataManager = new  Hashtable<String,Record>();
	
	
	
    Record root = null;
	
    
	

	
	/**
	 * 遍历节点时创建 record记录
	 */
	public void exec(Node node){
		
		
		Record record= new Record(node);
		hshDataManager.put(node.index, record);
		
		if(node.parent==null){
			this.root = record;
		}	

		
		
	}
	
	
	
public void exec(Link link){
		//在OM(m-1)中的发送消息的处理，
	    //发送时送到同一步骤的其他节点的record中，并绑定两个record的记录	    
	    //发送者的record记录的otherOrder    
	    
	    if(link.start.parent==null){
	    	KVPair kvp = new KVPair(link.end.dispalyName, link.end.receivedOrder);	
	    	kvp.subRecord = hshDataManager.get(link.end.index);	
	    	this.root.otherOrders.add(kvp);
	    }else{
	    	// pattern:L1_L2_l3 
	    	
	    	
	    	//根据命名规则， 前面一半是前缀
	    	//需要统计_出现的个数， 然后以中间为分隔符
	    	//或者 split再join
	    	
	    	String current_index = link.end.index;
	    	String[] tmp = current_index.split("_");
	    	StringBuffer sbtmp = new StringBuffer();
	    	for(int i=tmp.length/2;i<tmp.length;i++){
	    		sbtmp.append(tmp[i]+"_");
	    	}
	    	sbtmp.deleteCharAt(sbtmp.length()-1);
	    	
	    	String index =sbtmp.toString();
	    	
	    	
	    	
	    	
		    KVPair kvp = new KVPair(link.start.dispalyName, link.order);	    
		    kvp.subRecord = hshDataManager.get(link.end.index);	   
		    
		    hshDataManager.get(index).otherOrders.add(kvp);
	    }
	    
	
	}


   /**
    * 逆向构造 calcOrders
    * 如果 没有下一层节点，则意味着这是叶节点， 拷贝 otherOrders 到 calcOrder 列表，并进行 Majority的计算。
    * 如果有下一层节点，则逐一获取下一层的 majority 到 calcOrder ，计算本结点的Majority。
    * 设置 postflag标志
    */
   public void postExec(Node node){
	   
	   
	   
	   Record record = hshDataManager.get(node.index);
	   
	   for(int i=0;i<record.otherOrders.size();i++){
		   KVPair kvp = record.otherOrders.get(i);
		   
		   if (kvp.subRecord!=null&&kvp.subRecord.postFlag==true){
			   record.calcOrders.add(new KVPair(kvp.key,  kvp.subRecord.majority));
		   }else{
			   record.calcOrders.add(kvp);
		   }
	   }

	   record.majority = record.calcMajority();
	   record.postFlag = true;  
	   
	   
   }

   public void DLR(Record record,StringBuffer sb){
	   
	   sb.append(record.toString()+"\n");
	   
	   
	   for(KVPair kvp : record.otherOrders ){
		   if(kvp.subRecord!=null) DLR(kvp.subRecord ,sb);
	   }
   }

   
   
    public String getDotContent(String origin){
		String head ="digraph G {  node[fontname=\"FangSong\"]; \n edge[fontname=\"FangSong\"]; \n graph[fontname=\"FangSong\"];\n node [style = filled , fillcolor = \"WHITE\"  ];\n compound=true;";
		String tail = "}";
		String dotFile = String.format("%s  %s %s", head, origin ,tail);
		return dotFile;
    }
   
    public String getResult(){
    	StringBuffer sb = new StringBuffer();
    	
    	DLR(this.root,sb);
    	
    	
		String dotFile = getDotContent(sb.toString());
    	
    	
    	return dotFile;
    } 
    
    public HashMap<String, String> getIndividualResult(){
    	HashMap<String, String> ret = new HashMap<String, String>();
    	
    	for(KVPair kvp : this.root.otherOrders){
    		
        	StringBuffer sb = new StringBuffer();
        	
        	DLR(kvp.subRecord,sb);
        	        	
    		String dotFile = getDotContent(sb.toString());
        	
    		ret.put(kvp.key,dotFile);
    	}
    	
    	return ret;
    	
    }

}
