package TFS;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

public class Client1{
	MyObject toSend;
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;

	public static void main(String[] args){
		new Client1();
	} 


	public Client1(){
	
		toSend = new MyObject(); // object 

		try{
			socket = new Socket("localhost", 7500);

			System.out.println("I am a new client");
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			//Compose msg
			toSend.from = "client";
			toSend.c = this;
			
			toSend.s = "append"; // Depends later.
			out.writeObject(toSend);

		}
		catch(Exception ex){
			ex.printStackTrace();
		}		
		//while(true){ // this loop writes when necessary
			try{
				//Ask for instruction then send it to master
			}
			catch(Exception ex){
				ex.printStackTrace();
			}

		//}		
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