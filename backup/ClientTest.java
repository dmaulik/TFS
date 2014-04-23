package TFS;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

public class ClientTest extends JFrame{
	MyObject fs;
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;

	//we need for sending
	//FactoryUpdater factoryUpdater = new FactoryUpdater();
	//FactoryManagerAnimation factoryAnimation;


	public static void main(String[] args){
		new ClientTest();
	} 


	public ClientTest(){

		//jframe for factoryproductionmanager		
		fs = new MyObject(); // object received


		try{
			socket = new Socket("localhost", 7500);

			System.out.println("new socket");
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			Integer n = 1;
			out.writeObject(n);
			System.out.println("Client stream");
			
			//ArrayList<Kit> kits = (ArrayList<Kit>)in.readObject(); //gets the existing list of kits
			//factoryAnimation.setList(kits);
			//factoryAnimation.setComboBoxes();
			Read read = new Read();
			new Thread(read).start();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}		
		while(true){ // this loop writes when necessary
			try{

			}
			catch(Exception ex){
				ex.printStackTrace();
			}

		}		
	}

	public class Read implements Runnable{
		public void run(){

			while(true){ // this looop reads all the time
				try{
					/*
					fs = (FactoryState)in.readObject();
					fs.printState();
					ArrayList<Kit> kits = new ArrayList<Kit>();
					kits = fs.getKits();
					factoryAnimation.setList(kits);
					factoryAnimation.setComboBoxes();
					*/

				}
				catch(Exception ex){

				}
			}			
		}
	}

}