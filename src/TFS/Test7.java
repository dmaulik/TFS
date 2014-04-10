package TFS;

import java.util.*;
import java.io.*;

public class Test7 {

    public Test7() {}

    public static void countFile (TFSClient client, String filename) throws IOException {
    	
    	if (!client.fileExists(filename))
    	{
    		System.out.println("Error: Input TFS file does not exists");
		System.exit(0);
    	}
    }

    public static void main (String[] args) throws Exception{
		  TFSMaster master = new TFSMaster();
	  	TFSClient client = new TFSClient(master);

		

	}
  
}
