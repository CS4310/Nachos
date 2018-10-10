package nachos.threads;

<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.LinkedList;

>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
<<<<<<< HEAD
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
=======
    	Machine.timer().setInterruptHandler(new Runnable() {
    		public void run() { timerInterrupt(); }
	    });
    	
    	/********************
    	 * init variables here
    	 *********************/
    	
    	lock = new Lock();
    	c2 = new Condition2(lock);
    	waitQ = new ArrayList<>();
    	wakeTimeQ = new ArrayList<>();
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
<<<<<<< HEAD
    public void timerInterrupt() {
	KThread.currentThread().yield();
=======
    
    /********************************
     * check all threads and their wake time every 500 clock cycles
     *********************************/
    public void timerInterrupt() {
    	//KThread.currentThread().yield();
    	
    	boolean intStatus = Machine.interrupt().disable();
    	
    	/*
    	 * check all elements in wake time queue
    	 * if the wake time >= current time, wake the element
    	 */
    	
    	for(int i=0; i < wakeTimeQ.size(); i++) {
    		if(Machine.timer().getTime() >= wakeTimeQ.get(i)) {
    			waitQ.get(i).ready();
    			wakeTimeQ.remove(i);
    			waitQ.remove(i);
    		}
    	}
    	
    	Machine.interrupt().restore(intStatus);
    	
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
<<<<<<< HEAD
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	long wakeTime = Machine.timer().getTime() + x;
	while (wakeTime > Machine.timer().getTime())
	    KThread.yield();
    }
=======
    
    /***********************
     * put threads to sleep 
     ***********************/
    public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
 
    	
		long wakeTime = Machine.timer().getTime() + x;
		//while (wakeTime > Machine.timer().getTime()) {
			//KThread.yield();
		//}
			
		/*
		 * put the element to sleep if it is not at wake time
		 * use wake time queue to keep track of each thread wake time
		 * use wait queue to keep track of all threads
		 */
		
		boolean intStatus = Machine.interrupt().disable();
		lock.acquire();
		if(wakeTime > Machine.timer().getTime()) {
			wakeTimeQ.add(wakeTime);
			waitQ.add(KThread.currentThread());
			c2.sleep();
		}
		lock.release();
		Machine.interrupt().restore(intStatus);
    }
    
    /******************
     * variable we used
     ******************/
    Lock lock;
    Condition2 c2;
    private ArrayList<KThread> waitQ;
    private ArrayList<Long> wakeTimeQ;
    
>>>>>>> 4ee0e38ebb4f393d6e741483adc4e9943284aa61
}
