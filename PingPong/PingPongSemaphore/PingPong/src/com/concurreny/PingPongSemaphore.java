package com.concurreny;

import java.util.concurrent.Semaphore;

public class PingPongSemaphore {
    public static int mMaxIterations = 10;
    public static void main(String[] args) {
        try {
            System.out.println("Ready...Set...Go with Semaphores!");
            final Semaphore pingSemaphore = new Semaphore(1); // Starts out unlocked.
            final Semaphore pongSemaphore = new Semaphore(0);

            final PlayPingPongThreadSemaphore ping = new PlayPingPongThreadSemaphore("Ping!",pingSemaphore,pongSemaphore);
            final PlayPingPongThreadSemaphore pong = new PlayPingPongThreadSemaphore("Pong!",pongSemaphore,pingSemaphore);
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
        public void setDebug(boolean debug){
            this.debug = debug;
        }
        protected void println(String message){
            System.out.println(message);
        }
    }
    public static class PlayPingPongThreadSemaphore extends PingPongThread{
        /**
         * Semaphores that schedule the ping/pong algorithm
         */
        private Semaphore mSemas[] = new Semaphore[2];

        /**
         * Consts to distinguish between ping and pong Semaphores.
         */
        private final static int FIRST_SEMA = 0;
        private final static int SECOND_SEMA = 1;

        public PlayPingPongThreadSemaphore(String stringToPrint,
                                           Semaphore firstSema,
                                           Semaphore secondSema)
        {
            super(stringToPrint);
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
    }
}
