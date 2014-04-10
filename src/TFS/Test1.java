package TFS;

import java.io.*;
import java.util.*;

public class Test1{

	public static void makeDirs(TFSClient client, String parentDir, int i, int n) throws IOException{
		if(i > n)
			return;
		else{
			String pD;
			File d;
			if(i == 1){
				pD = ""+i;
				d = new File(pD);
			}
			else{
				pD = parentDir + "\\" + i;
				d = new File(pD);
			}
			client.createDirectory(d.toString());
			
			makeDirs(client, pD + "\\", i*2, n);
			makeDirs(client, pD + "\\", i*2+1, n);
		}
	}
	
	public static void makeFiles(String path, int n){
		
	}
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		String pathName = "";
		makeDirs(client, pathName ,1,7);
		    
	}
	
}