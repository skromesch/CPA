package concurreny.com.example.PingPong;

public abstract class PingPongThread extends Thread {
    protected boolean debug = false;
    protected String mStringToPrint;
    public static int MAX_ITERATION = 10;
    protected PrintlnInterface printlnInterface;

    public PingPongThread(String stringToPrint, PrintlnInterface printlnInterface) {
        this.mStringToPrint = stringToPrint;
        this.printlnInterface = printlnInterface;
    }

    /**
     * Abstract hook methods that determine the ping/pong
     * scheduling protocol in the run() template method.
     */
    abstract void acquire();

    abstract void release();

    public void run() {
        for (int loopsDone = 1; loopsDone <= MAX_ITERATION; ++loopsDone) {
            acquire();
            if (debug) {
                println(mStringToPrint + ":: acquired");
            }
            if (debug) {
                println(mStringToPrint + ":: " + mStringToPrint + "(" + loopsDone + ")");
            } else {
                println(mStringToPrint + "(" + loopsDone + ")");

            }
            release();
            if (debug) {
                println(mStringToPrint + ":: released");
            }
        }

    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    protected void println(String message) {
        printlnInterface.println(message);
    }
}
