package TFS;

import java.io.*;
import java.util.*;

public class MyObject implements Serializable{
	String s = "";
	
	public MyObject(){
		
	}
	
	public String getString(){
		return s;
	}
	public void setString(String s){
		this.s = s;
	}
}