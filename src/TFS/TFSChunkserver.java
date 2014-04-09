package TFS;

import java.io.*;
import java.util.*;

public class TFSChunkserver 
{
	protected String chunkLocation;
	protected Map<Integer, String> chunkTable;
	protected String root;
	protected String local_filesystem_root;
	
	TFSChunkserver(String chunkloc){
		this.chunkLocation = chunkloc;
		this.chunkTable = new HashMap<Integer, String>();
		this.root = "src";
		this.local_filesystem_root = "/tmp/gfs/chunks" + chunkLocation.toString();
		//if not os.access didn't have time to finish : )
	}
	
	public void write (int chunkuuid, String chunk) throws IOException
	{
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(chunk);
		chunkTable.put(chunkuuid, local_filename);
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
