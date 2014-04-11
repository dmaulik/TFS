package TFS;

import java.util.*;
import java.io.*;

public class Test7 {

    public Test7() {}

    public static void countFile (TFSClient client, String filename) throws IOException {
    	//If the input TFS file does not exist then return an error
    	int numOfLogicalFiles;
    	if (!client.fileExists(filename))
    	{
    		System.out.println("Error: Input TFS file does not exists");
		System.exit(0);
    	}
    	//count the number of logical files stored in a TFS file
    	//read the size and payload pairs in the specified file name
    	
    	else {
    		
    	}
    }

    public static void main (String[] args) throws Exception{
		  TFSMaster master = new TFSMaster();
	  	TFSClient client = new TFSClient(master);

		

	}
  
}
