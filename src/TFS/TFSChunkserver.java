package TFS;

import java.io.File;
import java.util.*;

public class TFSChunkserver 
{
	protected String chunkLocation;
	protected String [] chunkTable;
	protected String root;
	
	public void write (int chunkuuid, File chunk)
	{
		String fileName;
		File f = open (fileName, "read");
	}
	
	private File open(String fileName, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public String read (int chunkID)
	{
		String data = " ";
		return data;
	}
	
	public String getFileName (int chunkID)
	{
		
		return root + "/" + Integer.toString(chunkID) + ".txt";
	}

}
