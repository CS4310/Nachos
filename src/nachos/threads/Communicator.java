package nachos.threads;

import java.util.LinkedList;
import java.util.Queue;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	/****************
    	 * initialize variables here
    	 ****************************/
    	lock      = new Lock();
    	message   = null;
    	speaker   = new Condition2(lock);
    	listener  = new Condition2(lock);
    	speakerListener = new Condition2(lock);
    	speakerQ  = new LinkedList<>();
    	listenerQ = new LinkedList<>();
    	
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    
    /*************************************
     * requirement is to satisfy 1 speak to 1 listener, 
     * and we need speakerListener to keep track of that
     *************************************/
    
    public void speak(int word) {
    	lock.acquire();
    	
    	while(message != null) {
    		//speakerQ.add(KThread.currentThread());
    		speaker.sleep();
    	}
    	
    	message= new Integer(word);
    	speakerListener.sleep();
    	listener.wake();
    	
    	//listenerQ.poll();
    	
    	lock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    
    /**************************
     * return the message from speaker
     ***********************/
    
    public int listen() {
    	lock.acquire();
    	
    	int temp;
    	while(message == null) {
    		//listenerQ.add(KThread.currentThread());
    		listener.sleep();
    	}
    	
    	speakerListener.wake();
    	speaker.wake();
    	
    	//speakerQ.poll();
    		
    	temp = message.intValue();
    	message = null;
    	lock.release();
    	
    	return temp;
    }
    
    /***********
     * variable we use
     *******************/
    Integer message;
    Lock lock;
    Condition2 speaker;
    Condition2 listener;
    Condition2 speakerListener;
    Queue <KThread> speakerQ;
    Queue <KThread> listenerQ;

    
}
