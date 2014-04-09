package TFS;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class TFSMaster {
    private int numOfChunkservers = 3;
    public int chunkSize = 10;
    public int chunkRobin = 0;
    public Sequence counter = new Sequence();
    

    private Map<Integer, TFSChunkserver> chunkserverTable; // Map chunkloc id to chunkserver id
    private Map<String, List<Integer>> fileTable; // Map filename to chunk ids
    private Map<Integer, Integer> chunkTable; // Map chunk id to chunkloc id

    public TFSMaster() {
        fileTable = new HashMap<String, List<Integer>>();
        chunkserverTable = new HashMap<Integer, TFSChunkserver>();
        chunkTable = new HashMap<Integer, Integer>();

        for(int i = 0; i < this.numOfChunkservers; i++){
            TFSChunkserver cs = new TFSChunkserver(""+i);
            chunkserverTable.put (i, cs);
        }
    }

    protected Map getServers(){
        return this.chunkserverTable;
    }

    protected List allocate(String filename, int numChunks){
        List<Integer> chunkuuids = allocateChunks(numChunks);
        fileTable.put (filename, chunkuuids);
        return chunkuuids;
    }

    protected List allocateChunks(int numChunks){
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

    protected List alloc_append(String filename, int numChunks){
        List<Integer> uuids = this.fileTable.get(filename);
        List<Integer> append_uuids = allocateChunks(numChunks);
        uuids.addAll(append_uuids);

        return append_uuids;
    }

    protected int getLocation(int uuid){
        return this.chunkTable.get(uuid);
    }

    protected List<Integer> getUUIDS(String filename){
        return this.fileTable.get(filename);
    }

    protected boolean exists(String filename){
        return fileTable.containsKey(filename);
    }

    protected void delete(String filename){
        List<Integer> uuids = this.fileTable.get(filename);
        this.fileTable.remove(filename);
        Date date= new Date();
   	    Timestamp ts = new Timestamp(date.getTime());
   	    String deleted_filename = "/hidden/deleted/" + ts + filename;
   	    fileTable.put(deleted_filename, uuids);
   	    System.out.println("Deleted file: " + filename + " renamed to " + deleted_filename + " ready for gc ");

    }

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
    	
    }
	
}

