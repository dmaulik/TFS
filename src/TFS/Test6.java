package TFS;

import java.util.*;
import java.io.*;

public class Test6 {
	
	public void appendImg(TFSClient client,String lpath, String filename){
		File f = new File(lpath);
		if(!f.exists()){
			System.out.println("Error: Source file does not exist");
			System.exit(0);
		}
		
		String []s = filename.split("\\\\");
		String folderName = "";
		if(s.length == 1){
			//create directly. No folder
		}
		else{
			folderName += s[0];
			for(int i=1; i<s.length-1; i++){
				folderName += ("\\" + s[i]);
			}
			if(!client.folderExists(folderName)){
				System.out.println("Error: Folder doesn't exist");
				System.exit(0);
			}
		}
		
	}

    public static void main (String[] args) throws Exception{
	  	TFSMaster master = new TFSMaster();
	  	TFSClient client = new TFSClient(master);

	
	}
}
