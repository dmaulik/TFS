package TFS;
import java.io.*;
import java.net.*;
import java.util.*;



public class HandlerForClient extends HandleAClient {
	static int chunkSize = 64;
	
	public HandlerForClient(Socket socket, TFSClient client, ObjectOutputStream out, ObjectInputStream in) throws UnknownHostException, IOException{		
		super(socket,client);
		//System.out.println(socket.getLocalPort());
		//System.out.println(socket.getInputStream().toString());
		//System.out.println("ClientHandlferForChunkserver spawned");
		outputToClient = out;
		inputFromClient = in;
	}



	public void run(){
		while(true){
			
		}
	}
	
	//DONE
		public void createFile(String fileName) throws IOException, ClassNotFoundException{
			if(fileExists(fileName)){
				System.out.println("Error: Files already exists");
				return;
			}
			int numOfChunks = 1;
			
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "allocate";
			o.params.add(fileName);
			o.params.add(numOfChunks);
			outputToClient.writeObject(o);
			outputToClient.flush();
			o = (MyObject) inputFromClient.readObject();
			
			List<Integer> chunkuuids = new ArrayList<Integer>();
			List<Object> l = (List<Object>) o.params.get(0);
			for(int i=0; i<l.size(); i++){
				chunkuuids.add((Integer)l.get(i));
			}
			String b = "";
			write_chunks(chunkuuids, b.getBytes());
		}
		
		//DONE
		public void createDirectory(String folderName) throws IOException, ClassNotFoundException{
			if(folderExists(folderName)){
				System.out.println("Error: Directory already exists");
				return;
			}
			
			
			System.out.println("Sending request to create directory");
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "allocateFolder";
			o.params.add(folderName);
			outputToClient.writeObject(o);
			outputToClient.flush();
		}
		
		//DONE
		public void deleteDirectory(String folderName) throws IOException, ClassNotFoundException{
			if(!folderExists(folderName)){
				System.out.println("Directory Delete: File does not exist");
				return;
			}
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "deleteDirectory";
			o.params.add(folderName);
			outputToClient.writeObject(o);
			outputToClient.flush();
		}

		//DONE
		public List<Integer> write(String filename, byte[] data) throws IOException, ClassNotFoundException{
			//System.out.println(new String(data));
			int numOfChunks = num_chunks(data.length);
			if(numOfChunks == 0)
				numOfChunks = 1;
			
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "allocate";
			o.params.add(filename);
			o.params.add(numOfChunks);
			outputToClient.writeObject(o);
			outputToClient.flush();
			o = (MyObject) inputFromClient.readObject();
			
			List<Integer> chunkuuids = new ArrayList<Integer>();
			List<Object> l = (List<Object>) o.params.get(0);
			for(int i=0; i<l.size(); i++){
				chunkuuids.add((Integer)l.get(i));
			}

			return chunkuuids;
			//	write_chunks(chunkuuids,data);
		}
		
		
		//TODO Need to do this
		public void write_chunks(List<Integer> chunkuuids, byte[] data) throws IOException, ClassNotFoundException{
			List<byte[]> chunks = new ArrayList<byte[]>();
			int remainingLetters = data.length;
			if(remainingLetters == 0){
				chunks.add("".getBytes());
			}
			
			for(int i=0; i<data.length; i+= chunkSize){
				if(remainingLetters<= chunkSize){
					chunks.add(Arrays.copyOfRange(data, i, i + remainingLetters));
					break;
				}
				chunks.add(Arrays.copyOfRange(data, i, i + chunkSize));
				remainingLetters -= chunkSize;
			}
				
			/*
			//TODO
			Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
			
			//TODO
			for(int i =0; i<chunkuuids.size(); i++){
				int chunkuuid = chunkuuids.get(i);
				int chunkloc = master.getLocation(chunkuuid);
				//System.out.println(chunks.get(i));
				chunkserverTable.get((Integer)(chunkloc)).write(chunkuuid, chunks.get(i));
			}*/
			
			//Socket chunkserversocket = new Socket("localhost", 7501);
			//ObjectOutputStream out2 = new ObjectOutputStream(chunkserversocket.getOutputStream());
			
			for(int i=0; i<chunkuuids.size(); i++){
				int chunkuuid = chunkuuids.get(i);
			
				MyObject o = new MyObject();
				o.from = "client";
				o.s = "write";
				o.params.add(chunkuuid);
				o.params.add(chunks.get(i));
				outputToClient.writeObject(o);
				outputToClient.flush();
			}
			
			
		}
		
		//DONE
		public int num_chunks(int size){
			if(size == 0)
				return 0;
			else if (size <= chunkSize)
				return 1;
			else{
				return (int)(Math.ceil((float)size/chunkSize));
			}

		}
		
		//DONE
		public List<Integer> write_append(String filename,byte[] data) throws IOException, ClassNotFoundException{
			if(!fileExists(filename)){
				System.out.println("Exception!");
				return null;
			}
			int num_append_chunks = num_chunks(data.length);
			
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "allocAppend";
			o.params.add(filename);
			o.params.add(num_append_chunks);
			outputToClient.writeObject(o);
			outputToClient.flush();
			
			o = (MyObject) inputFromClient.readObject();
			
			List<Integer> append_chunkuuids = new ArrayList<Integer>();
			List<Object> l = (List<Object>) o.params.get(0);
			for(int i=0; i<l.size(); i++){
				append_chunkuuids.add((Integer)l.get(i));
			}
			
			return append_chunkuuids;
			//write_chunks(append_chunkuuids, data);	
		}
		
