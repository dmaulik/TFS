package TFS;

import java.io.*;
import java.util.*;

public class App{
	
	public App(){	
	}
	
	public static void main (String[] args) throws IOException{
		TFSMaster master = new TFSMaster();
		TFSClient client = new TFSClient(master);
		
		 System.out.println( "\nWriting...");
		 
		 client.write("src/readme.txt", "This file tells you all about python that you ever wanted to know. Not every README is as informative as this one, but we aim to please. Never yet has there been so much information in so little space.");
		 System.out.println( "File exists? " + client.exists("src/readme.txt"));
		 System.out.println( client.read("src/readme.txt"));

		 
		    //test append, read after append
		    System.out.println( "\nAppending...");
		    client.write_append("src/readme.txt", "I'm a little sentence that just snuck in at the end.\n");
		    System.out.println( client.read("src/readme.txt"));

		    
		    // test delete
		    System.out.println( "\nDeleting...");
		    client.delete("src/readme.txt");
		    System.out.println( "File exists? " + client.exists("src/readme.txt"));

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

		    /*
		    // show structure of the filesystem
		    System.out.println( "\nMetadata Dump...");
		    master.dump_metadata();
		    */
	}
	
}