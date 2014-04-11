package TFS;

import java.io.*;
import java.util.*;

public class TFSClient {
	static TFSMaster master;
	
	TFSClient(TFSMaster master){
		this.master = master;
	}
	
	public void createFile(String fileName) throws IOException{
		if(fileExists(fileName)){
			System.out.println("Error: Files already exists");
			return;
		}
		int numOfChunks = 1;
		List<Integer> chunkuuids = master.allocate(fileName, numOfChunks);
		String b = " ";
		write_chunks(chunkuuids, b.getBytes());
	}
	
	public void createDirectory(String folderName) throws IOException{
		if(folderExists(folderName)){
			System.out.println("Error: Directory already exists");
			return;
		}
		master.allocateFolder(folderName);
		int loc = master.getFolderLocation(folderName);
		
		Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
		//chunkserverTable.get((Integer)(loc)).createFolder(folderName);		
	}
	
	public void deleteDirectory(String folderName) throws IOException{
		//Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
		if(!folderExists(folderName)){
			System.out.println("Directory Delete: File does not exist");
			return;
		}
		master.deleteDirectory(folderName);
	}

	public void write(String filename, byte[] data) throws IOException{
		
		int numOfChunks = num_chunks(data.length);
		List<Integer> chunkuuids = master.allocate(filename, numOfChunks);
		write_chunks(chunkuuids,data);
	}
	
	
	public static void write_chunks(List<Integer> chunkuuids, byte[] data) throws IOException{
		List<byte[]> chunks = new ArrayList<byte[]>();
		//System.out.println(data.length());
		int remainingLetters = data.length;
		for(int i=0; i<data.length; i+= master.chunkSize){
			if(remainingLetters<= master.chunkSize){
				chunks.add(Arrays.copyOfRange(data, i, i + remainingLetters));
				//chunks.add(data.substring(i,i+remainingLetters));
				break;
			}
			chunks.add(Arrays.copyOfRange(data, i, i + master.chunkSize));
			//chunks.add(data.substring(i, i+master.chunkSize));
			remainingLetters -= master.chunkSize;
		}
				
		Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
		for(int i =0; i<chunkuuids.size(); i++){
			int chunkuuid = chunkuuids.get(i);
			int chunkloc = master.getLocation(chunkuuid);
			//System.out.println(chunks.get(i));
			chunkserverTable.get((Integer)(chunkloc)).write(chunkuuid, chunks.get(i));
		}
		
	}
	
	public static int num_chunks(int size){
		if(size == 0)
			return 0;
		else if (size <= master.chunkSize)
			return 1;
		else{
			return (int)(Math.ceil((float)size/master.chunkSize));
		}

	}
	
	public void write_append(String filename,byte[] data) throws IOException{
		if(!fileExists(filename)){
			System.out.println("Exception!");
			return;
		}
		int num_append_chunks = num_chunks(data.length);
		List<Integer> append_chunkuuids = master.alloc_append(filename, num_append_chunks);
		write_chunks(append_chunkuuids, data);
		
	}
	
	public boolean fileExists(String filename){
		return master.exists(filename);
	}
	
	public boolean folderExists(String filename){
		return master.folderExists(filename);
	}
	
	public byte[] read(String filename) throws IOException{
		if(!fileExists(filename)){
			System.out.println("Exception!");
			return null;
		}
		List<byte[]> chunks = new ArrayList<byte[]>();
		List<Integer> chunkuuids = master.getUUIDS(filename);
		Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
		byte[] data;
		
		for (int i = 0; i <chunkuuids.size(); i++){
			int chunkloc = master.getLocation(chunkuuids.get(i));
			byte[] ch = chunkserverTable.get((Integer)(chunkloc)).read(chunkuuids.get(i));
			chunks.add(ch);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		for(int i=0; i<chunks.size(); i++){
			outputStream.write(chunks.get(i));
		}
		data = outputStream.toByteArray( );
		
		return data;
	}
	
	public void delete(String filename) throws IOException{
		if(!fileExists(filename)){
			System.out.println("Delete Error: File does not exist");
			return;
		}
		master.delete(filename);
	}
		
	
	
	public byte[] fileToByte (File file) throws IOException{

	    byte []buffer = new byte[(int) file.length()];
	    InputStream ios = null;
	    try {
	        ios = new FileInputStream(file);
	        if ( ios.read(buffer) == -1 ) {
	            throw new IOException("EOF reached while trying to read the whole file");
	        }        
	    } finally { 
	        try {
	             if ( ios != null ) 
	                  ios.close();
	        } catch ( IOException e) {
	        }
	    }

	    return buffer;
	}
	
	public List<Integer> getUUIDS (String filename){
		return master.getUUIDS(filename);
	}
	
	public String byteToString(byte[] _bytes)
	{
	    String file_string = "";

	    for(int i = 0; i < _bytes.length; i++)
	    {
	        file_string += (char)_bytes[i];
	    }

	    return file_string;    
	}
}