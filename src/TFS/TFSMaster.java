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
	ObjectOutputStream outputToClient;
	ObjectInputStream inputFromClient;
	PrintWriter pw;
	BufferedReader br;
	ServerSocket serverSocket;
	ClientHandlerForMaster csHandler;
	
	MyObject obj;
	//array of clients
	TreeMap<Integer, HandleAClient> chunkservers = new TreeMap<Integer, HandleAClient>();
	TFSClient client;
	
    int numOfChunkservers = 1;
    int chunkSize = 64;
    int chunkRobin = 0;
    Sequence counter = new Sequence();
    
    Map<Integer, TFSChunkserver> chunkserverTable; // Map chunkloc id to chunkserver id
    Map<String, List<Integer>> fileTable; // Map filename to chunk ids
    Map<Integer, Integer> chunkTable; // Map chunk id to chunkloc id
    
    Map<String, Integer> folderTable; // Map foldername to chunkloc id
    List<String> folderList;

    public static void main(String[] args) throws IOException{
		new TFSMaster();
	} 
    
    public TFSMaster() throws IOException {
    	obj = new MyObject();
        fileTable = new HashMap<String, List<Integer>>();
        chunkserverTable = new HashMap<Integer, TFSChunkserver>();
        chunkTable = new HashMap<Integer, Integer>();
        folderList = new ArrayList<String>();
        folderTable = new HashMap<String,Integer>();

        //TODO
        /*
        for(int i = 0; i < this.numOfChunkservers; i++){
            TFSChunkserver cs = new TFSChunkserver(""+i);
            chunkserverTable.put (i, cs);
        }
        */
        
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
			serverSocket = new ServerSocket(7500);
			System.out.println("Server started");
		} 
		catch(Exception ex){ 
			ex.printStackTrace();
			System.exit(0);
		}
        
        //Wait for Clients
        
        try{				
        	while(true){
        		Socket socket = serverSocket.accept();
				Socket clientsocket = new Socket("localhost", 7499);
				//Socket socket = serverSocket.accept();
				System.out.println("Got client");
				//create an input stream and an output stream from the socket
				outputToClient = new ObjectOutputStream(clientsocket.getOutputStream());
				inputFromClient = new ObjectInputStream(socket.getInputStream());				

				try{
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
    

    public ObjectOutputStream getOutput(){
		return outputToClient;
	}

	public ObjectInputStream getInput(){
		return inputFromClient;
	}
}

