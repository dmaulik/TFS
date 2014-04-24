package TFS;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 *
 */
public class HandlerForClient extends HandleAClient {
	static int chunkSize = 64;

    /**
     *
     * @param socket
     * @param client
     * @param out
     * @param in
     * @throws UnknownHostException
     * @throws IOException
     */
	public HandlerForClient(Socket socket, TFSClient client, ObjectOutputStream out, ObjectInputStream in) throws UnknownHostException, IOException{		
		super(socket,client);
		outputToClient = out;
		inputFromClient = in;
	}

    /**
     *
     */
	public void run(){
		while(true){}
	}

    /**
     *
     * @param fileName
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public void createFile(String fileName) throws IOException, ClassNotFoundException{
		//Check if File exists
		if(fileExists(fileName)){
			System.out.println("Error: Files already exists");
			return;
		}
		int numOfChunks = 1;

		//Ask server to Allocate chunks
		MyObject o = new MyObject();
		o.cmd = "allocate";
		o.params.add(fileName);
		o.params.add(numOfChunks);
		outputToClient.writeObject(o);
		outputToClient.flush();
		
		//Get the chunk Ids from the server
		o = (MyObject) inputFromClient.readObject();

		List<Integer> chunkuuids = (List<Integer>) o.params.get(0);
		String b = "";
		write_chunks(chunkuuids, b.getBytes());
	}

    /**
     *
     * @param folderName
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public void createDirectory(String folderName) throws IOException, ClassNotFoundException{
		if(folderExists(folderName)){
			System.out.println("Error: Directory already exists");
			return;
		}

		//Requesting master to create the directory.
		MyObject o = new MyObject();
		o.cmd = "allocateFolder";
		o.params.add(folderName);
		outputToClient.writeObject(o);
		outputToClient.flush();
	}

    /**
     *
     * @param folderName
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public void deleteDirectory(String folderName) throws IOException, ClassNotFoundException{
		//Check if folder exists
		if(!folderExists(folderName)){
			System.out.println("Directory Delete: File does not exist");
			return;
		}
		//Requesting Master to delete Directory
		MyObject o = new MyObject();
		o.cmd = "deleteDirectory";
		o.params.add(folderName);
		outputToClient.writeObject(o);
		outputToClient.flush();
	}

    /**
     *
     * @param uuid
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public int getChunkserverToTalk(int uuid) throws IOException, ClassNotFoundException{
		MyObject o = new MyObject();
		o.cmd = "getChunkserverToTalk";
		o.params.add(uuid);
		outputToClient.writeObject(o);
		o = (MyObject) inputFromClient.readObject();
		int i = (int) o.params.get(0);
		return i;
	}

    /**
     *
     * @param filename
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public List<Integer> write(String filename, byte[] data) throws IOException, ClassNotFoundException{
		//System.out.println(new String(data));
		int numOfChunks = num_chunks(data.length);
		if(numOfChunks == 0)
			numOfChunks = 1;

		//Requesting master to allocate chunks
		MyObject o = new MyObject();
		o.cmd = "allocate";
		o.params.add(filename);
		o.params.add(numOfChunks);
		outputToClient.writeObject(o);
		outputToClient.flush();
		
		//Getting chunkuuids from master
		o = (MyObject) inputFromClient.readObject();

		List<Integer> chunkuuids = (List<Integer>) o.params.get(0);
		return chunkuuids;
		//write_chunks(chunkuuids,data);
	}

    /**
     * Talks to chunkserver
     * @param chunkuuids
     * @param data
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
			for(int i =0; i<chunkuuids.size(); i++){
				int chunkuuid = chunkuuids.get(i);
				int chunkloc = master.getLocation(chunkuuid);
				//System.out.println(chunks.get(i));
				chunkserverTable.get((Integer)(chunkloc)).write(chunkuuid, chunks.get(i));
			}
		*/

