package TFS;
import java.io.*;
import java.net.*;
import java.util.*;



public class ClientHandlerForChunkserver extends HandleAClient {


	public ClientHandlerForChunkserver(Socket socket, TFSChunkserver chunkserver) throws UnknownHostException, IOException{		
		super(socket,chunkserver);
	}


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
				
			}
			catch(Exception ex){
				ex.printStackTrace();
			}						
		}

	}
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

	
	public void createFile (int chunkuuid){
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
	}
	
	public void write (int chunkuuid, byte[] chunk) throws IOException
	{
		String local_filename = getFileName(chunkuuid);
		File file = new File(local_filename);
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(chunk);
		chunkserver.chunkTable.put(chunkuuid, local_filename.getBytes());
		fos.flush();
		fos.close();
	}

	public byte[] read (int chunkID) throws IOException
	{
		byte[] data = null;
		String localFilename = getFileName(chunkID);
		data = fileToByte(new File(localFilename));
		return data;
	}
	
	public String getFileName (int chunkID)
	{	
		return chunkserver.root + "\\" + Integer.toString(chunkID) + ".tfs";
	}
	
	public void removeChunk(int chunkID){
		File f = new File(chunkserver.root + "\\" + Integer.toString(chunkID) + ".tfs");
		f.delete();
	}

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
	
	 public ObjectOutputStream getOutput(){
			return outputToClient;
		}

		public ObjectInputStream getInput(){
			return inputFromClient;
		}
}
			