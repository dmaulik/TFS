package TFS;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;

public class Test6 {
	
	public static void appendImg(TFSClient client,String lpath, String filename) throws IOException{
		File f = new File(lpath);
		if(!f.exists()){
			System.out.println("Error: Source file does not exist");
			System.exit(0);
		}
		
		String []s = filename.split("\\\\");
		String folderName = "";
		if(s.length == 1){
			//create directly. No folder
		}
		else{
			folderName += s[0];
			for(int i=1; i<s.length-1; i++){
				folderName += ("\\" + s[i]);
			}
			if(!client.folderExists(folderName)){
				System.out.println("Error: Folder doesn't exist");
				System.exit(0);
			}
		}
		byte[] b=client.fileToByte(f);
		int size = b.length;
		System.out.println(size);
		byte[] byteSize=new byte[4];
		
		//ByteBuffer byteSize = ByteBuffer.wrap(temp.getBytes());
		
		
		//byteSize= temp.getBytes();
		byteSize=client.intToByteArray(size);
		int temp=client.byteArrayToInt(byteSize);
		byte[] combined = new byte[b.length + 4];
		//String s2 = new String(""+temp);
	    System.out.println(temp);
		for (int i = 0; i < combined.length; ++i)
		{
		    combined[i] = i < byteSize.length ? byteSize[i] : b[i - byteSize.length];
		}
		if(!client.fileExists(filename))
			client.write(filename, combined);
		else
			client.write_append(filename, combined);
		
	}

    public static void main (String[] args) throws Exception{
	  	TFSMaster master = new TFSMaster();
	  	TFSClient client = new TFSClient(master);
	  	
	  	appendImg(client,"src\\img.png","1\\File5.png");
	  	byte[] tt=client.read("1\\File5.png");
	  	byte[] check={tt[0],tt[1],tt[2],tt[3]};
	  	
	  	String s = new String(tt);
	    System.out.println("Content : " + tt);
	
	}
}
