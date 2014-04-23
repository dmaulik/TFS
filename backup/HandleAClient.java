package TFS;

import java.io.*;
import java.net.*;
import java.util.*;


public class HandleAClient implements Runnable{
	TFSMaster server;
	Socket socket;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;


	public HandleAClient(){}

	public HandleAClient(Socket socket, TFSMaster server){
		this.server = server;
		this.socket = socket; // initialize socket

		output = server.getOutput();
		input = server.getInput();

		System.out.println("client handler spawned");		
	}

	////////SENDING METHODS////////////////
	
	public void sendObject(){
		try{
			server.obj.setString("from server");
			MyObject o = new MyObject();
			o.setString("hi");
			output.writeObject(o);
		
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	//getters
	public ObjectOutputStream getOutput(){
		return output;
	}

	public ObjectInputStream getInput(){
		return input;
	}


	public void run() {


	}


}