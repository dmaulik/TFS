package Main;


import java.util.*;
import java.io.*;

/**
 *
 */
public class Restart{

    /**
     * Clear the config files
     * @param args
     * @throws IOException
     */
	public static void main (String[] args) throws IOException{
		
		System.out.println("Clearing config files");
		
		File f0 = new File("chconfig.csv");
	    f0.delete();
	    f0.createNewFile();
	    
	    File f1 = new File("config.csv");
	    f1.delete();
	    f1.createNewFile();
	    
	    File f2 = new File("dirconfig.csv");
	    f2.delete();
	    f2.createNewFile();
	    
	    System.out.println("Done...");
	}
	
}