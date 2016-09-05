package com.concurrency;


public class PingPongMonitorObject {
    public static int mMaxIterations = 10;

    public static void main(String[] args) {
        try {
            System.out.println("Ready...Set...Go with Monitor Object!");
            final BinarySemaphore pingSemaphore = new BinarySemaphore(false);
            final BinarySemaphore pongSemaphore = new BinarySemaphore(true);

            final PlayPingPongThreadMonitorObject ping = new PlayPingPongThreadMonitorObject("Ping!",pingSemaphore,pongSemaphore,true);
            final PlayPingPongThreadMonitorObject pong = new PlayPingPongThreadMonitorObject("Pong!",pongSemaphore,pingSemaphore,false);
            ping.start();
            pong.start();
            ping.join();
            pong.join();
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static abstract class PingPongThread extends Thread {
        private boolean debug = false;
        private String mStringToPrint;
        public PingPongThread (String stringToPrint) {
            this.mStringToPrint = stringToPrint;
        }
        /**
         * Abstract hook methods that determine the ping/pong
         * scheduling protocol in the run() template method.
         */
        abstract void acquire();
        abstract void release();
        public void run() {
            for (int loopsDone = 1; loopsDone <= mMaxIterations; ++loopsDone){
                acquire();
                if(debug){
                    println(mStringToPrint+":: acquired");
                }
                if(debug) {
                    println(mStringToPrint+":: "+mStringToPrint + "(" + loopsDone + ")");
                }else{
                    println(mStringToPrint + "(" + loopsDone + ")");

                }
                release();
                if(debug){
                    println(mStringToPrint + ":: released");
                }
            }
        }
        /**
         * Sets the id of the other thread.
         */
        void setOtherThreadId(long id) {}

        public void setDebug(boolean debug){
            this.debug = debug;
        }
        protected void println(String message){
            System.out.println(message);
        }
    }
    private static class BinarySemaphore {
        private Boolean mLocked;
        private Object mMonObj;

        public BinarySemaphore(Boolean locked) {
            mLocked = locked;
            mMonObj = new Object();
        }
        public void acquire() {
            synchronized(mMonObj) {
                while (mLocked)
                    try {
                        mMonObj.wait();
                    } catch (InterruptedException e) {
                        // ignore.
                    }
                mLocked = true;
            }
        }
        public void release() {
            synchronized(mMonObj) {
                mLocked = false;
                mMonObj.notify();
            }
        }
    }


    public static class PlayPingPongThreadMonitorObject extends PingPongThread{

        /**
         * Semaphores that schedule the ping/pong algorithm.
         */
        private BinarySemaphore mSemaphores[] = new BinarySemaphore[2];

        /**
         * Consts to distinguish between ping and pong BinarySemaphores.
         */
        private final static int FIRST_SEMA = 0;
        private final static int SECOND_SEMA = 1;


        public PlayPingPongThreadMonitorObject(String stringToPrint,
                                               BinarySemaphore firstSema,
                                               BinarySemaphore secondSema,
                                               boolean isOwner)
        {
            super(stringToPrint);
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
    }
}
