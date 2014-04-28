package Main.ChunkServer;

import Main.*;
import Main.Client.*;
import Main.Master.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;


/**
 * 
 */
public class ClientHandlerForChunkserver extends HandleAClient {
	List<request> requests;
	List<request> readRequests;
    /**
     *
     * @param socket                The socket it is connecting to
     * @param chunkserver           The Chunkserver it is on
     * @throws UnknownHostException 
     * @throws IOException         
     */
	public ClientHandlerForChunkserver(Socket socket, TFSChunkserver chunkserver) throws UnknownHostException, IOException{		
		super(socket,chunkserver);
		requests = new ArrayList<request>();
		readRequests = new ArrayList<request>();
	}

    /**
     * To process write/read. Works like a queue
     */
	class request{
		int id;	//Request ID
		byte[] array;	//content
		
		//Constructor
		public request(int a, byte[] b){
			id = a;
			array = b;
		}
		//return the request ID
		public int getId() {
			return id;
		}
		//return the content
		public byte[] getArray() {
			return array;
		}
	}

    /**
     *	This runs the thread
     */
	public void run(){
		while(true){
			try{
				//To store incoming msg
				MyObject obj = new MyObject();
				try{
					//Keep Reading for input from client
					obj = (MyObject) inputFromClient.readObject();
				}catch (Exception e){}

				//Write chunks to chunkserver
				if(obj.cmd.equals("write")){
					this.write((int)obj.params.get(0), (byte[]) obj.params.get(1));
				}
				//Read chunks and return it
				else if (obj.cmd.equals("read")){
					//get the chunk ID
					int chunkID = (int) obj.params.get(0);
					//Create new semaphore corresponding to the chunk ID
					if(chunkserver.lockTable.get(chunkID)==null){
						locks temp = new locks();
						chunkserver.lockTable.put(chunkID, temp);
					}
					//Acquire the lock
					chunkserver.lockTable.get(chunkID).write.acquire();
					//Do the read
					byte[] by = this.read(chunkID);
					//Reply to client
					obj.params.clear();
					obj.params.add(by);
					outputToClient.writeObject(obj);
					outputToClient.flush();
					chunkserver.lockTable.get(chunkID).write.release();
					
				}
				//Remove chunks from chunkserver
				else if (obj.cmd.equals("removeChunk")){
					this.removeChunk((int)obj.params.get(0));
					obj.params.clear();
				}
			}
			catch(Exception ex){
				//ex.printStackTrace();
			}						
		}

	}

    /**
     *
     * @param folderName    The name of the folder to be created
     */
	public void createFolder(String folderName){
		File dir = new File(folderName);

		// if the directory does not exist, create it
		if (!dir.exists()) {
		    System.out.println("Creating directory: " + folderName);
		    boolean result = dir.mkdir();  

		    if(result) 
		       System.out.println("Folder created");  
		}else{
			System.out.println("Directory already exists");
		}
    }

    /**
     * Create a local file
     * @param chunkuuid The uuid of the chunk
     */
	public void createFile (int chunkuuid){
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
		chunkserver.versionNumber++;
	}

    /**
     * Write data to a specific chunk
     * @param chunkuuid     The uuid of the chunk
     * @param chunk         The chunk
     * @throws IOException  Something
     * @throws InterruptedException 
     */
	public void write (int chunkuuid, byte[] chunk) throws IOException, InterruptedException
	{
		//Add to request lists
		requests.add(new request(chunkuuid,chunk));
		//Finish all requests to write
		while(requests.size()!=0){
			int id = requests.get(0).id;
			byte[] b = requests.get(0).getArray();
			if(chunkserver.lockTable.get(id)==null){
				chunkserver.lockTable.put(id, new locks());
				System.out.println("Lock "+ id+" created");
			}
			chunkserver.lockTable.get(id).write.acquire();
			System.out.println("Lock "+id+ " acquired");
			String local_filename = getFileName(id);
			System.out.println(local_filename);
		
			//Write data to the chunk
			File file = new File(local_filename);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(b);
		
			//update chunkTable
			chunkserver.chunkTable.put(id, local_filename.getBytes());
			fos.flush();
			fos.close();
			chunkserver.versionNumber++;
		
			//Release semaphore
			chunkserver.lockTable.get(id).write.release();
			System.out.println("Lock "+id+ " released");
			requests.remove(0);
		}
	}

	
    /**
     * Read a chunk and return it
     * @param chunkID       Id of the chunk
     * @return              A byte array
     * @throws IOException  Something
     */
	public byte[] read (int chunkID) throws IOException
	{
		byte[] data = null;	//To store the data
		//Read the data
		String localFilename = getFileName(chunkID);
		data = fileToByte(new File(localFilename));
		return data;
	}

    /**
     *
     * @param chunkID   Id of the chunk
     * @return          The file name
     */
	public String getFileName (int chunkID)
	{	
		return chunkserver.root + "\\" + Integer.toString(chunkID) + ".tfs";
	}

    /**
     * Remove chunk from local chunkserver
     * @param chunkID   Id of the chunk
     */
	public void removeChunk(int chunkID){
		File f = new File(chunkserver.root + "\\" + Integer.toString(chunkID) + ".tfs");
		f.delete();
	}

    /**
     * Get the content of the file in byte[] and then return it
     * @param file          A file
     * @return              A byte array from the file
     * @throws IOException  Something
     */
	public byte[] fileToByte (File file) throws IOException{
		if(file.length() == 0){
			return new byte[0];
		}
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
			