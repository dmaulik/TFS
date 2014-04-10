package TFS;

import java.util.*;
import java.io.*;

public class Test5 {

public static void storeTFSFile(TFSClient client, String filename, String lpath) throws IOException{
		
		if(!client.fileExists(filename)){
			System.out.println("File doesn't exist in TFS");
			return;
		}
		
		File file = new File(lpath);
		byte[] content = client.read(filename);
		
		FileOutputStream fos = new FileOutputStream(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		fos.write(content);
		fos.flush();
		fos.close();


	} 
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		
		String filename = "1\\2\\5\\File5";
		String destination = "src\\test.csv";
		String a = "QEQWEQWEQWEQWEQWEQWEWQE";
		client.write(filename, a.getBytes());
		storeTFSFile(client, filename, destination);
	}
}