		//DONE
		public boolean fileExists(String filename) throws IOException, ClassNotFoundException{
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "fileExists";
			o.params.add(filename);
			outputToClient.writeObject(o);
			outputToClient.flush();
			
			o = (MyObject) inputFromClient.readObject();
			return (boolean) o.params.get(0);
		}
		
		//DONE
		public boolean folderExists(String filename) throws IOException, ClassNotFoundException{
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "folderExists";
			o.params.add(filename);
			outputToClient.writeObject(o);
			outputToClient.flush();
			
			o = (MyObject) inputFromClient.readObject();
			return (boolean) o.params.get(0);
		}
		
		public List<Integer> getUUIDs(String filename) throws IOException, ClassNotFoundException{
			if(!fileExists(filename)){
				System.out.println("Exception!");
				System.out.println("Terminating..");
				System.exit(0);;
			}
			
			MyObject obj = new MyObject();
			obj.from = "client";
			obj.s = "chunkuuids";
			obj.params.add(filename);
			outputToClient.writeObject(obj);
			outputToClient.flush();
			
			obj = (MyObject) inputFromClient.readObject();
			List<Object> l = (List<Object>) obj.params.get(0);
			//System.out.println(l);
			
			List<Integer> chunkuuids = new ArrayList<Integer>();

			for(int i=0; i<l.size(); i++){
				chunkuuids.add((Integer)l.get(i));
			}
			return chunkuuids;
		}
		
		public byte[] read(String filename, List<Integer> chunkuuids) throws IOException, ClassNotFoundException{
			
			List<byte[]> chunks = new ArrayList<byte[]>();
					
			byte[] data;
			/*
			//TODO 
			Map<Integer, TFSChunkserver> chunkserverTable = master.getServers();
			
			
			for (int i = 0; i <chunkuuids.size(); i++){
				int chunkloc = master.getLocation(chunkuuids.get(i));
				byte[] ch = chunkserverTable.get((Integer)(chunkloc)).read(chunkuuids.get(i));
				chunks.add(ch);
			}
			*/
			
			//Socket chunkserversocket = new Socket("localhost", 7501);
			//ObjectOutputStream out2 = new ObjectOutputStream(chunkserversocket.getOutputStream());
			
			
			for(int i=0; i<chunkuuids.size(); i++){
				int chunkloc = 1;
				
				MyObject o2 = new MyObject();
				o2.from = "client";
				o2.s = "read";
				o2.params.add(chunkuuids.get(i));
				outputToClient.writeObject(o2);
				//System.out.println("Sent read request");
			
				o2 = (MyObject) inputFromClient.readObject();
				
				//System.out.println("Got response");
				byte[] ch = (byte[]) o2.params.get(0);
				//System.out.println(new String(ch));
				chunks.add(ch);
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			for(int i=0; i<chunks.size(); i++){
				outputStream.write(chunks.get(i));
			}
			data = outputStream.toByteArray( );
			
			return data;
		}
		
		//DONE
		public void delete(String filename) throws IOException, ClassNotFoundException{
			if(!fileExists(filename)){
				System.out.println("Delete Error: File does not exist");
				return;
			}
			
			MyObject o = new MyObject();
			o.from = "client";
			o.s = "delete";
			o.params.add(filename);
			outputToClient.writeObject(o);
			outputToClient.flush();
		}
			
		
		//DONE
		public byte[] fileToByte (File file) throws IOException{

		    byte []buffer = new byte[(int) file.length()];
		    InputStream ios = null;
		    try {
		        ios = new FileInputStream(file);
		        if ( ios.read(buffer) == -1 ) {
		            throw new IOException("EOF reached while trying to read the whole file");
		        }        
		    } finally { 
		        try {
		             if ( ios != null ) 
		                  ios.close();
		        } catch ( IOException e) {
		        }
		    }

		    return buffer;
		}
		
		//DONE
		public byte[] seekByteSize(int offset, String filename, List<Integer> chunkuuids) throws IOException, ClassNotFoundException{
			byte[] result=new byte[4];
			//byte[] b = read(filename,getUUIDs(filename));
			List<byte[]> chunks = new ArrayList<byte[]>();
			byte[] b;
			
			for(int i=0; i<chunkuuids.size(); i++){
				int chunkloc = 1;
				
				MyObject o2 = new MyObject();
				o2.from = "client";
				o2.s = "read";
				o2.params.add(chunkuuids.get(i));
				outputToClient.writeObject(o2);
				outputToClient.flush();
				//System.out.println("Sent read request");
			
				o2 = (MyObject) inputFromClient.readObject();
				
				//System.out.println("Got response");
				byte[] ch = (byte[]) o2.params.get(0);
				//System.out.println(new String(ch));
				chunks.add(ch);
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			for(int i=0; i<chunks.size(); i++){
				outputStream.write(chunks.get(i));
			}
			b = outputStream.toByteArray( );
			
			for(int i=0;i<4;i++){
				result[i]=b[offset+i];
			}
			return result;
		}
		
		//DONE
		public String byteToString(byte[] _bytes)
		{
		    String file_string = "";
		    for(int i = 0; i < _bytes.length; i++)
		    {
		        file_string += (char)_bytes[i];
		    }
		    
		    return file_string;    
		}
		
		public List<String> getFolderInDirectory(String pathname) throws ClassNotFoundException, IOException{
	    	MyObject o = new MyObject();
			o.from = "client";
			o.s = "folderInDirectory";
			o.params.add(pathname);
			outputToClient.writeObject(o);
			outputToClient.flush();
			
			o = (MyObject) inputFromClient.readObject();
			
			List<String> s = new ArrayList<String>();
			List<Object> l = (List<Object>) o.params.get(0);
			for(int i=0; i<l.size(); i++){
				s.add((String)l.get(i));
			}
			return s;
	    }
		
}