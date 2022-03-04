import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class CrystalVase {
    static int numGuests;
    // static AtomicBoolean isShowOver = new AtomicBoolean(false);
    static AtomicInteger visitorsCount = new AtomicInteger(0);

    public static void main(String [] args) {

        Scanner input = new Scanner(System.in);

        do {
            System.out.println("How many guests are at the event?");
            numGuests = input.nextInt();
            if (numGuests < 2) 
                System.out.println("Not enough guests for the party.");
        } while (numGuests < 2);

        input.close();

        // This lock works as a line to enter the showroom.
        CLHLock lock = new CLHLock();

        GuestThread [] guests = new GuestThread[numGuests];

        // The guests.
        for (int i = 0; i < numGuests; i++) {
            guests[i] = new GuestThread(lock);
            new Thread(guests[i]).start();
        }
            

        // Wait till every guest visits labyrinth. 
        while (visitorsCount.get() < numGuests) {}
        
        // Stop all threads.
        //isShowOver.set(true);

        for (int i = 0; i < numGuests; i++) {
            guests[i].leave();
        }

        System.out.println("The show is over. All " + visitorsCount.get() + 
                           " guests got a chance to see the Crystal Vase.");
    }

    public static void enterTheShowroom(GuestThread guest) {
       
        // If first time visiting leave a trace.
        if (!guest.isVisited()) {
            visitorsCount.addAndGet(1);
            guest.markVisited();
        }
    }
}


// This is adopted CLHLock code from the book "The Art of Multiprocessor Programming"
// by Maurice Herlihy & Nir Shavit, from p. 153. With its implementation, it plays 
// the role of the line into the showroom to look at the Crystal Vase.
class CLHLock implements Lock{
    AtomicReference<Guest> tail;
    ThreadLocal<Guest> myPred;
    ThreadLocal<Guest> myself;

    CLHLock() {
        tail = new AtomicReference<Guest>(new Guest());
        myself = new ThreadLocal<Guest>() {
            protected Guest initialValue() {
                return new Guest();
            }
        };
        myPred = new ThreadLocal<Guest>() {
            protected Guest initialValue() {
                return null;
            }
        };
    }

    // Get in line.
    @Override
    public void lock() {
        Guest guest = myself.get();
        guest.isWaiting = true;
        Guest pred = tail.getAndSet(guest);
        myPred.set(pred);

        while (pred.isWaiting) {}  
    }

    // Tell next person in line that room is available.
    @Override
    public void unlock() {
        Guest guest = myself.get();
        guest.isWaiting = false;
        myself.set(myPred.get());
    }
    
    @Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}
}

class Guest {
    boolean isWaiting = false;
}

class GuestThread implements Runnable {
    private boolean visited = false;
    private CLHLock lock;
    private volatile boolean stop = false;
    
    GuestThread(CLHLock lock) {
        this.lock = lock;
    }

    public void run() {
       
        while (!this.stop) {
            this.lock.lock();

            try {
                CrystalVase.enterTheShowroom(this);
            } finally {
                this.lock.unlock();
            }
            
            //System.out.println("here");
        }

        System.out.println("Stopping " + Thread.currentThread().getId());
    }

    public Boolean isVisited() {
        return this.visited;
    }

    public void markVisited() {
        this.visited = true;
    }

    public void leave() {
        this.stop = true;
    }
}

