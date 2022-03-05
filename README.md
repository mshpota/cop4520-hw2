# COP4520 HOMEWORK 2

## PROBLEM 1:

#### SOLUTION

The idea for the solution is similar to the "prisoners and switch" problem that was discussed during one of the previous lectures. Key elements are:

Minotaur - is represented by scheduler

Guests - are represented by threads.

At the beginning of the party all guests agree that one and only one guest will be counting. If counter comes out of the labyrinth and sees that there is no cupcake available, that is a sign that some other guest has eaten it. So, he asks Minotaur for a new cupcake and increments the count. Only counter guest can ask for a new cupcake. Other guests, when they go through the labyrinth for the first time and there is a cupcake available at the end, they must eat it and leave. If that's not the first time going through the labyrinth, they must leave right after no matter if there is a cupcake or not. Thus, since only one guest counts and if every single guest adheres to this system, when count reaches n - 1 value, the counter guest will know that all guests went through the labyrinth at least once and so he/she can notify Minotaur.

#### IMPLEMENTATION

In this implementation, main method together with scheduler behind the scenes play the role of Minotaur. Guests are implemented with GuestThread class that implements Runnable interface. Program starts as usually in main method and creates one thread with the name Counter that will be counting according to the solution algorithm.  Labyrinth is represented with visitLabyrinth method. Each thread will try to obtain a lock before calling the method and release the lock after leaving the method. Main method waits till count of visitors will reach number of specified guests and then change boolean value isPartyOver. This boolean value controls the while loop of each thread, thus, it will stop each thread. 

This solution resolves the problem. However, because we don't have particular control over how scheduler picks the threads it could be considered somewhat random. Each thread could obtain a lock variable number of times. At the end, only thing  that we know certainly is that each thread has obtained a lock at least once. So in this program threads are experiencing a lot of starvation. Also a lot of contention is happening every time the lock is released. 

**Times:**

10 guests - 32 ms

100 guests - 153 ms

1000 guests - 10884 ms

#### HOW TO RUN

To run this program, navigate to the directory with the file and type the following commands in the terminal window:

```
javac BirthdayParty.java

java BirthdayParty
```

After that, the program will ask for the input of number of guests in the party. Type a positive integer value and press enter. Program will continue with the problem.

## PROBLEM 2:

#### IMPLEMENTATION

To solve this problem I picked strategy 3. To implement it, I used the CLH Queue Lock to simulate the line of guests to enter the showroom[^1]. I prefer this solution because this lock guarantees that no starvation occurs, it also provides first-come-first-served fairness. Even tho some guests will still get to enter the showroom multiple times before all the guests will get a chance, this lock minimizes contention because guests get in line and wait their turn instead of all together competing with each other every time room is available (lock is released). Since there is no specified condition on when the program should stop, I made the decision to keep track of the guests visiting the showroom and when all guests have seen the vase terminate the program. Also some changes were made to the way threads are created, started and terminated in comparison to BirthdayParty.java. However, this program runs much slower than BirthdayParty with Reentrant Lock (without fairness enabled).

**Times:**

10 guests - 9ms

100 guests - 2997 ms

200 guests - 12077 ms

**Alternative strategies:**

Strategy 1: 

*Pros:* fast, much faster than strategy number 3.

*Cons:* lots of contention, no fairness guarantee (guests are all trying to get through  the same door), starvation prone (some guests might wait very long time).

Strategy 2: 

*Pros:* fast; minimized contention (guests only periodically check the sign and go about their business).

*Cons:* no fairness guarantee, starvation prone (some guests will still be unlucky and wait for very long before get a chance to enter the room).

#### HOW TO RUN

Similar to problem 1, to run this program, navigate to the directory with the file and type the following commands in the terminal window:

```
javac CrystalVase.java

java CrystalVase
```

Program will ask for the input of number of guests. Type a positive integer value greater than 1.

[^1]: Herlihy, M., & Shavit, N. (2012). *The Art of Multiprocessor Programming,
 Revised Reprint* (1st ed., pp. 151-154). Morgan Kaufmann.