		for(int i=0; i<chunkuuids.size(); i++){
			int chunkuuid = chunkuuids.get(i);

			//Writing to the Chunkserver
			MyObject o = new MyObject();
			o.cmd = "write";
			o.params.add(chunkuuid);
			o.params.add(chunks.get(i));
			outputToClient.writeObject(o);
			outputToClient.flush();
		}
	}

    /**
     * Get the number of chunks
     * @param size
     * @return
     */
	public int num_chunks(int size){
		if(size == 0)
			return 0;
		else if (size <= chunkSize)
			return 1;
		else{
			return (int)(Math.ceil((float)size/chunkSize));
		}
	}

    /**
     *
     * @param filename
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public List<Integer> write_append(String filename,byte[] data) throws IOException, ClassNotFoundException{
		if(!fileExists(filename)){
			System.out.println("Exception!");
			return null;
		}
		int num_append_chunks = num_chunks(data.length);

		//Requesting allocAppend to master
		MyObject o = new MyObject();
		o.cmd = "allocAppend";
		o.params.add(filename);
		o.params.add(num_append_chunks);
		outputToClient.writeObject(o);
		outputToClient.flush();

		//Getting the new chunk ids
		o = (MyObject) inputFromClient.readObject();

		List<Integer> append_chunkuuids = (List<Integer>) o.params.get(0);
		return append_chunkuuids;
		//write_chunks(append_chunkuuids, data);	
	}

    /**
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public boolean fileExists(String filename) throws IOException, ClassNotFoundException{
		//Ask master if file exists
		MyObject o = new MyObject();
		o.cmd = "fileExists";
		o.params.add(filename);
		outputToClient.writeObject(o);
		outputToClient.flush();

		//Get reply from master
		o = (MyObject) inputFromClient.readObject();
		return (boolean) o.params.get(0);
	}

    /**
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public boolean folderExists(String filename) throws IOException, ClassNotFoundException{
		//Ask master if folder exists
		MyObject o = new MyObject();
		o.cmd = "folderExists";
		o.params.add(filename);
		outputToClient.writeObject(o);
		outputToClient.flush();

		//Get reply from master
		o = (MyObject) inputFromClient.readObject();
		return (boolean) o.params.get(0);
	}

    /**
     * Get ChunkIDS from master for specific filename
     * @param filename
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public List<Integer> getUUIDs(String filename) throws IOException, ClassNotFoundException{
		if(!fileExists(filename)){
			System.out.println("Exception!");
			System.out.println("Terminating..");
			System.exit(0);;
		}

		//Request chunk IDs to master
		MyObject obj = new MyObject();
		obj.cmd = "chunkuuids";
		obj.params.add(filename);
		outputToClient.writeObject(obj);
		outputToClient.flush();

		//Get the chunk IDs
		obj = (MyObject) inputFromClient.readObject();
		List<Integer> chunkuuids = (List<Integer>) obj.params.get(0);
		return chunkuuids;
	}

    /**
     * Talks to chunserver
     * @param filename
     * @param chunkuuids
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

		for(int i=0; i<chunkuuids.size(); i++){
			int chunkloc = 1;

			//Requesting read to Chunkserver
			MyObject o2 = new MyObject();
			o2.cmd = "read";
			o2.params.add(chunkuuids.get(i));
			outputToClient.writeObject(o2);


			//Getting the reply from chunkserver
			o2 = (MyObject) inputFromClient.readObject();
			byte[] ch = (byte[]) o2.params.get(0);
			chunks.add(ch);
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		for(int i=0; i<chunks.size(); i++){
			outputStream.write(chunks.get(i));
		}
		data = outputStream.toByteArray( );

		return data;
	}

    /**
     *
     * @param filename
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public void delete(String filename) throws IOException, ClassNotFoundException{
		if(!fileExists(filename)){
			System.out.println("Delete Error: File does not exist");
			return;
		}

		//Requesting delete to Master
		MyObject o = new MyObject();
		o.cmd = "delete";
		o.params.add(filename);
		outputToClient.writeObject(o);
		outputToClient.flush();
	}


    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
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

    /**
     * Talks to chunkservers
     * @param offset
     * @param filename
     * @param chunkuuids
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public byte[] seekByteSize(int offset, String filename, List<Integer> chunkuuids) throws IOException, ClassNotFoundException{
		byte[] result=new byte[4];
		List<byte[]> chunks = new ArrayList<byte[]>();
		byte[] b;

		for(int i=0; i<chunkuuids.size(); i++){
			int chunkloc = 1;

			//Requesting read to chunkserver
			MyObject o2 = new MyObject();
			o2.cmd = "read";
			o2.params.add(chunkuuids.get(i));
			outputToClient.writeObject(o2);
			outputToClient.flush();
			
			//Get the reply from master
			o2 = (MyObject) inputFromClient.readObject();
			byte[] ch = (byte[]) o2.params.get(0);
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

    /**
     *
     * @param _bytes
     * @return
     */
	public String byteToString(byte[] _bytes)
	{
		String file_string = "";
		for(int i = 0; i < _bytes.length; i++)
		{
			file_string += (char)_bytes[i];
		}
		return file_string;    
	}

    /**
     * Asks the master for all the folders in a directory
     * @param pathname
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
	public List<String> getFolderInDirectory(String pathname) throws ClassNotFoundException, IOException{
		//Ask the master
		MyObject o = new MyObject();
		o.cmd = "folderInDirectory";
		o.params.add(pathname);
		outputToClient.writeObject(o);
		outputToClient.flush();

		//Get reply
		o = (MyObject) inputFromClient.readObject();

		List<String> s = new ArrayList<String>();
		List<Object> l = (List<Object>) o.params.get(0);
		for(int i=0; i<l.size(); i++){
			s.add((String)l.get(i));
		}
		return s;
	}

}