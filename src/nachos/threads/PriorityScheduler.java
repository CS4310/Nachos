package nachos.threads;

import nachos.machine.*;
import nachos.threads.PriorityScheduler.PriorityQueue;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A scheduler that chooses threads based on their priorities.
 *
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 *
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 *
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
    /**
     * Allocate a new priority scheduler.
     */
    public PriorityScheduler() {
    }
    
    /**
     * Allocate a new priority thread queue.
     *
     * @param	transferPriority	<tt>true</tt> if this queue should
     *					transfer priority from waiting threads
     *					to the owning thread.
     * @return	a new priority thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
    	return new PriorityQueue(transferPriority);
    }

    public int getPriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());
			       
		return getThreadState(thread).getPriority();
    }

    public int getEffectivePriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());
			       
		return getThreadState(thread).getEffectivePriority();
    }

    public void setPriority(KThread thread, int priority) {
		Lib.assertTrue(Machine.interrupt().disabled());
			       
		Lib.assertTrue(priority >= priorityMinimum &&
			   priority <= priorityMaximum);
		
		getThreadState(thread).setPriority(priority);
    }

    public boolean increasePriority() {
		boolean intStatus = Machine.interrupt().disable();
			       
		KThread thread = KThread.currentThread();
	
		int priority = getPriority(thread);
		if (priority == priorityMaximum)
		    return false;
	
		setPriority(thread, priority+1);
	
		Machine.interrupt().restore(intStatus);
		return true;
    }

    public boolean decreasePriority() {
		boolean intStatus = Machine.interrupt().disable();
			       
		KThread thread = KThread.currentThread();
	
		int priority = getPriority(thread);
		if (priority == priorityMinimum)
		    return false;
	
		setPriority(thread, priority-1);
	
		Machine.interrupt().restore(intStatus);
		return true;
    }

    /**
     * The default priority for a new thread. Do not change this value.
     */
    public static final int priorityDefault = 1;
    /**
     * The minimum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMinimum = 0;
    /**
     * The maximum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMaximum = 7;    

    boolean debug = false;
    /**
     * Return the scheduling state of the specified thread.
     *
     * @param	thread	the thread whose scheduling state to return.
     * @return	the scheduling state of the specified thread.
     */
    protected ThreadState getThreadState(KThread thread) {
		if (thread.schedulingState == null)
		    thread.schedulingState = new ThreadState(thread);
	
		return (ThreadState) thread.schedulingState;
    }

    /**
	     * A <tt>ThreadQueue</tt> that sorts threads by priority.
	     */
    protected class PriorityQueue extends ThreadQueue {
		PriorityQueue(boolean transferPriority) {
			display("initialize transferPriority");
		    this.transferPriority = transferPriority;
		}
	
		public void waitForAccess(KThread thread) {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    display("waitForAccess "+thread);
		    //add this thread as threadstate in waitQueue
		    ThreadState currentThread = getThreadState(thread);
		    this.waitQueue.add(currentThread);
		    getThreadState(thread).waitForAccess(this);
		    
		}
	
		public void acquire(KThread thread) {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    display("acquire "+thread);
		    //if thread holder exist and transfer priority, 
		    //remove thread from the holder's resource list
		    if(holder != null) {
		    	holder.release(this);	
		    }
		    holder = getThreadState(thread); 
		    getThreadState(thread).acquire(this);
		}
	
		public KThread nextThread() {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    display("nextThread");
		    // implement me
		    //return null;
		    //if I have a holder and I transfer priority
		    //remove myself from the holder's resource list
		    if(holder != null && transferPriority) {
		    	holder.myResources.remove(this);
		    }
		    
		    if(!waitQueue.isEmpty()) {
		    	ThreadState firstThread = pickNextThread();
		    	if(firstThread != null) {
		    		waitQueue.remove(firstThread);
			    	firstThread.acquire(this);	
		    		//acquire(firstThread.thread);
//			    	if(firstThread.waitingOn.isEmpty()) {
//			    		System.out.println("I am not waiting on resource " + firstThread.thread+" priority: "+firstThread.getPriority() + " EP: " +firstThread.getEffectivePriority());
//			    	}

		    	}
		    	return firstThread.thread;
		    }
		    
		    //if waitQueue is empty, return null
		    return null;
		    
		}
		
		public int getEffectivePriority() {
			display("getEffectivePriority");
			if(!transferPriority)
				return priorityMinimum;
			if(dirty) {
				effective = priorityMinimum;
				for(int i=0; i<waitQueue.size(); i++) {
					effective = Math.max(effective, waitQueue.get(i).getPriority());
				}
			}
			return effective;
		}
		
		public void setDirty() {
			display("setDirty");
			if(!transferPriority) return;
			dirty = true;
			if(holder != null)
				holder.setDirty();
		}
		
		/**
		 * Return the next thread that <tt>nextThread()</tt> would return,
		 * without modifying the state of this queue.
		 *
		 * @return	the next thread that <tt>nextThread()</tt> would
		 *		return.
		 */
		protected ThreadState pickNextThread() {
		    // implement me
		    //return null;
			
			ThreadState pickThread = waitQueue.pollLast();

			// determine thread existence
			if (pickThread != null) {
				// return the thread picked
				return (pickThread);
				// if none found, return null
			} else {
				return null;
			}
		}
		
		public void print() {
		    Lib.assertTrue(Machine.interrupt().disabled());
		    // implement me (if you want)
		    for(int i=0; i<waitQueue.size(); i++)
		    	System.out.print(waitQueue.get(i).thread);
		    System.out.println();
		}
		
		public void display(String s) {
			if(debug)
				System.out.println(s);
		}
		
		public void display(int num) {
			System.out.println(num);
		}
	
		/**
		 * <tt>true</tt> if this queue should transfer priority from waiting
		 * threads to the owning thread.
		 */
		public boolean transferPriority;
		
		ThreadState holder = null;
		
		LinkedList <ThreadState> waitQueue = new LinkedList<>();
		
		boolean dirty = false;
		
		int effective = priorityMinimum;

		public void updateEP() {
			if(holder != null) {
				//System.out.println("Priority Queue: holder.toString()");
				holder.updateEP();
				//System.out.println(holder.toString());
			}
		}
		/*
		@Override
		public String toString() {
			return "PriorityQueue [transferPriority=" + transferPriority + ", holder=" + holder + ", waitQueue="
					+ waitQueue + ", dirty=" + dirty + ", effective=" + effective + "]";
		}
		*/
    }

    /**
     * The scheduling state of a thread. This should include the thread's
     * priority, its effective priority, any objects it owns, and the queue
     * it's waiting for, if any.
     *
     * @see	nachos.threads.KThread#schedulingState
     */
    protected class ThreadState {
	/**
	 * Allocate a new <tt>ThreadState</tt> object and associate it with the
	 * specified thread.
	 *
	 * @param	thread	the thread this state belongs to.
	 */
	public ThreadState(KThread thread) {
		display("TS Initilize thread: "+thread);
	    this.thread = thread;
	    
	    setPriority(priorityDefault);
	}

	/**
	 * Return the priority of the associated thread.
	 *
	 * @return	the priority of the associated thread.
	 */
	public int getPriority() {
		display("TS2 getPriority");
	    return priority;
	}

	/**
	 * Return the effective priority of the associated thread.
	 *
	 * @return	the effective priority of the associated thread.
	 */
	public int getEffectivePriority() {
		//Lib.assertTrue(Machine.interrupt().disabled());
		display("TS3 getEffectivePriority");
	    // implement me
	    //return priority;
		updateEP();
		if (!myResources.isEmpty() && this.dirty) {
			this.effective = this.getPriority();
			// iterate through threads in resourceQueue
			Iterator<PriorityQueue> nextThread = myResources.iterator();
			while (nextThread.hasNext()) {
				effective = Math.max(effective, nextThread.next().getEffectivePriority());
			}
			dirty = false;
		}
		// return final effective priority
		return effective;
	}

	/**
	 * Set the priority of the associated thread to the specified value.
	 *
	 * @param	priority	the new priority.
	 */
	public void setPriority(int priority) {
		display("TS4 setPriority");
	    if (this.priority == priority)
		return;
	    
	    this.priority = priority;
	    
	    // implement me
	    updateEP();
	    setDirty();
	}
	
	public int calcMaxPriority() {

		// current priority
		int calcMaxEP = this.priority;

		// iterate through the Priority queue
		Iterator<PriorityQueue> thread = myResources.iterator();
		while (thread.hasNext()) {

			// determine maximum priority by comparing w/ effective priority
			calcMaxEP = Math.max(calcMaxEP, (thread.next()).getEffectivePriority());

		} // return max priority from PriorityQueue
		return calcMaxEP;
	}
	
	
	public void updateEP() {

  	  	System.out.println(toString());
		effective = calcMaxPriority();
		PriorityQueue ret = null;
	      for(int i=0; i<myResources.size(); i++) {
	        if(ret == null || myResources.get(i).getEffectivePriority() > ret.getEffectivePriority())
	          ret = myResources.get(i);
	      }
	      myResources.remove(ret);
	      
	      //System.out.println(getEffectivePriority());
	      
	      for(int i = 0; i < waitingOn.size(); i++) {
	    	  //System.out.println("ThreadState" + toString());
	    	  waitingOn.get(i).updateEP();
	      }
	}
	
	public void setDirty() {
		display("TS5 setDirty");
		if(dirty)
			return;
		dirty = true;
		for(int i=0; i<waitingOn.size();i++) {
			waitingOn.get(i).setDirty();
		}
	}
	
	public void release(PriorityQueue waitQueue) {
		display("TS6 release");
		myResources.remove(waitQueue);
		setDirty();
	}
	/**
	 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
	 * the associated thread) is invoked on the specified priority queue.
	 * The associated thread is therefore waiting for access to the
	 * resource guarded by <tt>waitQueue</tt>. This method is only called
	 * if the associated thread cannot immediately obtain access.
	 *
	 * @param	waitQueue	the queue that the associated thread is
	 *				now waiting on.
	 *
	 * @see	nachos.threads.ThreadQueue#waitForAccess
	 */
	public void waitForAccess(PriorityQueue waitQueue) {
		display("TS7 waitForAccess");
	    // implement me
		waitingOn.add(waitQueue);
		if(myResources.indexOf(waitQueue) != -1) {
			//it is in resource that is currently holding
			myResources.remove(waitQueue);
			waitQueue.holder = null;
		}
		if(waitQueue.holder != null)
			waitQueue.setDirty();
	}
	

	/**
	 * Called when the associated thread has acquired access to whatever is
	 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
	 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
	 * <tt>thread</tt> is the associated thread), or as a result of
	 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
	 *
	 * @see	nachos.threads.ThreadQueue#acquire
	 * @see	nachos.threads.ThreadQueue#nextThread
	 */
	public void acquire(PriorityQueue waitQueue) {
		display("TS8 acquire");
	    // implement me
		myResources.add(waitQueue);
		if(waitingOn.indexOf(waitQueue) != -1) {
			waitingOn.remove(waitQueue);
		}
		waitQueue.holder = this;
		setDirty();
	}	
	
	public void display(String s) {
		if(debug)
			System.out.println(s);
	}
	
	public void display(int num) {
		System.out.println(num);
	}

	/** The thread with which this object is associated. */	   
	protected KThread thread;
	/** The priority of the associated thread. */
	protected int priority;
	
	LinkedList <PriorityQueue> myResources = new LinkedList<>();
	
	LinkedList <PriorityQueue> waitingOn = new LinkedList<>();
	
	int effective = priorityMinimum;
	
	boolean dirty = false;

	@Override
	public String toString() {
		return "ThreadState [thread=" + thread + ", priority=" + priority + ", myResources=" + myResources
				+ ", waitingOn=" + waitingOn + ", effective=" + effective + ", dirty=" + dirty + "]";
	}
	
    }
}



