package TFS;

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
		
		storeLocalFile(client, "src\\test.txt", "1\\2\\5\\File5.txt");
		String s = new String(client.read("1\\2\\5\\File5.txt"));
	    System.out.println("Content : " + s);
	}
	
}
