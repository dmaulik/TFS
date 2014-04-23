package TFS;

import java.util.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.*;
import java.awt.*;
import java.awt.event.*;


public class TFSMaster implements Serializable{
	private ObjectOutputStream outputToClient;
	private ObjectInputStream inputFromClient;
	private PrintWriter pw;
	private BufferedReader br;
	private ServerSocket serverSocket;
	
	public MyObject obj;
	//array of clients
	private TreeMap<Integer, HandleAClient> clients = new TreeMap<Integer, HandleAClient>();

    private int numOfChunkservers = 4;
    public int chunkSize = 64;
    public int chunkRobin = 0;
    public Sequence counter = new Sequence();
    
    private Map<Integer, TFSChunkserver> chunkserverTable; // Map chunkloc id to chunkserver id
    private Map<String, List<Integer>> fileTable; // Map filename to chunk ids
    private Map<Integer, Integer> chunkTable; // Map chunk id to chunkloc id
    
    private Map<String, Integer> folderTable; // Map foldername to chunkloc id
    private List<String> folderList;

    //public static void main(String[] args) throws IOException{
	//	new TFSMaster();
	//} 
    
    public TFSMaster() throws IOException {
    	obj = new MyObject();
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
        
        //Populate chunkID -> chunkLoc mapping
        line = "";
        br = new BufferedReader(new FileReader("chconfig.csv"));
        while((line = br.readLine()) != null){
        	String[] filenames = line.split(",");
        	chunkTable.put(Integer.parseInt(filenames[0]), Integer.parseInt(filenames[1]));
        	int i = counter.nextValue();	//so that chunkID are still unique
        }
        br.close();
        
        //Start the server
        /*
        try{
			serverSocket = new ServerSocket(7500);
			System.out.println("Server started");
		} 
		catch(Exception ex){ 
			ex.printStackTrace();
			System.exit(0);
		}*/
        
        //Wait for Clients
        /*
        try{			
			while(true){				
				Socket socket = serverSocket.accept();
				System.out.println("Got client");
				//create an input stream and an output stream from the socket
				outputToClient = new ObjectOutputStream(socket.getOutputStream());
				inputFromClient = new ObjectInputStream(socket.getInputStream());				

				try{

					Integer clientNo = (Integer)inputFromClient.readObject();
					System.out.println((Integer)clientNo);

					switch(clientNo){
					
					//Client
					case 0://TODO

					//ChunkServers
					case 1: ChunkServerHandler csHandler = new ChunkServerHandler(socket, this);
							clients.put(clientNo,csHandler);
							new Thread(csHandler).start();		
							break;
					/*
					case 2: KitsManagerHandler kitsManHandler = new KitsManagerHandler(socket, this);
							clients.put(clientNo,kitsManHandler);
							new Thread(kitsManHandler).start();		
							break;						

					case 3: FactoryProductionHandler factoryProdHandler = new FactoryProductionHandler(socket, this);
							clients.put(clientNo,factoryProdHandler);
							new Thread(factoryProdHandler).start();		
							break;								

					case 4: GantryRobotManHandler gantryRobotHandler = new GantryRobotManHandler(socket, this);
							clients.put(clientNo,gantryRobotHandler);
							new Thread(gantryRobotHandler).start();		
							break;

					case 5: LaneHandler lanehandler = new LaneHandler(socket,this);
							clients.put(clientNo, lanehandler);
							new Thread(lanehandler).start();
							break;

					case 6: PandKHandler pandkHandler = new PandKHandler(socket,this);
							clients.put(clientNo, pandkHandler);
							new Thread(pandkHandler).start();
							break;
					
					default:

					}

				}
				catch(Exception ex){
					ex.printStackTrace();
				}						
			}	
		}

		catch(IOException ex){
			ex.printStackTrace();
		}*/
    }

    protected Map getServers(){
        return this.chunkserverTable;
    }
    
    protected List<String> folderInDirectory(String folderName){
    	List<String>arr = new ArrayList<String>();
    	for(int i = 0; i < folderList.size(); i++){
    		String name = folderList.get(i);
    		if(folderName.length() < name.length()){
    			if(folderName.equals(name.substring(0, folderName.length()))){
        			arr.add(name);
        		}
    		}
    	}
    	return arr;
    }
    
    protected void allocateFolder(String folderName) throws IOException{
    	int serverloc = chunkRobin;
    	chunkRobin = (chunkRobin +1)%numOfChunkservers;
    	folderList.add(folderName);    	
    	folderTable.put(folderName, serverloc);
    	System.out.println(folderName + " is created");
    	
    	FileWriter fw = new FileWriter("dirconfig.csv", true);
       
        String s = folderName + "," + serverloc + "\r\n";
        fw.append(s);
        fw.flush();
        fw.close();
    }
   
    
    protected List allocate(String filename, int numChunks) throws IOException{
    	FileWriter fw = new FileWriter("config.csv", true);
        List<Integer> chunkuuids = allocateChunks(numChunks);
        fileTable.put (filename, chunkuuids);
        System.out.println(filename + " is created");
        
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
        FileWriter fw = new FileWriter("chconfig.csv", true);
        String s = "";
        for(int i = 0; i < numChunks; i++){
            int chunkuuid = counter.nextValue();
            int chunkloc = chunkRobin;
            chunkTable.put (chunkuuid, chunkloc);
            chunkuuids.add(chunkuuid);
            chunkRobin = (chunkRobin +1)%numOfChunkservers;
            s += chunkuuid + "," + chunkloc + "\r\n";
            fw.flush();
        }
        fw.append(s);
        fw.flush();
        fw.close();

        return chunkuuids;
    }

    protected List alloc_append(String filename, int numChunks) throws IOException{
        List<Integer> uuids = this.fileTable.get(filename);
        List<Integer> append_uuids = allocateChunks(numChunks);
        uuids.addAll(append_uuids);
        
        //Modify the config file
        /*
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader("config.csv"));
        line = br.readLine();
        while((line = br.readLine()) != null){
        	String[] filenames = line.split(",");
        	if(filenames[0].equals(filename)){
        		for(int i = 0; i<append_uuids.size(); i++){
        			line += "," + append_uuids.get(i);  
        		}
        	}
        }
        br.close();
        */
    
        //FIX Filetable
        fileTable.remove(filename);
        fileTable.put(filename, uuids);
        
       //Rewrite config.csv
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
    	
    	return folderTable.containsKey(foldername);
    	//File dir = new File(foldername);
    	//return dir.exists();
    	//return folderList.contains(foldername);
    }
    protected void deleteDirectory(String folderName) throws IOException{
    	//System.out.println("Size:"+ folderList.size());
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
        
      //remove chconfig log
        File f0 = new File("chconfig.csv");
        f0.delete();
        f0.createNewFile();
        FileWriter fw0 = new FileWriter("chconfig.csv", true);
        String s0 = "";
        for(int i=0 ; i<uuids.size(); i++){
        	int chunkLoc = chunkTable.get(uuids.get(i));
        	TFSChunkserver cs = chunkserverTable.get(chunkLoc);
        	cs.removeChunk(uuids.get(i));
        	chunkTable.remove(uuids.get(i));
        }
        
        for(Map.Entry<String, List<Integer>> e : fileTable.entrySet()){
            s0 +=  e.getKey() + "," + e.getValue() + "\r\n";
            fw0.append(s0);
            fw0.flush();
        }
        fw0.close();
        
        //remove config log
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
    public ObjectOutputStream getOutput(){
		return outputToClient;
	}

	public ObjectInputStream getInput(){
		return inputFromClient;
	}
}

