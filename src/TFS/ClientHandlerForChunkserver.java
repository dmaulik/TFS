package TFS;
import java.io.*;
import java.net.*;
import java.util.*;



public class ClientHandlerForChunkserver extends HandleAClient {


	public ClientHandlerForChunkserver(Socket socket, TFSChunkserver chunkserver) throws UnknownHostException, IOException{		
		super(socket,chunkserver);
		//System.out.println(socket.getLocalPort());
		//System.out.println(socket.getOutputStream().toString());
		//System.out.println("ClientHandlerForChunkserver spawned");
	}


	public void run(){
		while(true){
	
			try{
				MyObject obj = new MyObject();
				try{
					obj = (MyObject) inputFromClient.readObject();
				}
				catch (Exception e){
					
				}

				//System.out.println("Request from client: "+obj.s);
				if(obj.s.equals("write")){
					//System.out.println(new String((byte[])obj.params.get(1)));
					this.write((int)obj.params.get(0), (byte[]) obj.params.get(1));
				}
				else if (obj.s.equals("read")){
					//System.out.println("Got read request");
					int chunkID = (int) obj.params.get(0);
					byte[] by = this.read(chunkID);
					obj.params.clear();
					obj.params.add(by);
					outputToClient.writeObject(obj);
					//System.out.println("Sent response");
					outputToClient.flush();
				}
				else if (obj.s.equals("removeChunk")){
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
		
		//FileWriter fw = new FileWriter(file.getAbsoluteFile());
		FileOutputStream fos = new FileOutputStream(file);
		//bw.write(chunk);
		fos.write(chunk);
		chunkserver.chunkTable.put(chunkuuid, local_filename.getBytes());
		fos.flush();
		fos.close();
	}

	public byte[] read (int chunkID) throws IOException
	{
		byte[] data = null;
		String localFilename = getFileName(chunkID);
		//String currentLine;
		//BufferedReader br = new BufferedReader(new FileReader(localFilename));
		//while ((currentLine = br.readLine()) != null ){
		//	data = currentLine.getBytes();
		//}
		//br.close();
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
			