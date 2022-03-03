import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

public class BirthdayParty {
    static int numGuests;
    static boolean cupcake = true;
    static AtomicInteger visitorsCount = new AtomicInteger(0);
    static Boolean isPartyOver = false;
    
    public static void main(String [] args) {

        Scanner input = new Scanner(System.in);

        do {
            System.out.println("How many guests are invited to the birthday party?");
            numGuests = input.nextInt();
            if (numGuests < 2) 
                System.out.println("Not enough guests for the party.");
        } while (numGuests < 2);

        input.close();
        
        ReentrantLock lock = new ReentrantLock();

        // All guests have agreed to have a dedicated counter.
        new Thread(new GuestThread("Counter", lock)).start();
        // The rest of guests.
        for (int i = 0; i < numGuests - 1; i++)
            new Thread(new GuestThread("", lock)).start();

        // Wait till every guest visits labyrinth. 
        while (visitorsCount.get() < numGuests - 1) {}
        
        // Stop all threads.
        isPartyOver = true;
    }

    public static void visitLabyrinth(GuestThread guest) {
        // If guest is the Counter.
        if (guest.getName().compareTo("Counter") == 0) {
             // No cupcake, ask Minotaur to bring another one.
            if (!cupcake) {
                cupcake =true;
                if (visitorsCount.addAndGet(1) == (numGuests - 2)) {
                    System.out.println("Mr. Minotaur, all the " + numGuests + 
                                       " guests have visited the labyrinth!");
                    // Add yourself to the count.
                    visitorsCount.addAndGet(1);
                }
            }
            return;
        }

        // If visited before just leave.
        if (guest.isVisited()) return;
        
        // Eat the cupcake and leave.
        if (cupcake) {
            cupcake = false;
            guest.markVisited();
        }  
    }

}


class GuestThread implements Runnable {
    private boolean visited;
    private String name;
    ReentrantLock lock;

    GuestThread(String name, ReentrantLock lock) {
        this.name = name;
        this.lock = lock;
    }

    public void run() {
       
        while (!BirthdayParty.isPartyOver) {
            lock.lock();

            try {
                BirthdayParty.visitLabyrinth(this);
            } finally {
                lock.unlock();
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public Boolean isVisited() {
        return this.visited;
    }

    public void markVisited() {
        this.visited = true;
    }
 
}