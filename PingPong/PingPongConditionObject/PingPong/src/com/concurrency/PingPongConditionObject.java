package com.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PingPongConditionObject {
    public static int mMaxIterations = 10;
    /** Maximum number of iterations per "turn" (defaults to 1). */
    public static int mMaxTurns = 1;


    public static void main(String[] args) {
        try {
            System.out.println("Ready...Set...Go with Condition Object!");
            final ReentrantLock lock = new ReentrantLock();
            final Condition pingCondition = lock.newCondition();
            final Condition pongCondition = lock.newCondition();

            final PlayPingPongThreadConditionObject ping = new PlayPingPongThreadConditionObject("Ping!",lock,pingCondition,pongCondition,true);
            final PlayPingPongThreadConditionObject pong = new PlayPingPongThreadConditionObject("Pong!",lock,pongCondition,pingCondition,false);
            ping.setOtherThreadId(pong.getId());
            pong.setOtherThreadId(ping.getId());
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
    public static class PlayPingPongThreadConditionObject extends PingPongThread{

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

        public PlayPingPongThreadConditionObject(String stringToPrint,
                                           ReentrantLock lock,
                                           Condition firstCond,
                                           Condition secondCond,
                                           boolean isOwner)
        {
            super(stringToPrint);
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
    }
}
