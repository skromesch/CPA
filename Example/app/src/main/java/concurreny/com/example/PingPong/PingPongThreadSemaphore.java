package concurreny.com.example.PingPong;

import java.util.concurrent.Semaphore;

/**
 * Created by Donnie on 2016. 08. 27..
 */
public class PingPongThreadSemaphore extends PingPongThread {
    /**
     * Semaphores that schedule the ping/pong algorithm
     */
    private Semaphore mSemas[] = new Semaphore[2];

    /**
     * Consts to distinguish between ping and pong Semaphores.
     */
    private final static int FIRST_SEMA = 0;
    private final static int SECOND_SEMA = 1;

    public PingPongThreadSemaphore(String stringToPrint, PrintlnInterface printlnInterface,
                                   Semaphore firstSema,
                                   Semaphore secondSema)
    {
        super(stringToPrint,printlnInterface);
        mSemas[FIRST_SEMA] = firstSema;
        mSemas[SECOND_SEMA] = secondSema;
    }



    @Override
    void acquire() {
        mSemas[FIRST_SEMA].acquireUninterruptibly();
    }

    @Override
    void release() {
        mSemas[SECOND_SEMA].release();
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
