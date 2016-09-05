package concurreny.com.example.PingPong;

public class BinarySemaphore {
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
