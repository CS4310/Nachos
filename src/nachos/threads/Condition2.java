package nachos.threads;

<<<<<<< HEAD
=======
import java.util.LinkedList;

>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
<<<<<<< HEAD
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
=======
	
	/*****************************************
	 * initialize conditionLock and queue here
	 *****************************************/
	
    public Condition2(Lock conditionLock) {
    	this.conditionLock = conditionLock;
    	q = new LinkedList<>();
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
<<<<<<< HEAD
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	conditionLock.release();

	conditionLock.acquire();
=======
    
    /****************************
     * Try to make thread sleep using FIFO
     *****************************/
    public void sleep() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		
		
		boolean intStatus = Machine.interrupt().disable(); //disable other thread interrupt
		KThread current = KThread.currentThread();         //current has current thread
		q.add(current);                                   //add to queue and wait to be wake
		conditionLock.release();  //this line was given
		KThread.sleep();                                 //put thread to sleep
	
		conditionLock.acquire(); //this line was given
		Machine.interrupt().restore(intStatus);          //enable interrupt
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
<<<<<<< HEAD
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
=======
    
    /******************************
     * Try to wake an asleep thread with FIFO
     ******************************/
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
		//if wait queue is not empty then we start removing from wait queue and put it in ready state
		if(!q.isEmpty()) {
			boolean intStatus = Machine.interrupt().disable();
			q.removeFirst().ready();
			Machine.interrupt().restore(intStatus);
		}
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
<<<<<<< HEAD
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    }

    private Lock conditionLock;
=======
    
    /************************
     * wait all asleep threads
     *************************/
    public void wakeAll() {
    	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
    	//same structure as condition.java
		while(!q.isEmpty())
			wake();
    }

    /***********************************
     * initialize variables that we need 
     ***********************************/
    
    private Lock conditionLock;
    private LinkedList<KThread> q;
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
}
