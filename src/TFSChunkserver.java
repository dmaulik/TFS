

import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
public class TFSChunkserver implements Serializable
{
	protected String chunkLocation;
	protected Map<Integer, byte[]> chunkTable; //chunkID to fileName
	//protected Map<Integer, locks> lockTable;
	protected String root;
	protected String local_filesystem_root;
	
	Socket serversocket;	//Socket to Client
	ServerSocket mysocket;	//My socket
	static ObjectOutputStream out;
	static ObjectInputStream in;
	ClientHandlerForChunkserver csHandler;	//Connection handler to client
	int versionNumber = 0; //Version number of the chunkserver

	
    /**
     *
     * @param args
     */
	public static void main(String[] args){
		new TFSChunkserver("0");
	}

    /**
     *
     * @param chunkloc
     */
	TFSChunkserver(String chunkloc){
		this.chunkLocation = chunkloc;
		this.chunkTable = new HashMap<Integer, byte[]>();

		this.root = "src";
		this.local_filesystem_root = "/tmp/gfs/chunks" + chunkLocation.toString();
		//createFolder(this.local_filesystem_root);
		try{
			mysocket = new ServerSocket(7501); 
			System.out.println("Chunkserver started");
		} 
		catch(Exception ex){ 
			ex.printStackTrace();
			System.exit(0);
		}
		
		int clients = 0;
		try{
		while(true){
			try{
				Socket socket = mysocket.accept();
				serversocket = new Socket("localhost", 7499-clients);	//ClientSocket
				clients++;
				//System.out.println("Got Client");
				out = new ObjectOutputStream(serversocket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());

				try{
					csHandler = new ClientHandlerForChunkserver(socket, this);
					new Thread(csHandler).start();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}	

			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		}catch (Exception e ){
			e.printStackTrace();
		}
	}

    /**
     *
     * @return
     */
	public ObjectOutputStream getOutput(){
		return out;
	}

    /**
     *
     * @return
     */
	public ObjectInputStream getInput(){
		return in;
	}
}
