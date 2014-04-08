package TFS;

import java.util.*;

public class TFSMaster {
    private int numChunks = 5;
    private int MAX_CHUNKS = 10;
    private int MAX_CHUNKS_PER_FILE = 100;
    private int chunkSize = 10;
    private int chunkRobin = 0;

    private Map<Integer, TFSChunkserver> servers;

    //self.filetable = {} TODO: change to HashMaps
    //self.chunktable = {}

    public TFSMaster() {
        servers = new HashMap<Integer, TFSChunkserver>();

        for(int i = 0; i < this.numChunks; i++){
            TFSChunkserver cs = new TFSChunkserver(i);
            servers.put(i, cs);
        }
    }

    private Map getServers(){
        return this.servers;
    }
	
}