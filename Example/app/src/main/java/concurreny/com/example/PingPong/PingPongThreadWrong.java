package concurreny.com.example.PingPong;

/**
 * Created by Donnie on 2016. 08. 27..
 */
public class PingPongThreadWrong extends PingPongThread {
    public PingPongThreadWrong(String stringToPrint,PrintlnInterface printlnInterface) {
        super(stringToPrint,printlnInterface);
    }

    @Override
    void acquire() {

    }

    @Override
    void release() {

    }
    public static Thread create(final PrintlnInterface printlnInterface) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printlnInterface.println("Ready...Set...Go!");
                    PingPongThreadWrong ping = new PingPongThreadWrong("Ping!",printlnInterface);
                    PingPongThreadWrong pong = new PingPongThreadWrong("Pong!",printlnInterface);
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
