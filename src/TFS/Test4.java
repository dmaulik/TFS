package TFS;

import java.io.*;

public class Test4 {
	public Test4(){}
	
	public static void storeLocalFile(TFSClient client, String lpath, String filename) throws IOException{
		
		File file = new File(lpath);
		byte[] f = client.fileToByte(file);
		//System.out.println("It's writing text: "+client.byteToString(f));
		client.write(filename, f);
		
		
	} 
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		storeLocalFile(client, "src/test.txt", "1\\2\\5\\File5.txt");
		System.out.println(client.read("1\\2\\5\\File5.txt"));
		//System.out.println(client.read("1\\2\\5\\File2.txt"));
	}
	
}
