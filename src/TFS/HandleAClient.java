package TFS;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 */
public class HandleAClient implements Runnable{
	TFSMaster server;
	TFSChunkserver chunkserver;
	TFSClient client;
	Socket socket;
	protected ObjectOutputStream outputToClient;
	protected ObjectInputStream inputFromClient;

	public HandleAClient(){}

    /**
     *
     * @param socket
     * @param client
     */
	public HandleAClient(Socket socket, TFSClient client){
		this.client = client;
		this.socket = socket;
		
		//outputToClient = client.getOutput();
		//inputFromClient = client.getInput();
	}

    /**
     *
     * @param socket
     * @param server
     */
	public HandleAClient(Socket socket, TFSMaster server){
		this.server = server;
		this.socket = socket; // initialize socket

		outputToClient = server.getOutput();
		inputFromClient = server.getInput();

		//System.out.println("client handler spawned");		
	}

    /**
     *
     * @param socket
     * @param chunkserver
     * @throws UnknownHostException
     * @throws IOException
     */
	public HandleAClient(Socket socket, TFSChunkserver chunkserver) throws UnknownHostException, IOException{
		this.chunkserver = chunkserver;
		this.socket = socket; // initialize socket

		outputToClient = chunkserver.getOutput();
		inputFromClient = chunkserver.getInput();

		//System.out.println("client handler spawned");		
	}


	////////SENDING METHODS////////////////

    /**
     *
     */
	public void sendObject(){
		try{
			server.obj.cmd = "from server";
			
			MyObject o = new MyObject();
			o.cmd = "hi";
			outputToClient.writeObject(o);
		
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	//getters

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

    /**
     *
     */
	public void run() {


	}


}