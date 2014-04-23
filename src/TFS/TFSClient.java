package TFS;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import java.util.*;

public class TFSClient implements Serializable{

	public static final int noOfChunkservers = 4;

	ServerSocket mysocket; // Socket for Master
	ObjectOutputStream out;	//Output stream
	ObjectInputStream in;	//InputStream
	
	HandlerForClient masterHandler;	//Handle connection with master
	//HandlerForClient chunkserverHandler;	//Handle connection with chunkserver
	List<HandlerForClient> chunkserverHandlers = new ArrayList<HandlerForClient>();
	
	static int chunkSize = 64;
	
	int clients = 0;	//Counter.
	
	public static void main(String[] args){
		new TFSClient();
	} 
	
	TFSClient(){
		try{
			mysocket = new ServerSocket(7499);
		} 
		catch(Exception ex){ 
			ex.printStackTrace();
			System.exit(0);
		}

		try{
			while(clients< 1+noOfChunkservers){

				Socket serversocket;
				if(clients == 0)
					serversocket = new Socket("localhost", 7500);//connection to Master
				else
					serversocket = new Socket("localhost", 7500+clients); //connection to Chunkserver
	
				Socket socket = mysocket.accept();	//Accept connection
				out = new ObjectOutputStream(serversocket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				
				try{
					if(clients == 0){
						masterHandler = new HandlerForClient(socket, this, out, in);
						System.out.println("Created masterHandler");
					}
					else{
						HandlerForClient chunkserverHandler = new HandlerForClient(socket,this, out, in);
						chunkserverHandlers.add(chunkserverHandler);
						System.out.println("Created chunkServerHandler");
					}
					//new Thread(masterHandler).start();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				clients++;
			}

			Scanner scan = new Scanner(System.in);
			int command;
			
			while(true){
				
				System.out.print("Enter Test Number:");
				command = scan.nextInt();
				
				if(command == 1){
					makeDirs("", 1,7);
				}
				else if(command == 2){
					createFiles("1\\2",5);
				}
				else if(command == 3){
					masterHandler.deleteDirectory("1\\2");
				}
				else if(command == 4){
					storeLocalFile("src\\test123.txt", "1\\2\\5\\File5");

					List<Integer> uuids = masterHandler.getUUIDs("1\\2\\5\\File5");
					int cs = masterHandler.getChunkserverToTalk(uuids.get(0));
					String s = new String(chunkserverHandlers.get(cs).read("1\\2\\5\\File5",uuids));
				    System.out.println("Content : " + s);
				}
				else if(command == 5){					
					String filename = "1\\2\\5\\File5";
					String destination = "src\\test";
					
					System.out.println("Reading from " + filename + " and writing to " + destination); // Writing to Local
					storeTFSFile(filename, destination);
				}
				else if(command == 6){
					String locfile = "src\\img.png";
					String tfspath = "1\\2\\5\\File3";
				  	
				  	append(locfile,tfspath);

				  	List<Integer> uuids = masterHandler.getUUIDs(tfspath);
				  	System.out.println(uuids);
				  	int cs = masterHandler.getChunkserverToTalk(uuids.get(0));
					String s = new String(chunkserverHandlers.get(cs).read(tfspath,uuids));
				    System.out.println("Content : " + s);
				}
				else if(command == 7){
					String filename = "1\\2\\5\\File3";
					countFile(filename);	
				}
				else if(command == 8){
					//Do it later
				}
				System.out.println("\nDone\n");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}		
	}
	
	
	//TEST #1
	public void makeDirs(String parentDir, int i, int n) throws IOException, ClassNotFoundException{
		if(i > n)
			return;
		else{
			String pD;
			File d;
			if(i == 1){
				pD = ""+i;
				d = new File(pD);
			}
			else{
				pD = parentDir + "\\" + i;
				d = new File(pD);
			}
			masterHandler.createDirectory(d.toString());	
			makeDirs(pD + "\\", i*2, n);
			makeDirs(pD + "\\", i*2+1, n);
		}
	}
	
	//TEST #2
	public void createFiles(String pathName, int n) throws IOException, ClassNotFoundException{
		if(!masterHandler.folderExists(pathName)){
			System.out.println("Folder doesn't exist");
			System.out.println("Terminating..");
			System.exit(0);
		}
		
		for(int i =1; i < n+1; i++){
			File a = new File(pathName + "\\" + "File" + i);
			masterHandler.createFile(pathName + "\\" + "File" + i);	
		}
		List <String> s = masterHandler.getFolderInDirectory(pathName);
		
		if(s.size()>0){		
			for(int i = 0; i< s.size(); i++){
				createFiles(s.get(i), n);
			}	
		}
	}
	
	//TEST #3 -> No Need to use a new method
	
	//TEST #4
	public void storeLocalFile(String lpath, String filename) throws IOException, ClassNotFoundException{
		
		File f = new File(lpath);
		if(!f.exists()){
			System.out.println("Error: Source file does not exist");
			System.out.println("Terminating..");
			System.exit(0);
		}
		
		if(masterHandler.fileExists(filename)){
			System.out.println("Error: Destination file already exists");
			System.out.println("Terminating..");
			System.exit(0);
		}
		
		String []s = filename.split("\\\\");
		String folderName = "";
		if(s.length == 1){
			//Do nothing - No folder
		}
		else{
			folderName += s[0];
			for(int i=1; i<s.length-1; i++){
				folderName += ("\\" + s[i]);
			}
			if(!masterHandler.folderExists(folderName)){
				System.out.println("Error: Folder doesn't exist");
				System.out.println("Terminating..");
				System.exit(0);
			}
		}
		
		byte[] b = masterHandler.fileToByte(f);
		List <Integer> uuids = masterHandler.write(filename, b);
		int cs = masterHandler.getChunkserverToTalk(uuids.get(0));
		chunkserverHandlers.get(0).write_chunks(uuids, b);
	} 
	
	//TEST #5
	public void storeTFSFile(String filename, String lpath) throws Exception{
		
		if(!masterHandler.fileExists(filename)){
			System.out.println("File doesn't exist in TFS");
			return;
		}	
		File file = new File(lpath);
		List<Integer> uuids = masterHandler.getUUIDs(filename);
		int cs = masterHandler.getChunkserverToTalk(uuids.get(0));
		byte[] content = chunkserverHandlers.get(cs).read(filename,uuids);
		
		//Check directory
		try{
			file.createNewFile();
		}catch (Exception e){
			System.out.println("Error: Can't find directory");
			System.out.println("Terminating..");
			System.exit(0);
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		fos.write(content);
		fos.flush();
		fos.close();
	} 
	
	//TEST #6
	public void append(String lpath, String filename) throws IOException, ClassNotFoundException{
		File f = new File(lpath);
		if(!f.exists()){
			System.out.println("Error: Source file does not exist");
			System.out.println("Terminating..");
			System.exit(0);
		}
		
		String []s = filename.split("\\\\");
		String folderName = "";
		if(s.length == 1){
			//Do nothing. No folder
		}
		else{
			folderName += s[0];
			for(int i=1; i<s.length-1; i++){
				folderName += ("\\" + s[i]);
			}
			if(!masterHandler.folderExists(folderName)){
				System.out.println("Error: Folder doesn't exist");
				System.out.println("Terminating..");
				System.exit(0);
			}
		}
		byte[] b = masterHandler.fileToByte(f);
		int size = b.length;
		String sizeInString = Integer.toString(size);
		byte[] sizeInByte = sizeInString.getBytes();
		byte[] newByte = new byte[4];
		
		byte[] zero = "0".getBytes();
		for(int i=0; i<4-sizeInByte.length; i++){
			newByte[i] = zero[0];
		}
		int k = 0;
		for(int i=4-sizeInByte.length; i<4; i++){
			newByte[i] = sizeInByte[k];
			k++;
		}
		
		String nB = new String(newByte);
		byte[] combined = new byte[b.length + 4];
		
		for (int i = 0; i < combined.length; ++i)
		    combined[i] = i < newByte.length ? newByte[i] : b[i - newByte.length];
		    
		String z = new String(combined);
		//System.out.println(z);
		
		if(!masterHandler.fileExists(filename)){
			List<Integer> uuids = masterHandler.write(filename, combined);
			int cs = masterHandler.getChunkserverToTalk(uuids.get(0));
			chunkserverHandlers.get(cs).write_chunks(uuids, combined );
		}
		else{
			List<Integer> uuids = masterHandler.write_append(filename, combined);
			int cs = masterHandler.getChunkserverToTalk(uuids.get(0));
			chunkserverHandlers.get(cs).write_chunks(uuids, combined);
		}
	
	}
	
	//TEST #7
	
	public void countFile (String filename) throws IOException, ClassNotFoundException {
    	//If the input TFS file does not exist then return an error
    	if (!masterHandler.fileExists(filename))
    	{
    		System.out.println("Error: Input TFS file does not exists");
    		System.out.println("Terminating..");
    		System.exit(0);
    	}
    	//count the number of logical files stored in a TFS file
    	//read the size and payload pairs in the specified file name
    	
    	//List<Integer> ids = client.getUUIDS(filename);
		List<Integer> ids = masterHandler.getUUIDs(filename);
    	System.out.println(ids);
	
		int cs = masterHandler.getChunkserverToTalk(ids.get(0));
    	byte []b = chunkserverHandlers.get(cs).read(filename,ids);
    	System.out.println("Size of \"" + filename + "\" is " + ids.size() + " chunk(s)");
    	System.out.println("Size of \"" + filename + "\" is " + b.length + " byte(s)");
    	int delta=0;
    	byte[] size= chunkserverHandlers.get(cs).seekByteSize(delta, filename,ids);
    	
    	
    	String nB = new String(size);
		int nB2 = Integer.parseInt(nB);
		int count=0;
		while(delta<b.length){
			count++;
			size= chunkserverHandlers.get(cs).seekByteSize(delta, filename, ids);
	    	nB = new String(size);
			nB2 = Integer.parseInt(nB);
			delta=delta+(4+nB2);
		}
		System.out.println("The TFSfile contains "+count+" logical files");
    }
	
	public ObjectOutputStream getOutput(){
		return out;
	}

	public ObjectInputStream getInput(){
		return in;
	}
}