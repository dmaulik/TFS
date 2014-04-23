package TFS;

import java.util.*;
import java.io.*;

public class Test3 {
	
	public static void delete(File f, TFSClient client) throws IOException{
		if(f.isDirectory()){
			String []s = f.list();
			if(s!=null){
				for(int i = 0; i< s.length; i++){
					File temp = new File(f.toString() + "\\" + s[i]);
					System.out.println(temp.toString());
					delete(temp, client);
				}	
			} 
			else 
				client.deleteDirectory(f.toString());
		} 
		else 
			client.delete(f.toString());
	}
	
	public static void main(String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		String pathName = "1\\2";
				
		Scanner scan = new Scanner(System.in);
		System.out.println("Test3:\nEnter folder's pathname (i.e. 1\\2) >> ");
		pathName = scan.nextLine();
		File f = new File(pathName);
		client.deleteDirectory(pathName);
		
	}
}
