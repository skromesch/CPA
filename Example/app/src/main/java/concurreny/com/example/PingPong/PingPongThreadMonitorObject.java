package concurreny.com.example.PingPong;

/**
 * Created by Donnie on 2016. 08. 27..
 */
public class PingPongThreadMonitorObject extends PingPongThread{

    /**
     * Semaphores that schedule the ping/pong algorithm.
     */
    private BinarySemaphore mSemaphores[] = new BinarySemaphore[2];

    /**
     * Consts to distinguish between ping and pong BinarySemaphores.
     */
    private final static int FIRST_SEMA = 0;
    private final static int SECOND_SEMA = 1;


    public PingPongThreadMonitorObject(String stringToPrint,PrintlnInterface printlnInterface,
                                           BinarySemaphore firstSema,
                                           BinarySemaphore secondSema,
                                           boolean isOwner)
    {
        super(stringToPrint,printlnInterface);
        mSemaphores[FIRST_SEMA] = firstSema;
        mSemaphores[SECOND_SEMA] = secondSema;
    }


    @Override
    void acquire() {
        mSemaphores[FIRST_SEMA].acquire();
    }

    @Override
    void release() {
        mSemaphores[SECOND_SEMA].release();
    }
    public static Thread create(final PrintlnInterface printlnInterface) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printlnInterface.println("Ready...Set...Go with Monitor Object!");
                    final BinarySemaphore pingSemaphore = new BinarySemaphore(false);
                    final BinarySemaphore pongSemaphore = new BinarySemaphore(true);

                    final PingPongThreadMonitorObject ping = new PingPongThreadMonitorObject("Ping!", printlnInterface, pingSemaphore, pongSemaphore, true);
                    final PingPongThreadMonitorObject pong = new PingPongThreadMonitorObject("Pong!", printlnInterface, pongSemaphore, pingSemaphore, false);
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
