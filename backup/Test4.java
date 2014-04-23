package TFS;

import java.util.*;
import java.io.*;


public class Test4 {
	public Test4(){}
	
	public static void storeLocalFile(TFSClient client, String lpath, String filename) throws IOException{
		
		File f = new File(lpath);
		if(!f.exists()){
			System.out.println("Error: Source file does not exist");
			System.exit(0);
		}
		
		if(client.fileExists(filename)){
			System.out.println("Error: Destination file already exists");
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
		
		//File file = new File(lpath);
		byte[] b = client.fileToByte(f);
		client.write(filename, b);

	} 
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		
		String localpath = "";
		String TFSfilePath = "";
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Test4:\nEnter local pathname(i.e. src\\test123.txt) >> ");
		localpath = scan.nextLine();
		System.out.println("Enter TFS destination pathname (i.e. 1\\2\\5\\File5) >> ");
		TFSfilePath = scan.nextLine();
		
		storeLocalFile(client, localpath, TFSfilePath);
		
		String s = new String(client.read(TFSfilePath));
	    System.out.println("Content : " + s);
	}
	
}
