package concurreny.com.example.PingPong;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PingPongThreadConditionObject extends PingPongThread{
    public static int mMaxTurns = 1;
    /**
     * Semaphores that schedule the ping/pong algorithm.
     */
    private Condition mConds[] = new Condition[2];
    /**
     * Number of times we've iterated thus far in our "turn".
     */
    private int mTurnCountDown = 0;
    /**
     * Monitor lock.
     */
    private ReentrantLock mLock = null;
    /**
     * Id for the other thread.
     */
    private long mOtherThreadId = 0;

    /**
     * Thread whose turn it currently is.
     */
    private static long mTurnOwner;

    public void setOtherThreadId(long otherThreadId)
    {
        this.mOtherThreadId = otherThreadId;
    }

    /**
     * Consts to distinguish between ping and pong conditions.
     */
    private final static int FIRST_COND = 0;
    private final static int SECOND_COND = 1;

    public PingPongThreadConditionObject(String stringToPrint,
                                             PrintlnInterface printlnInterface,
                                             ReentrantLock lock,
                                             Condition firstCond,
                                             Condition secondCond,
                                             boolean isOwner)
    {
        super(stringToPrint,printlnInterface);
        mTurnCountDown = mMaxTurns;
        mLock = lock;
        mConds[FIRST_COND] = firstCond;
        mConds[SECOND_COND] = secondCond;
        if (isOwner) {
            mTurnOwner = this.getId();
        }
    }



    @Override
    void acquire() {
        mLock.lock();

        while (mTurnOwner != this.getId()) {
            mConds[FIRST_COND].awaitUninterruptibly();
        }

        mLock.unlock();
    }

    @Override
    void release() {

        mLock.lock();

        --mTurnCountDown;
        if (mTurnCountDown == 0) {
            mTurnOwner = mOtherThreadId;
            mTurnCountDown = mMaxTurns;
            mConds[SECOND_COND].signal();
        }

        mLock.unlock();
    }
    public static Thread create(final PrintlnInterface printlnInterface){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printlnInterface.println("Ready...Set...Go with Semaphores!");
                    final Semaphore pingSemaphore = new Semaphore(1); // Starts out unlocked.
                    final Semaphore pongSemaphore = new Semaphore(0);

                    final PingPongThreadSemaphore ping = new PingPongThreadSemaphore("Ping!",printlnInterface,pingSemaphore,pongSemaphore);
                    final PingPongThreadSemaphore pong = new PingPongThreadSemaphore("Pong!",printlnInterface,pongSemaphore,pingSemaphore);
                    ping.start();
                    pong.start();
                    ping.join();
                    pong.join();
                    printlnInterface.println("Done!");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
