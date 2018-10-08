package nachos.threads;

import nachos.machine.*;

import java.util.PriorityQueue;

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
    
    waitLock = new Lock();
    waitingQueue = new PriorityQueue<>();
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	
    	Machine.interrupt().disable();
    	while(!waitingQueue.isEmpty() && waitingQueue.peek().getWaitTime() <= Machine.timer().getTime()) {
    		Condition2 thread = waitingQueue.poll().getThread();
    		
    		if(thread != null) {
    			waitLock.acquire();
    			thread.wake();
    			waitLock.release();
    		}
    	}
	KThread.currentThread().yield();
	Machine.interrupt().enable();
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
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
    		long wakeTime = Machine.timer().getTime() + x;
    		Condition2 thread = new Condition2(waitLock);
    		waitingQueue.add(new ThreadNode(wakeTime, thread));
    		
    		waitLock.acquire();
    		thread.sleep();
    		waitLock.release();
    	
	
    }
    /**
     * The lock needed for the Condition2 variable that sleeps and wakes up the thread.
     */
    private Lock waitLock;
    
    private PriorityQueue<ThreadNode> waitingQueue;
    
    
    /**
     * This private class is a node that encapsulates the information about a thread.
     * In holds the Condition2 variable for the certain thread and also it has the 
     * wait time that the thread has to wait. This node class uses Comparable so that
     * the priority queue can determine a threads wait priority based on a threads 
     * wait time.
     * 
     * @author Bryan Ayala
     *
     */
    private class ThreadNode implements Comparable<ThreadNode>{
    	
    	private long waitTime;
    	private Condition2 thread;
    	
    	public ThreadNode(long waitTime, Condition2 thread){
    		this.waitTime = waitTime;
    		this.thread = thread;
    	}
    	@Override
    	public int compareTo(ThreadNode thread) {
    		if(this.waitTime > thread.waitTime)
    			return 1;
    		else if(this.waitTime < thread.waitTime)
    			return -1;
    		else 
    			return 0;
    	}
    	public long getWaitTime() {
    		return waitTime;
    	}
    	public Condition2 getThread() {
    		return thread;
    	}

    
   
    
}


	
	
}
