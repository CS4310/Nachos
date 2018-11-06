//package nachos.threads;
//
//import java.util.ArrayList;
//
//import nachos.machine.*;
//
///**
// * A <i>communicator</i> allows threads to synchronously exchange 32-bit
// * messages. Multiple threads can be waiting to <i>speak</i>,
// * and multiple threads can be waiting to <i>listen</i>. But there should never
// * be a time when both a speaker and a listener are waiting, because the two
// * threads can be paired off at this point.
// */
//public class Communicator {
//    /**
//     * Allocate a new communicator.
//     */
//    public Communicator() {
//    	/****************
//    	 * initialize variables here
//    	 ****************************/
//    	lock            = new Lock();
//    	speaker         = new Condition2(lock);
//    	listener        = new Condition2(lock);
//    	speakerListener = new Condition2(lock);
//    	hasMessage      = false;
//    	message         = Integer.MIN_VALUE;
//    	conditions      = new ArrayList<>();
//    }
//
//    /**
//     * Wait for a thread to listen through this communicator, and then transfer
//     * <i>word</i> to the listener.
//     *
//     * <p>
//     * Does not return until this thread is paired up with a listening thread.
//     * Exactly one listener should receive <i>word</i>.
//     *
//     * @param	word	the integer to transfer.
//     */
//    
//    /*************************************
//     * requirement is to satisfy 1 speak to 1 listener, 
//     * and we need speakerListener to keep track of that
//     * 0 for empty
//     * 1 for speaker
//     * 2 for listener
//     * 3 for complete
//     *************************************/
//    
//    public void speak(int word) {
//    	System.out.println("here");
//    	lock.acquire();
//    	int size = conditions.size();
//    	
//    	if(conditions.size()!= 0 && conditions.get(0)!=2) {
//    		conditions.add(1);
//    		speaker.sleep();
//    	}
//    	
//    	if(conditions.size() == 0 || conditions.get(0) == 2) {
//    		//has at least a listener
//    		if(!hasMessage) {
//    			System.out.println("s1");
//    			message = word;
//    			hasMessage = true;
//    			if(size == 0)
//    				conditions.add(1); 
//    			else
//    				conditions.set(0, 1);
//    			listener.wake();
//    		}
//    	}
//    	
//    	else {
//    		
//    		System.out.println("s3");
//    		conditions.add(1);
//    		message = word;
//    		hasMessage = true;
//    		listener.wake();
//    		speaker2.sleep();
//    	}
//    		
//    	
//    	lock.release();
//    	
//    }
//    public int listen() {
//    	System.out.println("array(0) " + conditions.get(0));
//    	lock.acquire();
//    	int size = conditions.size();
//    	
//    	if(conditions.size() == 0 || conditions.get(0) == 2) {
//    		conditions.add(2);
//    		listener.sleep();
//    	}
//    	if(conditions.get(0) == 1) {
//    		//has a speaker
//    		if(hasMessage) {
//    			temp = message;
//    			hasMessage = false;
//    			conditions.remove(0);
//    		}
//    		
//    		else {
//    			System.out.println("here2");
//    			speaker.wake();
//    			listener.sleep();
//    			temp = message;
//    			hasMessage = false;
//    			conditions.remove(0);
//    		}
//    	}
//    	else {
//    		System.out.println("here3");
//    		conditions.add(2);
//    		hasMessage = false;
//    		speaker.wake();
//    		temp = message;
//    	}
//    	lock.release();
//    	return temp;
//    }
//    
//    
//    
//    /***********
//     * variable we use
//     *******************/
//    private int message;
//    Lock lock;
//    Condition2 speaker;
//    Condition2 speaker2;
//    Condition2 listener;
//    Condition2 speakerListener;
//    boolean hasMessage;
//    int temp;
//    ArrayList<Integer> conditions;
//    
//}

package nachos.threads;

import nachos.machine.*;
import java.util.LinkedList;

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
    	messageQ  = new LinkedList<Integer>(); 
    	q  = new LinkedList<Condition2>(); 
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
    
    public void speak(int word) {
    	lock.acquire();
    	System.out.println("Speaking ("+word+"): " + messageQ.toString());
    	//if there is a line of speakers
    	if(!messageQ.isEmpty() && !q.isEmpty()) {
    		q.add(speaker);		//add self to q
    		messageQ.add(word);	//add message to q
    		speaker.sleep(); 	//and sleep
    	}
    	//no line of speakers, but possibly waiting listeners
    	else {					//you may speak
    		//if waiting listeners, squad up
    		if(!q.isEmpty() && messageQ.isEmpty()) {
    			messageQ.add(word);
    			Condition2 waiting_listener = q.remove();
    			waiting_listener.wake();
    		}
    		//else, nothin in q's -- sleep and wait for listener
    		else {
        		q.add(speaker);		//add self to q
        		messageQ.add(word);	//add message to q
        		speaker.sleep(); 	//and sleep    			
    		}
    	}
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
    	System.out.println("Listening sees: " + messageQ.toString());
    	int word = -1;
    	//if there is a line of listeners
    	if(!q.isEmpty() && messageQ.isEmpty()) {
    		q.add(listener);	//add self to q
    		listener.sleep(); 	//and sleep

    		word = messageQ.remove();
    	}
    	//no line of listeners, but possibly waiting speakers
    	else {
    		//if waiting speakers, squad up
    		if(!q.isEmpty() && !messageQ.isEmpty()) {
    			q.remove().wake();			//wake up speaker

    			word = messageQ.remove();
    		}
    		//else if only a message, grab it
    		else if(q.isEmpty() && !messageQ.isEmpty()) {
    			word = messageQ.remove();
    		}
    		//else sleep and wait for speaker
    		else {
        		q.add(listener);	//add self to q
        		listener.sleep(); 	//and sleep    			

        		word = messageQ.remove();
    		}
    	}
    	lock.release();
    	return word;
    }
    
    /***********
     * variable we use
     *******************/
    Lock lock;
    Condition2 speaker;
    Condition2 listener;
    LinkedList<Condition2> q;
    LinkedList<Integer> messageQ;   
}

