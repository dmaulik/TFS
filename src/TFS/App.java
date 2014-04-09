package TFS;

import java.io.*;
import java.util.*;

public class App{
	
	public App(){	
	}
	
	
	public static void makeDirs(String parentDir, int i, int n){
		if(i > n)
			return;
		else{
			File d;
			String pD;
			if(i == 1){
				pD = ""+i;
				d = new File(pD);
			}
			else{
				pD = parentDir + "/" + i;
				d = new File(pD);
			}
			if (!d.exists()) {
			    System.out.println("Creating directory: " + d);
			    boolean result = d.mkdirs();  
			}
			makeDirs(pD+"/", i*2, n);
			makeDirs(pD+"/", i*2+1, n);
		}
	}
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		
		
		makeDirs("/Users/Elsen/Desktop/",1,7);
		/*
		File dir = new File("1/2/4");
		File dir2 = new File("1/2/5");

		boolean result = dir.mkdirs();
		boolean result2 = dir2.mkdirs();
		*/
		
		/*
		 System.out.println( "\nWriting...");
		 
		 client.write("src/readme.txt", "This file tells you all about python that you ever wanted to know. Not every README is as informative as this one, but we aim to please. Never yet has there been so much information in so little space.");
		 System.out.println( "File exists? " + client.fileExists("src/readme.txt"));
		 System.out.println( client.read("src/readme.txt"));

		 
		    //test append, read after append
		    System.out.println( "\nAppending...");
		    client.write_append("src/readme.txt", "I'm a little sentence that just snuck in at the end.\n");
		    System.out.println( client.read("src/readme.txt"));

		    
		    // test delete
		    System.out.println( "\nDeleting...");
		    client.delete("src/readme.txt");
		    System.out.println( "File exists? " + client.fileExists("src/readme.txt"));

		    // test exceptions
		    System.out.println( "\nTesting Exceptions...");
		    try{
		        client.read("src/readme.txt");
		    }
		    catch (Exception e){
		        System.out.println( "This exception should be thrown:" + e);
		    }
		    
		    try{
		        client.write_append("src/readme.txt", "foo");
		    }catch (Exception e){
		        System.out.println( "This exception should be thrown:" +e);
		    }

			System.out.println("Testing directory..");
		    client.createDirectory("test123");
			*/
		    /*
		    // show structure of the filesystem
		    System.out.println( "\nMetadata Dump...");
		    master.dump_metadata();
		    */
		    
	}
	
}