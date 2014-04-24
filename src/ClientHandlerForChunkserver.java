
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;


/**
 *
 */
public class ClientHandlerForChunkserver extends HandleAClient {
	List<request> requests;

    /**
     *
     * @param socket                The socket it is connecting to
     * @param chunkserver           The chunkserver
     * @throws UnknownHostException Something something
     * @throws IOException          Something something
     */
	public ClientHandlerForChunkserver(Socket socket, TFSChunkserver chunkserver) throws UnknownHostException, IOException{		
		super(socket,chunkserver);
		requests = new ArrayList<request>();
	}

    /**
     *
     */
	class request{
		int id;
		byte[] array;

        /**
         *
         * @param a Something
         * @param b Something
         */
		public request(int a, byte[] b){
			id = a;
			array = b;
		}

        /**
         *
         * @return  An int id
         */
		public int getId() {
			return id;
		}

        /**
         *
         * @return  A byte array
         */
		public byte[] getArray() {
			return array;
		}
	}

    /**
     *
     */
	public void run(){
		while(true){
			try{
				MyObject obj = new MyObject();
				try{
					//Keep Reading for input from client
					obj = (MyObject) inputFromClient.readObject();
				}
				catch (Exception e){
					
				}

				//System.out.println("Request from client: "+obj.s);
				if(obj.cmd.equals("write")){
					//Writing the chunks to chunkserver
					this.write((int)obj.params.get(0), (byte[]) obj.params.get(1));
				}
				else if (obj.cmd.equals("read")){
					//Reading the chunks
					int chunkID = (int) obj.params.get(0);
					byte[] by = this.read(chunkID);
					
					//Reply to client
					obj.params.clear();
					obj.params.add(by);
					outputToClient.writeObject(obj);
					outputToClient.flush();
				}
				else if (obj.cmd.equals("removeChunk")){
					//Remove chunks from chunkserver
					this.removeChunk((int)obj.params.get(0));
					obj.params.clear();
				}
				/*else if (obj.cmd.equals("versionInquery")){
					int version = chunkserver.versionNumber;
					//Reply to client
					obj.params.clear();
					obj.params.add(version);
					outputToClient.writeObject(obj);
					outputToClient.flush();
				}*/
				
			}
			catch(Exception ex){
				ex.printStackTrace();
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
     *
     * @param chunkuuid The uuid of the chunk
     */
	public void createFile (int chunkuuid){
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
		chunkserver.versionNumber++;
	}

    /**
     *
     * @param chunkuuid     The uuid of the chunk
     * @param chunk         The chunk
     * @throws IOException  Something
     */
	public void write (int chunkuuid, byte[] chunk) throws IOException
	{
		requests.add(new request(chunkuuid,chunk));
		while(requests.size()!=0){
			int id = requests.get(0).getId();
			byte[] b = requests.get(0).getArray();
			String local_filename = getFileName(id);
			File file = new File(local_filename);
		
			FileOutputStream fos = new FileOutputStream(file);
		
			fos.write(b);
			chunkserver.chunkTable.put(id, local_filename.getBytes());
			fos.flush();
			fos.close();
			chunkserver.versionNumber++;
			requests.remove(0);
		}
	}

    /**
     *
     * @param chunkID       Id of the chunk
     * @return              A byte array
     * @throws IOException  Something
     */
	public byte[] read (int chunkID) throws IOException
	{
		byte[] data = null;
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
     *
     * @param chunkID   Id of the chunk
     */
	public void removeChunk(int chunkID){
		File f = new File(chunkserver.root + "\\" + Integer.toString(chunkID) + ".tfs");
		f.delete();
	}

    /**
     *
     * @param file          A file
     * @return              A byte array from the file
     * @throws IOException  Something
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
			