package nachos.threads;

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
    	lock            = new Lock();
    	speaker         = new Condition2(lock);
    	listener        = new Condition2(lock);
    	speakerListener = new Condition2(lock);
    	hasMessage      = false;
    	message         = Integer.MIN_VALUE;
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
    	while(hasMessage) {
    		speaker.sleep(); //sleep when there is message (other speaker is using the buffer)
    	}
    	
    	message = word;     //your turn to speak
    	hasMessage = true;  //a message is available for listener
    	speakerListener.sleep();
    	listener.wake();  
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
    	while(!hasMessage) {
    		listener.sleep(); //sleep when no message in buffer
    	}
    	
    	temp = message;  //message is available
    	hasMessage = false;
    	speakerListener.wake();
    	speaker.wake();
    	lock.release();
    	return temp;
    }
    
    /***********
     * variable we use
     *******************/
    private int message;
    Lock lock;
    Condition2 speaker;
    Condition2 listener;
    Condition2 speakerListener;
    boolean hasMessage;
    
}
