package nachos.threads;

import java.util.PriorityQueue;

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
    	Machine.timer().setInterruptHandler(new Runnable() {
    		public void run() { timerInterrupt(); }
	    });
    	
    	/********************
    	 * init variables here
    	 *********************/
    	
    	lock = new Lock();
    	c2 = new Condition2(lock);
    	waitingQueue = new PriorityQueue<>();
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    
    /********************************
     * check all threads and their wake time every 500 clock cycles
     *********************************/
    public void timerInterrupt() {
    	//KThread.currentThread().yield();
    	
    	
    	/*
    	 * While the queue isn't empty. It checks the first Condition2 in the queue
    	 * and checks if it's paired wait time is less than the current machine time,
    	 * it is removed from the queue and woken up.
    	 */
    	
    	while(!waitingQueue.isEmpty() && waitingQueue.peek().wakeTime <= Machine.timer().getTime()){
    		
    		Condition2 c2 = waitingQueue.poll().c2;//Removes the top Condition2 of the queue
    		
    		if(c2 != null) {//Checks if the condition variable is null, just incase
    			lock.acquire();
    			c2.wake();//wakes the condition variable with its paired wait time
    			lock.release();
    		}
    		
    		
    	}
    	 	
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
		 * Create a new condition variable to represent the thread. Then
		 * encapsulate it with the calculated wait time into a ThreadNode
		 * that will be stored in a priority queue. Once encapsulated, the 
		 * condition variable is put to sleep
		 */
		
		Condition2 c2 = new Condition2(lock);//Create new condition variable for specific wake
		waitingQueue.add(new ThreadNode(wakeTime,c2));//encapsulating into a node.
		
		lock.acquire();
		c2.sleep();
		lock.release();
		
    }
    
    /******************
     * variable we used
     ******************/
    private Lock lock;
    Condition2 c2;
    /*
     * This is the priority queue that holds a thread node which pairs the 
     * condition variable with its wake time.
     */
    private PriorityQueue<ThreadNode> waitingQueue;
    /*
     * This is the private class that represents a node that holds the condition
     * variable and its wake time.
     */
    private class ThreadNode implements Comparable<ThreadNode>{
    	private long wakeTime; //the condition variables wake time
    	private Condition2 c2;// the condition variable
    	private ThreadNode(long wakeTime, Condition2 c2){
    		this.wakeTime = wakeTime;
    		this.c2 = c2;
    	}
    	/*
    	 * This is used to compare the nodes wait times in the priority queue.
    	 * The node with the smallest wake time is given priority in the queue
    	 * (non-Javadoc)
    	 * @see java.lang.Comparable#compareTo(java.lang.Object)
    	 */
		@Override 
		public int compareTo(ThreadNode c2) {
			if (this.wakeTime < c2.wakeTime)
				return -1;
			else if (this.wakeTime > c2.wakeTime)
				return 1;
			else
				return 0;
		}
    	
    }
    
}
