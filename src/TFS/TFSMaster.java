package TFS;

import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

public class TFSMaster {
    private int numOfChunkservers = 1;
    public int chunkSize = 10;
    public int chunkRobin = 0;
    public Sequence counter = new Sequence();
    

    private Map<Integer, TFSChunkserver> chunkserverTable; // Map chunkloc id to chunkserver id
    private Map<String, List<Integer>> fileTable; // Map filename to chunk ids
    private Map<Integer, Integer> chunkTable; // Map chunk id to chunkloc id
    
    private Map<String, Integer> folderTable; // Map foldername to chunkloc id
    private List<String> folderList;

    public TFSMaster() throws IOException {
        fileTable = new HashMap<String, List<Integer>>();
        chunkserverTable = new HashMap<Integer, TFSChunkserver>();
        chunkTable = new HashMap<Integer, Integer>();
        folderList = new ArrayList<String>();
        folderTable = new HashMap<String,Integer>();

        for(int i = 0; i < this.numOfChunkservers; i++){
            TFSChunkserver cs = new TFSChunkserver(""+i);
            chunkserverTable.put (i, cs);
        }
        
        //Populate files
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader("config.csv"));
        line = br.readLine();
        while((line = br.readLine()) != null){
        	String[] filenames = line.split(",");
        	List<Integer> IDs = new ArrayList<Integer>();
        	for(int i= 1; i<filenames.length ; i++){
        		IDs.add(Integer.parseInt(filenames[i]));
        	}
        	fileTable.put(filenames[0], IDs);
      	
        }
        br.close();
        
        //Populate folders
        line = "";
        br = new BufferedReader(new FileReader("dirconfig.csv"));
        while((line = br.readLine()) != null){
        	String[] filenames = line.split(",");
        	folderList.add(filenames[0]);
        	folderTable.put(filenames[0], Integer.parseInt(filenames[1]));
        }
        br.close();
    }

    protected Map getServers(){
        return this.chunkserverTable;
    }
    
    protected void allocateFolder(String folderName) throws IOException{
    	int chunkloc = chunkRobin;
    	chunkRobin = (chunkRobin +1)%numOfChunkservers;
    	folderList.add(folderName);    	
    	folderTable.put(folderName, chunkloc);
    	
    	FileWriter fw = new FileWriter("dirconfig.csv", true);
       
        String s = folderName + "," + chunkloc + "\r\n";
        fw.append(s);
        fw.flush();
        fw.close();
    }
   
    
    protected List allocate(String filename, int numChunks) throws IOException{
    	FileWriter fw = new FileWriter("config.csv", true);
        List<Integer> chunkuuids = allocateChunks(numChunks);
        fileTable.put (filename, chunkuuids);
        
        String s = "\r\n" + filename;
        for(int i = 0; i < chunkuuids.size(); i++){
        	s += "," + chunkuuids.get(i);
        }
        fw.append(s);
        fw.flush();
        fw.close();
        return chunkuuids;
    }

    protected List allocateChunks(int numChunks) throws IOException{
        List<Integer> chunkuuids = new ArrayList<Integer>();

        for(int i = 0; i < numChunks; i++){
            int chunkuuid = counter.nextValue();
            int chunkloc = chunkRobin;
            chunkTable.put (chunkuuid, chunkloc);
            chunkuuids.add(chunkuuid);
            chunkRobin = (chunkRobin +1)%numOfChunkservers;
            
        }

        return chunkuuids;
    }

    protected List alloc_append(String filename, int numChunks) throws IOException{
        List<Integer> uuids = this.fileTable.get(filename);
        List<Integer> append_uuids = allocateChunks(numChunks);
        uuids.addAll(append_uuids);

        return append_uuids;
    }

    protected int getLocation(int uuid){
        return this.chunkTable.get(uuid);
    }

    protected int getFolderLocation(String foldername){
    	return this.folderTable.get(foldername);
    }
    
    protected List<Integer> getUUIDS(String filename){
        return this.fileTable.get(filename);
    }

    protected boolean exists(String filename){
        return fileTable.containsKey(filename);
    }
    
    protected boolean folderExists(String foldername){
    	File dir = new File(foldername);
    	return dir.exists();
    	//return folderList.contains(foldername);
    }
    protected void deleteDirectory(String folderName) throws IOException{
    	System.out.println("Size:"+ folderList.size());
    	List<String>arr = new ArrayList<String>();
    	for(int i = 0; i < folderList.size(); i++){
    		String name = folderList.get(i);
    		if(folderName.length() <= name.length()){
    			//System.out.println("Foldername: " + folderName);
        		//System.out.println("Ss: " + name.substring(0, folderName.length()));
        		if(folderName.equals(name.substring(0, folderName.length()))){
        			arr.add(name);
        		}
    		}
    	}
    	for (int i = 0; i < arr.size(); i++){
    		System.out.println("Deleting: " + arr.get(i));
    		folderList.remove(arr.get(i));
    		folderTable.remove(arr.get(i));
    	}
    	File f = new File("dirconfig.csv");
        f.delete();
        f.createNewFile();
        
        FileWriter fw = new FileWriter(f);
        for(int i =0; i< folderList.size(); i++){
        	String s = folderList.get(i) + "," + folderTable.get(folderList.get(i)) + "\r\n";
        	fw.append(s);
            fw.flush();
        }
        fw.close();
        
    	List<String>temp = new ArrayList<String>();
    	for(Map.Entry<String, List<Integer>> e : fileTable.entrySet()){
    		if(folderName.equals(e.getKey().substring(0, folderName.length()))){
    			temp.add(e.getKey());
    		}
    	}
    	
    	for(int i=0; i< temp.size(); i++){
    		System.out.println("Deleting: " + temp.get(i));
    		delete(temp.get(i));
    	}
    }
    
    protected void delete(String filename) throws IOException{
        List<Integer> uuids = this.fileTable.get(filename);
        this.fileTable.remove(filename);
        
        File f = new File("config.csv");
        f.delete();
        f.createNewFile();
        
        FileWriter fw = new FileWriter(f);
        for(Map.Entry<String, List<Integer>> e : fileTable.entrySet()){
        	String s = "\r\n" + e.getKey();
            for(int i = 0; i < e.getValue().size(); i++){
            	s += "," + e.getValue().get(i);
            }
            fw.append(s);
            fw.flush();
        }
        fw.close();
        //Date date= new java.util.Date();
   	    //Timestamp ts = new Timestamp(date.getTime());
   	    //String deleted_filename = "/hidden/deleted/" + ts + filename;
   	    //fileTable.put(deleted_filename, uuids);
   	    //System.out.println("Deleted file: " + filename + " renamed to " + deleted_filename + " ready for gc ");
    }

    /*
    public void dump_metadata() throws IOException{ 	
    	System.out.println("Filetable: ");
    	for(Map.Entry entry : fileTable.entrySet()){
    		System.out.println(entry.getKey().toString() + entry.getValue().toString());	// ?????
    	}
    	System.out.println("Chunkservers: " + chunkserverTable.size());
    	System.out.println("Chunkserver Data: ");
    	for(Map.Entry entry : chunkTable.entrySet()){
    		int chunkLoc = (int)(entry.getValue());
    		int chunkID = (int)(entry.getKey());
    		String ch = chunkserverTable.get(chunkLoc).read(chunkID);
    		System.out.println(" "+ entry.getValue().toString() + ", " + entry.getKey().toString() +  "," + ch);// prints chunkLoc, chunkID, ch
    		
    	}
    	
    }*/
	
}

