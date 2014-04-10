package TFS;

import java.util.*;
import java.io.*;

public class Test2 {

	public Test2(){
	
	}
	
	public static void createFiles(TFSClient client, String pathName, int n) throws IOException{
		for(int i =1; i < n+1; i++){
			File a = new File(pathName + "\\" + "File" + i + ".txt");
			System.out.println(a.toString());
			//a.createNewFile();
			client.createFile(pathName + "\\" + "File" + i + ".txt");
		}
		
		File dir = new File(pathName);
		String []s = dir.list();
		
		if(s!=null){
			
			for(int i = 0; i< s.length; i++){
				File temp = new File(pathName + "\\" + s[i]);
				System.out.println(s[i]);
				if(temp.isDirectory()){
					createFiles(client, pathName+"\\" + s[i], n);
				}
			}	
		}
	}
	
	public static void main (String [] args) throws IOException{
		// TODO Auto-generated method stub
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		String pathName = "1\\2";
		int n = 5;
		
		createFiles(client, pathName, n);
		//File a = new File(pathName);
		//a.createNewFile();
	
	}
	
}
