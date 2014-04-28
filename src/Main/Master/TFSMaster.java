package Main.Master;

import Main.*;
import Main.ChunkServer.*;
import Main.Client.*;


import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;
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


/**
 *
 */
public class TFSMaster implements Serializable{
	
	public static final int numOfChunkservers = 3;
	
	public ObjectOutputStream outputToClient;
	public ObjectInputStream inputFromClient;
	public PrintWriter pw;
	public BufferedReader br;
	public ServerSocket serverSocket;
	public ClientHandlerForMaster csHandler;
	public int numOfClients = 0;
	public Semaphore chLock, conLock, dirLock;//locks to the data files
	
	public MyObject obj;
	//array of clients
	public TreeMap<Integer, HandleAClient> chunkservers = new TreeMap<Integer, HandleAClient>();
	public TFSClient client;
	

	public int chunkSize = 64;
	public int chunkRobin = 0;
	public Sequence counter = new Sequence();
    
	public Map<Integer, Integer> chunkserverTable; // Map chunkloc id to chunkserver PORT#
	public Map<String, List<Integer>> fileTable; // Map filename to chunk ids
	public Map<Integer, Integer> chunkTable; // Map chunk id to chunkloc id
	public Map<String, Integer> folderTable; // Map foldername to chunkloc id
	public List<String> folderList;
    
    
    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
		new TFSMaster();
	}

    /**
     *
     * @throws IOException
     */
    public TFSMaster() throws IOException {
    	obj = new MyObject();
        fileTable = new HashMap<String, List<Integer>>();
        chunkserverTable = new HashMap<Integer, Integer>();
        chunkTable = new HashMap<Integer, Integer>();
        folderList = new ArrayList<String>();
        folderTable = new HashMap<String,Integer>();
        chLock = new Semaphore(1);
        conLock = new Semaphore(1);
        dirLock = new Semaphore(1);

  
        for(int i = 0; i < this.numOfChunkservers; i++){
            //TFSChunkserver cs = new TFSChunkserver(""+i);
            chunkserverTable.put (i, 7501+i);
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
        try{
        	InetAddress addr = InetAddress.getByName("68.181.174.42");
			serverSocket = new ServerSocket(7500);
			System.out.println("Server started");
		} 
		catch(Exception ex){ 
			ex.printStackTrace();
			System.exit(0);
		}
        
        //Wait for Clients
        int client = 0;
        try{				
        	while(true){
        		Socket socket = serverSocket.accept();
        		Socket clientsocket;
        		if(client == 0)	//CLIENT1
        			clientsocket = new Socket("68.181.174.53", 7499-numOfClients);
        		else	//CLIENT2
        			clientsocket = new Socket("68.181.174.53", 7499-numOfClients);
        		client++;
				numOfClients++;
				System.out.println("Got client");
				
				//create an input stream and an output stream from the socket
				outputToClient = new ObjectOutputStream(clientsocket.getOutputStream());
				inputFromClient = new ObjectInputStream(socket.getInputStream());				

				try{
					//Create new Thread to Handle each client
					csHandler = new ClientHandlerForMaster(socket, this);
					new Thread(csHandler).start();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}						
			}	
		}

		catch(IOException ex){
			ex.printStackTrace();
		}
    }

    /**
     *
     * @return
     */
    public ObjectOutputStream getOutput(){
		return outputToClient;
	}

    /**
     *
     * @return
     */
	public ObjectInputStream getInput(){
		return inputFromClient;
	}
}

