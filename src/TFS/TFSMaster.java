package TFS;

import java.util.*;

public class TFSMaster {
    private int numChunks = 5;
    private int MAX_CHUNKS = 10;
    private int MAX_CHUNKS_PER_FILE = 100;
    private int chunkSize = 10;
    private int chunkRobin = 0;

    private Map<Integer, TFSChunkserver> servers;
    private Map<String, ArrayList<Integer>> files;
    private Map<ArrayList<Integer>, Integer> chunks;

    public TFSMaster() {
        files = new HashMap<String, ArrayList<Integer>>();
        servers = new HashMap<Integer, TFSChunkserver>();
        chunks = new HashMap<ArrayList<Integer>, Integer>();

        for(int i = 0; i < this.numChunks; i++){
            TFSChunkserver cs = new TFSChunkserver(""+i);
            servers.put(i, cs);
        }
    }

    protected Map getServers(){
        return this.servers;
    }

    protected List allocate(String filename, int numChunks){
        //TODO: finish code
        List<Integer> uuids = allocateChunks(numChunks);

        return uuids;
    }

    protected List allocateChunks(int numChunks){
        List<Integer> uuids = new ArrayList<Integer>();
        for(int i = 0; i < numChunks; i++){
            //TODO: finish code
        }

        return uuids;
    }

    protected List appendChunks(String filename, int numChunks){
        List<Integer> uuid = this.files.get(filename);
        List<Integer> a_uuids = allocateChunks(numChunks);

        uuid.addAll(a_uuids);

        return a_uuids;
    }

    protected int getLocation(List<Integer> uuid){
        return this.chunks.get(uuid);
    }

    protected List<Integer> getUUIDS(String filename){
        return this.files.get(filename);
    }

    protected boolean exists(String filename){
        return files.containsKey(filename);
    }

    protected void delete(String filename){
        List<Integer> uuids = this.files.get(filename);
        this.files.remove(filename);

        //TODO: finish code
    }

    //TODO: dump metadata method?
	
}