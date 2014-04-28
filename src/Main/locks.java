package Main;
import java.util.concurrent.Semaphore;


public class locks {
	public Semaphore read;
	public Semaphore write;
	
	public locks(){
		read = new Semaphore(1);
		write = new Semaphore(1);
	}
	public Semaphore getRead() {
		return read;
	}
	public Semaphore getWrite() {
		return write;
	}
}
