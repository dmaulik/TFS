package TFS;

import java.util.*;
import java.io.*;

public class Test5 {

public static void storeTFSFile(TFSClient client, String filename, String lpath) throws Exception{
		
		if(!client.fileExists(filename)){
			System.out.println("File doesn't exist in TFS");
			return;
		}
		
		File file = new File(lpath);
		byte[] content = client.read(filename);
		
		//Check directory
		try{
			file.createNewFile();
		}catch (Exception e){
			System.out.println("Error: Can't find directory");
			System.exit(0);
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		fos.write(content);
		fos.flush();
		fos.close();


	} 
	
	public static void main (String[] args) throws Exception{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		
		String filename = "1\\2\\5\\File5";
		String destination = "src\\test";
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Test5:\nEnter TFS pathname (i.e. 1\\2\\5\\File5) >> ");
		filename = scan.nextLine();
		System.out.println("Enter local destination pathname(i.e. src\\test.txt) >> ");
		destination = scan.nextLine();
		
		System.out.println("Reading from " + filename + " and writing to " + destination); // Writing to Local
		storeTFSFile(client, filename, destination);
		
	}
}
