package com.alan2lin.byzantine.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alan2lin.byzantine.App;

public class FileManager {
	static Logger logger = LoggerFactory.getLogger(FileManager.class);
	
	String outputBase = "output/";
	String picPath = "pic/";
	String srcPath = "src/";
	String txtPath = "txt/";
	
	
	
	
	public void mkDirIfNotExsits(String file){

		String strDir = null;

		try{
			strDir = file.substring(0,file.lastIndexOf('/')); 		
		}catch(Exception e){
			strDir = file.substring(0,file.lastIndexOf('\\'));
		}
				
		File dir = new File(strDir); 
		if(!dir.exists()) {
			  dir.mkdirs();
		   }     
	     
	}
	
	public String pathConcat(String path1,String path2){
		path1.replaceFirst("(\\|/)$", "");
		return path1.replaceFirst("(\\|/)$", "")+"/"+path2.replaceFirst("(\\|/)$", "");
	}
	
	void deleteOutput(){
		
	}
	
	
	public File getPictureFile(String directory,String fileName){
		String file = pathConcat(pathConcat( pathConcat(outputBase,directory) ,picPath),fileName);
		return new File(file);	
	}
	
	
	public void WriteFile(String prefix ,String fileName,String content){
		
		String FixedFileName = pathConcat(prefix, fileName) ;
		mkDirIfNotExsits(FixedFileName);
		
		File file = new File (FixedFileName);
		
		try (FileOutputStream fop = new FileOutputStream(file)) {

			  if (!file.exists()) {				   
					file.createNewFile();					
				   }

		   // get the content in bytes
		   byte[] contentInBytes = content.getBytes();

		   fop.write(contentInBytes);
		   fop.flush();
		   fop.close();

		   

		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
	
	public void WriteSrcFile(String directory,String fileName,String content){
		String path = pathConcat( pathConcat(outputBase,directory) ,srcPath);
		WriteFile(path,fileName,content);
		

		 }
		
	public void WriteTxtFile(String directory,String fileName,String content){
		String path = pathConcat( pathConcat(outputBase,directory) ,txtPath);
		WriteFile(path,fileName,content);		

		 }
	
	public void createRunScript(){
		File file = new File(pathConcat(outputBase,"rundot.bat")); 
		if(!file.exists()){
			StringBuffer sb = new StringBuffer();
			
			sb.append("@echo off \n");
			sb.append("for /R %%s in (*.dot) do ( \n");
			sb.append("     dot -Tsvg %%s   -o%%~ps..\\pic\\%%~ns.svg \n");
			sb.append(") \n");

			
			WriteFile(outputBase ,"rundot.bat", sb.toString());
		}
	}
	
}
