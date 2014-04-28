package Main.Master;

import Main.HandleAClient;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 *
 */
public class ChunkServerHandler extends HandleAClient {


    /**
     *
     * @param socket
     * @param server
     */
	public ChunkServerHandler(Socket socket, TFSMaster server){		
		super(socket,server);	//Constructor
		System.out.println("ChunkServer Handler spawned");
	}


    /**
     *	Not used - Just in case
     */
	public void run(){
		while(true){
		}
	}

}