package TFS;

import java.util.*;
import java.io.*;

public class Unit7 {

    public Unit7() {}

    public static void countFile (TFSClient client, String filename) throws IOException {
    	//If the input TFS file does not exist then return an error
    	if (!client.fileExists(filename))
    	{
    		System.out.println("Error: Input TFS file does not exists");
    		System.exit(0);
    	}
    	//count the number of logical files stored in a TFS file
    	//read the size and payload pairs in the specified file name
    	
    	List<Integer> ids = client.getUUIDS(filename);
    	//File f= new File(filename);
    	byte []b = client.read(filename);
    	//client.fileToByte(f);
    	System.out.println("Size of \"" + filename + "\" is " + ids.size() + " chunk(s)");
    	System.out.println("Size of \"" + filename + "\" is " + b.length + " byte(s)");
    	int delta=0;
    	byte[] size=client.seekByteSize(delta, filename);
    	
    	String nB = new String(size);
		int nB2 = Integer.parseInt(nB);
		//System.out.println(nB2);
		int count=0;
		while(delta<b.length){
			count++;
			//System.out.println(delta);
			size=client.seekByteSize(delta, filename);
	    	nB = new String(size);
			nB2 = Integer.parseInt(nB);
			delta=delta+(4+nB2);
		}
		System.out.println("The TFSfile contains "+count+" logical files");
    }

    public static void main (String[] args) throws Exception{
    	
		TFSMaster master = new TFSMaster();
	  	TFSClient client = new TFSClient(master);
		String filename = "1\\File5";

		Scanner scan = new Scanner(System.in);
		System.out.println("Test7:\nEnter TFS file pathname (should be the same as TFS pathname in Test 6) (i.e. 1\\2\\5\\File3) >> ");
		filename = scan.nextLine();
		countFile(client, filename);		
	}
  
}
