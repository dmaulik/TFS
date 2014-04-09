package TFS;

import java.io.*;
import java.util.*;

public class TFSChunkserver 
{
	protected String chunkLocation;
	protected ArrayList<String> chunkTable;
	protected String root;
	protected String local_filesystem_root;
	
	TFSChunkserver(String chunkloc){
		this.chunkLocation = chunkloc;
		this.chunkTable = new ArrayList<String>();
		this.local_filesystem_root = "/tmp/gfs/chunks" + chunkLocation.toString();
		//if not os.access didn't have time to finish : )
	}
	
	public void write (int chunkuuid, File chunk) throws FileNotFoundException, UnsupportedEncodingException
	{
		String fileName = null;
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println(chunk);
		writer.close();
	}
	
	private File open(String fileName, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public String read (int chunkID) throws IOException
	{
		String data = " ";
		String localFilename = getFileName(chunkID);
		String currentLine;
		BufferedReader br = new BufferedReader(new FileReader(localFilename));
		while ((currentLine = br.readLine()) != null ){
			data += currentLine;
		}
		br.close();
		return data;
	}
	
	public String getFileName (int chunkID)
	{	
		return root + "/" + Integer.toString(chunkID) + ".txt";
	}

}
