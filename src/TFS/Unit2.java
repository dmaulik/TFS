package TFS;

import java.io.*;
import java.util.*;

//this us exactly the same as Test2.java
public class Unit2{

  public Unit2(){

	}

	public static void createFiles(TFSMaster master, TFSClient client, String pathName, int n) throws IOException{
		if(!client.folderExists(pathName)){
			System.out.println("Folder doesn't exist");
			System.exit(0);
		}

		for(int i =1; i < n+1; i++){
			File a = new File(pathName + "\\" + "File" + i);
			//System.out.println(a.toString())
			client.createFile(pathName + "\\" + "File" + i);

		}

		//File dir = new File(pathName);
		//String []s = dir.list();
		List<String> s = master.folderInDirectory(pathName);
		if(s.size()>0){		
			for(int i = 0; i< s.size(); i++){
				//File temp = new File(pathName + "\\" + s.get(i));
				//System.out.println(s[i]);
				createFiles(master, client, s.get(i), n);
			}	
		}
	}
	
  public static void main (String[] args) throws IOException{
    TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		String pathName = "1\\2";
		int n = 5;

		Scanner scan = new Scanner(System.in);
		System.out.println("Test2:\nEnter folder's pathname (i.e. 1\\2) >> ");
		pathName = scan.nextLine();
		System.out.println("Enter integer n >> ");
		n = scan.nextInt();
		createFiles(master, client, pathName, n);

  }
}
