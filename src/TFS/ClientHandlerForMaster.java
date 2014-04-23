package TFS;
import java.io.*;
import java.net.*;
import java.util.*;



public class ClientHandlerForMaster extends HandleAClient {


	public ClientHandlerForMaster(Socket socket, TFSMaster server){		
		super(socket,server);
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
				if(obj.cmd.equals("fileExists")){
					boolean b = exists((String)obj.params.get(0));
					//Send response
					obj.params.clear();
					obj.params.add(b);
					outputToClient.writeObject(obj);
				}
			
				else if (obj.cmd.equals("folderExists")){
					boolean b = folderExists((String)obj.params.get(0));
					//Send response
					obj.params.clear();
					obj.params.add(b);
					outputToClient.writeObject(obj);
				}
				else if (obj.cmd.equals("allocateFolder")){
					this.allocateFolder((String)obj.params.get(0));
				}
				else if (obj.cmd.equals("delete")){
					this.delete((String)obj.params.get(0));
				}
				else if (obj.cmd.equals("deleteDirectory")){
					this.deleteDirectory((String)obj.params.get(0));
				}
				else if (obj.cmd.equals("chunkuuids")){
					String s = (String)obj.params.get(0);
					List<Integer> l = server.fileTable.get(s);
					//Send reply
					obj.params.clear();
					obj.params.add(l);
					outputToClient.writeObject(obj);
					outputToClient.reset();
				}
				else if (obj.cmd.equals("allocAppend")){
					String s = (String)obj.params.get(0);
					int i = (int)obj.params.get(1);
					//Send reply
					obj.params.clear();
					obj.params.add(this.alloc_append(s, i));
					outputToClient.writeObject(obj);
				}
				else if (obj.cmd.equals("allocate")){
					List<Integer> l = this.allocate((String)obj.params.get(0), (int)obj.params.get(1));
					//Send reply
					obj.params.clear();
					obj.params.add(l);
					outputToClient.writeObject(obj);
				}
				else if (obj.cmd.equals("folderInDirectory")){
					String pName = (String) obj.params.get(0);
					List<String> l = this.folderInDirectory(pName);
					//Send reply
					obj.params.clear();
					obj.params.add(l);
					outputToClient.writeObject(obj);
				}
				else if (obj.cmd.equals("getChunkservers")){
					//Send chunkservers
					obj.params.clear();
					obj.params.add(this.getServers());
					outputToClient.writeObject(obj);
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}						
		}
	}

	protected Map getServers(){
		return server.chunkserverTable;
	}

	protected List<String> folderInDirectory(String folderName){
		List<String>arr = new ArrayList<String>();
		for(int i = 0; i < server.folderList.size(); i++){
			String name = server.folderList.get(i);
			if(folderName.length() < name.length()){
				if(folderName.equals(name.substring(0, folderName.length()))){
					arr.add(name);
				}
			}
		}
		return arr;
	}

	protected void allocateFolder(String folderName) throws IOException{
		int serverloc = server.chunkRobin;
		server.chunkRobin = (server.chunkRobin +1)%server.numOfChunkservers;
		server.folderList.add(folderName);    	
		server.folderTable.put(folderName, serverloc);
		System.out.println(folderName + " is created");

		FileWriter fw = new FileWriter("dirconfig.csv", true);

		String s = folderName + "," + serverloc + "\r\n";
		fw.append(s);
		fw.flush();
		fw.close();
	}


	protected List allocate(String filename, int numChunks) throws IOException{
		FileWriter fw = new FileWriter("config.csv", true);
		List<Integer> chunkuuids = allocateChunks(numChunks);
		server.fileTable.put (filename, chunkuuids);
		System.out.println(filename + " is created");

		String s = "\r\n" + filename;
		for(int i = 0; i < chunkuuids.size(); i++){
			s += "," + chunkuuids.get(i);
		}
		fw.append(s);
		fw.flush();
		fw.close();
		return chunkuuids;
	}

	protected List allocateChunks(int numChunks) throws IOException{
		List<Integer> chunkuuids = new ArrayList<Integer>();
		FileWriter fw = new FileWriter("chconfig.csv", true);
		String s = "";
		for(int i = 0; i < numChunks; i++){
			int chunkuuid = server.counter.nextValue();
			int chunkloc = server.chunkRobin;
			server.chunkTable.put (chunkuuid, chunkloc);
			chunkuuids.add(chunkuuid);
			server.chunkRobin = (server.chunkRobin +1)%server.numOfChunkservers;
			s += chunkuuid + "," + chunkloc + "\r\n";
			fw.flush();
		}
		fw.append(s);
		fw.flush();
		fw.close();

		return chunkuuids;
	}

	protected List alloc_append(String filename, int numChunks) throws IOException{
		List<Integer> uuids = server.fileTable.get(filename);
		List<Integer> append_uuids = allocateChunks(numChunks);
		uuids.addAll(append_uuids);

		//FIX Filetable
		server.fileTable.remove(filename);
		server.fileTable.put(filename, uuids);

		//Rewrite config.csv
		File f = new File("config.csv");
		f.delete();
		f.createNewFile();

		FileWriter fw = new FileWriter(f);
		for(Map.Entry<String, List<Integer>> e : server.fileTable.entrySet()){
			String s = "\r\n" + e.getKey();
			for(int i = 0; i < e.getValue().size(); i++){
				s += "," + e.getValue().get(i);
			}
			fw.append(s);
			fw.flush();
		}
		fw.close();

		return append_uuids;
	}

	protected int getLocation(int uuid){
		return server.chunkTable.get(uuid);
	}

	protected int getFolderLocation(String foldername){
		return server.folderTable.get(foldername);
	}

	protected List<Integer> getUUIDS(String filename){
		return server.fileTable.get(filename);
	}

	protected boolean exists(String filename){
		return server.fileTable.containsKey(filename);
	}

	protected boolean folderExists(String foldername){
		return server.folderTable.containsKey(foldername);
	}
	protected void deleteDirectory(String folderName) throws IOException{
		List<String>arr = new ArrayList<String>();
		for(int i = 0; i < server.folderList.size(); i++){
			String name = server.folderList.get(i);
			if(folderName.length() <= name.length()){
				if(folderName.equals(name.substring(0, folderName.length()))){
					arr.add(name);
				}
			}
		}
		for (int i = 0; i < arr.size(); i++){
			System.out.println("Deleting: " + arr.get(i));
			server.folderList.remove(arr.get(i));
			server.folderTable.remove(arr.get(i));
		}
		File f = new File("dirconfig.csv");
		f.delete();
		f.createNewFile();

		FileWriter fw = new FileWriter(f);
		for(int i =0; i< server.folderList.size(); i++){
			String s = server.folderList.get(i) + "," + server.folderTable.get(server.folderList.get(i)) + "\r\n";
			fw.append(s);
			fw.flush();
		}
		fw.close();

		List<String>temp = new ArrayList<String>();
		for(Map.Entry<String, List<Integer>> e : server.fileTable.entrySet()){
			if(folderName.equals(e.getKey().substring(0, folderName.length()))){
				temp.add(e.getKey());
			}
		}

		for(int i=0; i< temp.size(); i++){
			System.out.println("Deleting: " + temp.get(i));
			delete(temp.get(i));
		}
	}

	protected void delete(String filename) throws IOException{
		List<Integer> uuids = server.fileTable.get(filename);
		server.fileTable.remove(filename);

		//remove chconfig log
		File f0 = new File("chconfig.csv");
		f0.delete();
		f0.createNewFile();
		FileWriter fw0 = new FileWriter("chconfig.csv", true);
		String s0 = "";
		for(int i=0 ; i<uuids.size(); i++){
			int chunkLoc = server.chunkTable.get(uuids.get(i));
			
			//TODO FIX THIS TO MULTIPLE CHUNKSERVER
			//TFSChunkserver cs = server.chunkserverTable.get(chunkLoc);
			//cs.removeChunk(uuids.get(i));

			Socket cssocket = new Socket("localhost", 7501);
			ObjectOutputStream output = new ObjectOutputStream(cssocket.getOutputStream());
			MyObject o = new MyObject();
			o.cmd = "removeChunk";
			o.params.add(uuids.get(i));
			output.writeObject(o);

			server.chunkTable.remove(uuids.get(i));
		}

		for(Map.Entry<String, List<Integer>> e : server.fileTable.entrySet()){
			s0 +=  e.getKey() + "," + e.getValue() + "\r\n";
			fw0.append(s0);
			fw0.flush();
		}
		fw0.close();

		//remove config log
		File f = new File("config.csv");
		f.delete();
		f.createNewFile();

		FileWriter fw = new FileWriter(f);
		for(Map.Entry<String, List<Integer>> e : server.fileTable.entrySet()){
			String s = "\r\n" + e.getKey();
			for(int i = 0; i < e.getValue().size(); i++){
				s += "," + e.getValue().get(i);
			}
			fw.append(s);
			fw.flush();
		}
		fw.close();
		}

	public ObjectOutputStream getOutput(){
		return outputToClient;
	}

	public ObjectInputStream getInput(){
		return inputFromClient;
	}
}