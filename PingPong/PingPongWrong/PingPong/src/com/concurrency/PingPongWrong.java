package com.concurrency;
class PingPongWrong{
    public static int mMaxIterations = 10;
    public static void main(String[] args) {
        try {
            System.out.println("Ready...Set...Go!");
            PlayPingPongThread ping = new PlayPingPongThread("Ping!");
            PlayPingPongThread pong = new PlayPingPongThread("Pong!");
            ping.start();
            pong.start();
            ping.join();
            pong.join();
            System.out.println("Done!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class PlayPingPongThread extends Thread {
        private String mStringToPrint;
        public PlayPingPongThread (String stringToPrint) {
            this.mStringToPrint = stringToPrint;
        }
        public void run() {
            for (int loopsDone = 1; loopsDone <= mMaxIterations; ++loopsDone)
                System.out.println(mStringToPrint + "(" + loopsDone + ")");
        }
    }

}