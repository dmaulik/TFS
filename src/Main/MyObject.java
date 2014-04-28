package Main;


import java.io.*;
import java.util.*;

/**
 *
 */
public class MyObject implements Serializable{
	public String cmd = "";	//Command in String
	public List<Object> params = new ArrayList<Object>();	//Necessary parameters
	
	public MyObject(){
		
	}
}