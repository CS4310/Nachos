package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

import java.util.Queue;
import java.util.LinkedList;

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
	
	/*****************************************
	 * initialize conditionLock and queue here
	 *****************************************/
	
    public Condition2(Lock conditionLock) {
    	this.conditionLock = conditionLock;
    	q = new LinkedList<>();
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    
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

    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    
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
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    
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
    public LinkedList<KThread> q;

}
