import java.util.concurrent.Semaphore;


public class locks {
	Semaphore read;
	Semaphore write;
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
