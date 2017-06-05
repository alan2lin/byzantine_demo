package com.alan2lin.byzantine;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alan2lin.byzantine.actions.Action;
import com.alan2lin.byzantine.util.Constants;
import com.alan2lin.byzantine.util.Constants.Color;
import com.alan2lin.byzantine.util.Constants.MODE;
import com.alan2lin.byzantine.util.Constants.NODETYPE;



/*
 * 拜占庭问题的算法树构建，与遍历
 * 
 */
public class BZT {
	
	static Logger logger = LoggerFactory.getLogger(BZT.class);
	
	int hc = 0;
	int traitors = 0;
	Constants.MODE mode = Constants.MODE.BOTH;
	
	Hashtable<String,ArrayList<Link>> incomeSet = new Hashtable<String,ArrayList<Link>>();

	public BZT(int hc, int traitors, Constants.MODE mode) {
		this.hc = hc;
		this.traitors = traitors;
		this.mode = mode;
	}
	
	public void putLink2IncomeSet(String displayName,Link link){
		if (this.incomeSet.containsKey(displayName)){
			this.incomeSet.get(displayName).add(link);
		}else{
			ArrayList<Link> links = new ArrayList<Link>();
			links.add(link);
			this.incomeSet.put(displayName, links);
		}
	}


	Node initRoot(boolean isTraitor) {
		logger.trace("init tree root node ...");
		
		logger.debug(String.format("hc=[%d] traitors=[%d] mode=[%s]",this.hc,this.traitors,this.mode) );

		Node commander = new Node("C", "C", isTraitor, NODETYPE.COMMANDER);

		for (int i = 1; i < this.hc; i++) {
			Node tmp = new Node("L" + i, "L" + i, i + this.traitors + (commander.isTraitor?0:1) > this.hc, NODETYPE.LIEUTENANT);
			Link link = new Link(commander, tmp, false, Color.WHITE);
			commander.addLink(link);
			
			tmp.setParent(link);
			
			this.putLink2IncomeSet(tmp.dispalyName, link);
		}

		return commander;

	}

	
	void OM(int m, Node commander) {
		
		logger.trace(String.format(" m=[%d] children size=[%d]", m,commander.outLinkSet.size()));

		String[] orderSet = new String[]{"a","r"};
		
		//The commander sends his value to every lieutenant
		
		commander.outLinkSet.forEach((link)->{
			link.visibled = true;
			link.order = commander.isTraitor? "?"+orderSet[(new Random().nextInt(1000))%2] :commander.receivedOrder;
			link.end.receivedOrder = link.order;
		} );
		
		//设置年代标志以追踪对象
		
		commander.age = m;

		
		if (m == 0) {		
			return;
			
		} else {
		//For each i, let vi be the value Lieutenant i receives from the commander, or else be
		//	RETREAT if he re :eives no value. Lieutenant i acts as the commander in Algorithm
		//	OM(m - 1) to send the value vi to each of the n - 2 other lieutenants
			
			for(int i = 0; i< commander.outLinkSet.size();i++){
				Node newCommander = commander.outLinkSet.get(i).end;				
				
				for(int j = 0; j< commander.outLinkSet.size();j++) {
					if(i!=j){
						Node old = commander.outLinkSet.get(j).end;
						
						Node tmp = new Node(newCommander.index+"_"+old.index, old.dispalyName, old.isTraitor, NODETYPE.LIEUTENANT);
						Link link = new Link(newCommander, tmp, false, Color.WHITE);
						newCommander.addLink(link);	
						tmp.setParent(link);
						this.putLink2IncomeSet(tmp.dispalyName, link);
					}

				}
				
			    OM(m-1,newCommander);
			}
		}
		


	}
	
	public void makeTree(Node root){
		
		logger.trace("makeTree ===========");
		
		OM(this.traitors,root);
	
	}
	
	
	/**
	 * 前序遍历
	 * @param node
	 * @param action
	 */
	public void treeDLRWalker(Node node,Action action){
		
		logger.debug(String.format("name[%s],idx[%s]",node.dispalyName,node.index));
		
		action.exec(node);
		
		for(Link link : node.outLinkSet){
			
			treeDLRWalker(link.end,action);
			action.exec(link);
		}		
	}
	
	
	/**
	 * 层次遍历
	 * @param node
	 * @param action
	 */
	public void treeBFSWalker(Queue<Node> nodeQueue,Queue<Link>incomeLinkQueue, Action action,int limit){
		

		logger.debug(String.format("nodes size is [%d]",nodeQueue.size()));
		
		//需要维护原有的节点队列信息，更改单队列成新旧队列
		Queue<Link> newLinkQueue = new LinkedList<Link>();
		Queue<Node> newNodeQueue = new LinkedList<Node>();
		
		
		for(Node node  :nodeQueue ){
			action.exec(node);	
			newLinkQueue.addAll(node.outLinkSet);
		}
		
		
		for(Link link :incomeLinkQueue ){
			action.exec(link);	
		}
		

		
		
		newLinkQueue.forEach((x)->newNodeQueue.add(x.end));
		
		
		
		/*
		// deque  linkQueue 
		while (!incomeLinkQueue.isEmpty()){
			Link link  =  incomeLinkQueue.poll();
			action.exec(link);			
		}
		
		// deque  nodeQueue   enque incomeLinkqueue 
		while (!nodeQueue.isEmpty()){
			Node node  =  nodeQueue.poll();
			action.exec(node);
			
			incomeLinkQueue.addAll(node.outLinkSet);
		}
		 
		
		//enque nodeQueue  
		incomeLinkQueue.forEach((x)->nodeQueue.add(x.end));
		
		
		*/
		
		action.beforeRecurse();
		//recurse
		if(limit==0){return ;}
		
		//treeBFSWalker(nodeQueue,incomeLinkQueue,action,limit<0?limit:limit-1);
		treeBFSWalker(newNodeQueue,newLinkQueue,action,limit<0?limit:limit-1);
		
		for(Link link :incomeLinkQueue ){
			action.postExec(link);	
		}
		
		for(Node node  :nodeQueue ){
			action.postExec(node);			
		}
		
		action.afterRecurse();

	
	}

	public static void main(String[] args) {


	}

}
