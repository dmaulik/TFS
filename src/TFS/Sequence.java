package TFS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class Sequence {

	  private static final AtomicInteger counter = new AtomicInteger();

    /**
     *
     * @return
     */
	  public static int nextValue() {
	    return counter.getAndIncrement();
	  }
}