package TFS;

import java.io.*;
import java.util.*;

public class TFSChunkserver 
{
	protected String chunkLocation;
	protected Map<Integer, byte[]> chunkTable; //chunkID to fileName
	protected String root;
	protected String local_filesystem_root;
	
	TFSChunkserver(String chunkloc){
		this.chunkLocation = chunkloc;
		this.chunkTable = new HashMap<Integer, byte[]>();
		this.root = "src";
		this.local_filesystem_root = "/tmp/gfs/chunks" + chunkLocation.toString();
		//createFolder(this.local_filesystem_root);
	}
	
	public void createFolder(String folderName){
		File dir = new File(folderName);

		// if the directory does not exist, create it
		if (!dir.exists()) {
		    System.out.println("Creating directory: " + folderName);
		    boolean result = dir.mkdir();  

		    if(result) 
		       System.out.println("Folder created");  
		}else{
			System.out.println("Directory already exists");
		}
    }

	
	public void createFile (int chunkuuid){
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
	}
	
	public void write (int chunkuuid, byte[] chunk) throws IOException
	{
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
		
		//FileWriter fw = new FileWriter(file.getAbsoluteFile());
		FileOutputStream fos = new FileOutputStream(file);
		//bw.write(chunk);
		fos.write(chunk);
		chunkTable.put(chunkuuid, local_filename.getBytes());
		fos.flush();
		fos.close();
	}


	public byte[] read (int chunkID) throws IOException
	{
		byte[] data = null;
		String localFilename = getFileName(chunkID);
		String currentLine;
		//FileInputStream fis = new FileInputStream(new File(localFilename));
		BufferedReader br = new BufferedReader(new FileReader(localFilename));
		while ((currentLine = br.readLine()) != null ){
			data = currentLine.getBytes();
		}
		
		br.close();
		return data;
	}
	
	public String getFileName (int chunkID)
	{	
		return root + "\\" + Integer.toString(chunkID) + ".csv";
	}
	
	public void removeChunk(int chunkID){
		File f = new File(root + "\\" + Integer.toString(chunkID) + ".csv");
		f.delete();
	}

}
