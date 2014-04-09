package TFS;

import java.io.IOException;
import java.util.*;

public class TFSClient {
	TFSMaster master;
	
	TFSClient(TFSMaster master){
		this.master = master;
	}
	
	public void createDirectory(String folderName){
		if(folderExists(folderName)){
			System.out.println("Error: Directory already exists");
			return;
		}
		master.allocateFolder(folderName);
		int loc = master.getFolderLocation(folderName);
		
		Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
		chunkserverTable.get((Integer)(loc)).createFolder(folderName);		
	}
	
	public void deleteDirectory(String folderName){
		
	}

	public void write(String filename, String data) throws IOException{
		if(fileExists(filename))
			delete(filename);
		
		int numOfChunks = num_chunks(data.length());
		List<Integer> chunkuuids = master.allocate(filename, numOfChunks);
		write_chunks(chunkuuids,data);
	}
	
	
	public void write_chunks(List<Integer> chunkuuids, String data) throws IOException{
		List<String> chunks = new ArrayList<String>();
		//System.out.println(data.length());
		int remainingLetters = data.length();
		for(int i=0; i<data.length(); i+= master.chunkSize){
			if(remainingLetters<= master.chunkSize){
				chunks.add(data.substring(i,i+remainingLetters));
				break;
			}
			chunks.add(data.substring(i, i+master.chunkSize));
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
	
	public int num_chunks(int size){
		if(size == 0)
			return 0;
		else if (size <= master.chunkSize)
			return 1;
		else{
			return (int)(Math.ceil((float)size/master.chunkSize));
		}

	}
	
	public void write_append(String filename,String data) throws IOException{
		if(!fileExists(filename)){
			System.out.println("Exception!");
			return;
		}
		int num_append_chunks = num_chunks(data.length());
		List<Integer> append_chunkuuids = master.alloc_append(filename, num_append_chunks);
		write_chunks(append_chunkuuids, data);
		
	}
	
	public boolean fileExists(String filename){
		return master.exists(filename);
	}
	
	public boolean folderExists(String filename){
		return master.folderExists(filename);
	}
	
	public String read(String filename) throws IOException{
		if(!fileExists(filename)){
			System.out.println("Exception!");
			return "";
		}
		List<String> chunks = new ArrayList<String>();
		List<Integer> chunkuuids = master.getUUIDS(filename);
		Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
		String data = "";
		
		for (int i = 0; i <chunkuuids.size(); i++){
			int chunkloc = master.getLocation(chunkuuids.get(i));
			String ch = chunkserverTable.get((Integer)(chunkloc)).read(chunkuuids.get(i));
			chunks.add(ch);
			data += ch;
		}
		
		return data;
	}
	
	public void delete(String filename){
		master.delete(filename);
	}
}