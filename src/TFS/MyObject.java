package TFS;

import java.io.*;
import java.util.*;

public class MyObject implements Serializable{
	String from = "";
	String s = "";
	List<Object> params = new ArrayList<Object>();
	
	public MyObject(){
		
	}
	
	public String getString(){
		return s;
	}
	public void setString(String s){
		this.s = s;
	}
}