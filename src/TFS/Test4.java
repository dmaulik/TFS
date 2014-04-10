package TFS;

import java.io.*;

public class Test4 {
	public Test4(){}
	
	public static void storeLocalFile(TFSClient client, String lpath, String filename) throws IOException{
		
		//TODO Need to check whether file exists or not
		//If file(lpath) doesn't exist, give error
		
		if(client.fileExists(filename)){
			System.out.println("Error: Destination file already exists");
			return;
		}
		
		File file = new File(lpath);
		byte[] f = client.fileToByte(file);
		//System.out.println("It's writing text: "+client.byteToString(f));
		client.write(filename, f);

	} 
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		
		storeLocalFile(client, "src\\test.txt", "1\\2\\5\\File5.txt");
		String s = new String(client.read("1\\2\\5\\File5.txt"));
	    System.out.println("Text Decrypted : " + s);
	}
	
}
