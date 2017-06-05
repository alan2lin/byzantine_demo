package com.alan2lin.byzantine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.ResourceBundle;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import com.alan2lin.byzantine.util.UTF8Control;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import com.alan2lin.byzantine.util.Constants.MODE;
import com.alan2lin.byzantine.util.Context;
import com.alan2lin.byzantine.util.FileManager;
import com.alan2lin.byzantine.actions.Action;
import com.alan2lin.byzantine.actions.GenerateDotAction;
import com.alan2lin.byzantine.actions.HideAllAction;
import com.alan2lin.byzantine.actions.HideLinkAction;
import com.alan2lin.byzantine.actions.MarkIndividualAction;
import com.alan2lin.byzantine.actions.ResetAllAction;
import com.alan2lin.byzantine.actions.ShowAllAction;
import com.alan2lin.byzantine.actions.TraceDataAction;
import com.alan2lin.byzantine.util.Constants; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	static Logger logger = LoggerFactory.getLogger(App.class);
	
	
	/**
	 * 生成命令行的选项， 具体的选项描述在资源文件 message.properties中，
	 * 支持i18n资源国际化,目前只有中英文两个版本 message_zh_CN.properties， 需要的请自行翻译并增加 properties文件 .
	 * @param msg
	 * @return 该命令行的所有选项集合
	 */
	private static Options options(ResourceBundle msg) {       
        
        Options options = new Options();  
        
        Option o = new Option("hc", "headcount", true, msg.getString("HEADCOUNT"));
        o.setType(int.class);
        
        options.addOption("h", "help", false, msg.getString("HELP") );        
        //options.addOption("hc", "headcount", true, msg.getString("HEADCOUNT") );  
        options.addOption(o);  
        options.addOption("t", "traitors", true, msg.getString("TRAITORS") );  
        options.addOption("m", "mode", true, msg.getString("MODE") );  
         
        options.addOption("locale", "locale", true, msg.getString("LOCALE") );
        
       
        return options;  
    }  
	
	/**
	 * 获取语言区域，以便本地化，假如系统无改语言设定，则用默认的语言设定
	 * @param language 语言设定 
	 * @return
	 */
	private static Locale getLocale(String language){
		Locale locale = null;
		try{
			if (language!=null && !language.isEmpty()){
				String[] lang = language.split("_");
				if(lang.length==1){
					locale = new  Locale(lang[0]);	
				}else{
					locale = new  Locale(lang[0],lang[1]);	
				}				
				
			}else{
				locale = Locale.getDefault();
			}
			
		}catch(Exception e){
			locale = Locale.getDefault();
		}
		return locale;
	}
	
	static private void traceLocaleInfo(Locale locale){
		
		logger.trace("语言代码: " + locale.getLanguage());  
        logger.trace("地区代码: " + locale.getCountry());  
        logger.trace("语言地区代码: " + locale.toString());  
        logger.trace("---------------------------------------");  
        logger.trace("语言描述: " + locale.getDisplayLanguage());  
        logger.trace("地区描述: " + locale.getDisplayCountry());  
        logger.trace("语言,地区描述: " + locale.getDisplayName());  
        //logger.trace("---------------------------------------");  
        //logger.trace("在美国默认语言叫: " + locale.getDisplayLanguage(Locale.US));  
        //logger.trace("在美国默认地区叫: " + locale.getDisplayCountry(Locale.US));  
        //logger.trace("在美国默认语言,地区叫: " + locale.getDisplayName(Locale.US));  
        //logger.trace("在日本默认语言代码叫: " + locale.getDisplayLanguage(Locale.JAPAN));  
        //logger.trace("在日本默认地区代码叫: " + locale.getDisplayCountry(Locale.JAPAN));  
        //logger.trace("在日本默认语言,地区代码叫: " + locale.getDisplayName(Locale.JAPAN));  
        logger.trace("---------------------------------------");  
        logger.trace("语言环境三字母缩写: " + locale.getISO3Language());  
        logger.trace("国家环境三字母缩写: " + locale.getISO3Country());  
        logger.trace("---------------------------------------");  
        // 机器已经安装的语言环境数组  
        Locale[] allLocale = Locale.getAvailableLocales();  
		
        // 返回 ISO 3166 中所定义的所有两字母国家代码  
        String[] str1 = Locale.getISOCountries();  
        // 返回 ISO 639 中所定义的所有两字母语言代码  
        String[] str2 = Locale.getISOLanguages();  
        
	}
	
	
	private static ResourceBundle getMessagesByLang(String lang){
  	  Locale locale_default = getLocale(lang);
  	  traceLocaleInfo(locale_default);
  	  
       ResourceBundle msg = ResourceBundle.getBundle("messages", locale_default,new UTF8Control() ); 
  	  
  	  return msg;
	}
	

	
	private static boolean checkOption(CommandLine cl, String optionName,  ResourceBundle msg, String ErrorMsgCode){
		boolean bRet = true;
	    if (cl.hasOption(optionName)){
   	    	
	    	if ( !StringUtils.isNumeric(cl.getOptionValue(optionName)) ){
	    		logger.error(msg.getString(ErrorMsgCode));
	    		bRet = false;
	    	}
	    }else{
	    	bRet = false;
	    }
	    return bRet;
	}
	
	
    public static void main( String[] args )
    {
    	
    	  logger.trace("starting.....");
    	  //总人数
    	  int hc;
    	  //叛徒数目
    	  int traitors;
    	  //mode    	  
    	  Constants.MODE mode = Constants.MODE.BOTH;
    	  
          ResourceBundle msg = getMessagesByLang(null);

    	  Options opts = options(msg);
    	  
    	  BasicParser parser = new BasicParser();
    	  CommandLine cl;
    	  try {
     	     HelpFormatter hf = new HelpFormatter();
     	     
    	   cl = parser.parse(opts, args);
    	   
    	   if (cl.getOptions().length > 0) {
    		
    		  if (cl.hasOption("locale")){
    			  
    			  //reload message, re-parsing
    			  msg =getMessagesByLang( cl.getOptionValue("locale") );
    			  opts = options(msg);
    			  cl = parser.parse(opts, args);
    		  }
    		   
    	    if (cl.hasOption('h')) {    	     
    	     hf.printHelp(msg.getString("USAGE"), opts);        
    	    }
    	    
    	    logger.trace("check numberic opitons...");
    	    if (!checkOption(cl,"hc",msg,"HCERROR")){
    	    	return ;
    	    }
    	    if (!checkOption(cl,"t",msg,"TRAITORSERROR")){
    	    	return ;
    	    }
    	    if (!checkOption(cl,"m",msg,"MODEERROR")){
    	    	return ;
    	    }
    	    
    	    

    	    hc = Integer.parseInt(cl.getOptionValue("hc"));
    	    traitors = Integer.parseInt(cl.getOptionValue("t"));
    	    int imode = Integer.parseInt(cl.getOptionValue("m"));
    	    mode = imode == 0 ? Constants.MODE.BOTH: imode== 1 ? Constants.MODE.GOOD:Constants.MODE.BAD;
    	    
    	    
    	    //store to context
    	    
    	    Context ctx = Context.getInstance();
    	    ctx.set("msg", msg);
    	    ctx.set("hc", cl.getOptionValue("hc"));
    	    ctx.set("t", cl.getOptionValue("t"));
    	    ctx.set("mode", mode);
    	    
    	    
    		logger.trace(" create bzt");
    	    
    		if(mode.equals(MODE.GOOD)||mode.equals(MODE.BOTH)){
    			generateBZT(hc, traitors, MODE.GOOD);	
    		}
    		if(mode.equals(MODE.BAD)||mode.equals(MODE.BOTH)){
    			generateBZT(hc, traitors, MODE.BAD);	
    		}
    		
    		

   
    	    
    	   } else {
    		 
    		 hf.printHelp(msg.getString("NOPARAMS")+msg.getString("USAGE"), opts);
    	    
    	   }
    	  } catch (Exception e) {
    	   e.printStackTrace();
    	  }
    	 
    	String COMPLETED = msg.getString("COMPLETED"); 
        logger.info(COMPLETED  );
    }

	private static void generateBZT(int hc, int traitors, Constants.MODE mode) {
		//生成树
		
		BZT bzt = new BZT(hc, traitors, mode);
		Node root =bzt.initRoot(!mode.equals(MODE.GOOD));
		
		//构造目录前缀 nXtXc(l/t)，约定nX代表总数X tX代表叛徒数， c代表最初的发令者性质，l代表忠诚者， t代表叛徒
		
		String prefix =String.format("n%dt%dc%s", hc,traitors,mode.equals(MODE.GOOD)?"l":"t");
		
		
		bzt.makeTree(root);
		
		GenerateDotAction generateDot = new GenerateDotAction()	;   
		HideAllAction hideAll = new HideAllAction();
		ShowAllAction showAll = new ShowAllAction();
		HideLinkAction hideLink = new HideLinkAction();
		ResetAllAction resetAllVisible = new ResetAllAction(true);
		ResetAllAction resetAllInvisible = new ResetAllAction(false);
		
		//设置所有的节点和边可视化并输出到   full
		
		bzt.treeDLRWalker(root, resetAllVisible);    		
		 		
		bzt.treeDLRWalker(root,generateDot);
		String dotFile =  generateDot.getResult();    		
		makeDotAndPic(prefix+"/full","full",dotFile,null);
		
		//设置所有的节点隐藏  并根据m的数目生成stage1到 stagem的目录
		
		for(int i =0 ;i < traitors+1;i++){
			bzt.treeDLRWalker(root, resetAllInvisible);
			
			Queue<Link> linkQueue = new LinkedList<Link>();
			Queue<Node> nodeQueue = new LinkedList<Node>();
			
			nodeQueue.add(root);
			
			bzt.treeBFSWalker(nodeQueue, linkQueue, showAll, i+1);
			
			generateDot.clearContent();
			bzt.treeDLRWalker(root,generateDot);
			String dotFileStage =  generateDot.getResult();    		
			//makeDotAndPic(String.format("%s/stage/stage%d_m%d",prefix,i,traitors-i),dotFileStage,null);
			makeDotAndPic(String.format("%s/stage",prefix),String.format("stage%d_m%d",i,traitors-i),dotFileStage,null);
			    			
		}
		
		
		//生成单个节点的相关的信息, 图片，源码，描述    		
		for(String label : bzt.incomeSet.keySet()){
			//获取单独的节点
			ArrayList<Link> links = bzt.incomeSet.get(label);
			//清空树
			bzt.treeDLRWalker(root, resetAllInvisible);
			//隐藏边
			//bzt.treeDLRWalker(root, hideLink);
			
			//将直接节点标红，将边以及西相关路径标红，需要显示隐藏边
			MarkIndividualAction  markIndividual = new MarkIndividualAction(links);
			bzt.treeDLRWalker(root, markIndividual );
			String words =markIndividual.getWords();
			logger.debug("==========");
			logger.debug(words);
			
			
			generateDot.clearContent();
			bzt.treeDLRWalker(root,generateDot);
			String dotFileIndividual =  generateDot.getResult(); 
			
			//makeDotAndPic(String.format("%s/individual/%s",prefix,label),dotFileIndividual,words);
			makeDotAndPic(String.format("%s/individual",prefix),label,dotFileIndividual,words);
					
			logger.debug(label);
			
		}
		
		
		
		//生成数据结构
		for(int i =0 ;i < traitors+1;i++){
			bzt.treeDLRWalker(root, resetAllInvisible);
			TraceDataAction traceData = new TraceDataAction();
			Queue<Link> linkQueue = new LinkedList<Link>();
			Queue<Node> nodeQueue = new LinkedList<Node>();
			
			nodeQueue.add(root);
			
			bzt.treeBFSWalker(nodeQueue, linkQueue, traceData, i+1);
			String majority = traceData.getResult();
			makeDotAndPic(String.format("%s/stage",prefix),String.format("stage_majority_%d_m%d",i,traitors-i),majority,null);
			
			HashMap<String, String> indivisual_dots =  traceData.getIndividualResult();
			for(String label  : indivisual_dots.keySet()){
			     String content = indivisual_dots.get(label);
			     
				makeDotAndPic(String.format("%s/stage",prefix),String.format("stage_majority_%s_%d_m%d",label,i,traitors-i),content,null);
					
			}
			//logger.debug();
			    			
		}
		
		
	}
    
    private static void makeDotAndPic(String directory,String fileName, String dotContent,String words){
		FileManager fm = new FileManager();
		if(dotContent!= null){
			fm.WriteSrcFile(directory,fileName+".dot", dotContent);
		}
		
		if (words!=null){
			fm.WriteTxtFile(directory,fileName+".txt", words	);				
		}
		
		
		fm.mkDirIfNotExsits(fm.getPictureFile(directory,fileName+".svg").getPath());
		fm.createRunScript();
		/*
		MutableGraph g;
		try {
			g = Parser.read(dotContent);
			Graphviz.fromGraph(g).width(1000).render(Format.SVG).toFile(fm.getPictureFile(directory,fileName+".svg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
		}	
		*/
		
    }
    

}
