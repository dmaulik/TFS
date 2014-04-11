package TFS;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;

public class Test6 {
	
	public static void append(TFSClient client,String lpath, String filename) throws IOException{
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
		byte[] b = client.fileToByte(f);
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
		int nB2 = Integer.parseInt(nB);
		System.out.println("Integer: "+size);
		System.out.println("String: "+nB);
		System.out.println("BytesToInt: " + nB2);
		
		
		//byte[] byteSize=new byte[4];
		//ByteBuffer byteSize = ByteBuffer.wrap(temp.getBytes());
		
		//byteSize= temp.getBytes();
		
		//byteSize=client.intToByteArray(size);
		//int temp=client.byteArrayToInt(byteSize);
		byte[] combined = new byte[b.length + 4];
		
		//String s2 = new String(""+temp);
	    //System.out.println(temp);
		for (int i = 0; i < combined.length; ++i)
		{
		    combined[i] = i < newByte.length ? newByte[i] : b[i - newByte.length];
		}
		String z = new String(combined);
		System.out.println(z);
		
		if(!client.fileExists(filename))
			client.write(filename, combined);
		else
			client.write_append(filename, combined);
		
	}

    public static void main (String[] args) throws Exception{
	  	TFSMaster master = new TFSMaster();
	  	TFSClient client = new TFSClient(master);
	  	
	  	//append(client, "src\\test123.txt", "1\\File5");
	  	append(client,"src\\img.png","1\\File5");
	  	String s = new String(client.read("1\\File5"));
	    System.out.println("Content : " + s);
	
	}
}
