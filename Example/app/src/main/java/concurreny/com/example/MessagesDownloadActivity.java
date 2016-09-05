package concurreny.com.example;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MessagesDownloadActivity extends DownloadActivity {

    @Override
    void runDownloadImage(String url) {
        /**
         * Create and start a new Thread to download an image in the
         * background and then use Messages and MessageHandler to
         * cause it to be displayed in the UI Thread.
         */
        new Thread(new RunnableWithMessages(url)).start();
    }

    /**
     * @class MessageHandler
     *
     * @brief A static inner class that inherits from Handler and uses
     *        its handleMessage() hook method to process Messages sent
     *        to it from a background Thread. Since it's static its
     *        instances do not hold implicit references to their outer
     *        classes.
     */
    private static class MessageHandler extends Handler {
        /**
         * Types of Messages that can be passed from a background
         * Thread to the UI Thread to specify which processing to
         * perform.
         */
        static final int SHOW_DIALOG = 1;
        static final int DISMISS_DIALOG = 2;
        static final int DISPLAY_IMAGE = 3;

        /**
         * Allows Activity to be garbage collected properly.
         */
        private WeakReference<MessagesDownloadActivity> mActivity;

        /**
         * Class constructor constructs mActivity as weak reference
         * to the activity
         *
         * @param activity
         *            The corresponding activity
         */
        public MessageHandler(MessagesDownloadActivity activity) {
            mActivity = new WeakReference<MessagesDownloadActivity>(activity);
        }
        /**
         * Process the specified Messages passed to MessageHandler in
         * the UI Thread. These Messages instruct the Handler to start
         * showing the progress dialog, dismiss it, or display the
         * designated bitmap image via the ImageView.
         */
        public void handleMessage(Message msg) {

            /*
             * Check to see if the activity still exists and return if
             * not.
             */
            MessagesDownloadActivity activity = mActivity.get();
            if (activity == null)
                return;

            switch (msg.what) {

                case SHOW_DIALOG:
                    activity.showDialog("downloading via Handlers and Messages");
                    break;

                case DISMISS_DIALOG:
                    /**
                     * Dismiss the progress dialog.
                     */
                    activity.dismissDialog();
                    break;

                case DISPLAY_IMAGE:
                    /**
                     * Display the downloaded image to the user.
                     */
                    activity.displayImage((Bitmap) msg.obj);
                    break;
            }
        }
    }    /**
     * Instance of MessageHandler.
     */
    MessageHandler messageHandler = new MessageHandler(this);
    /**
     * @class RunnableWithMessages
     *
     * @brief This class downloads a bitmap image in the background
     *        using Handlers and Messages.
     */
    private class RunnableWithMessages implements Runnable {
        /*
         * The URL to download.
         */
        String mUrl;

        /**
         * Class constructor caches the url of a bitmap image and a
         * handler.
         *
         * @param url
         *            The bitmap image url
         */
        RunnableWithMessages(String url) {
            this.mUrl = url;
        }

        /**
         * Download a bitmap image in a background Thread. It sends
         * various messages to the mHandler running in the UI Thread.
         */
        public void run() {
            /**
             * Store a copy of the reference to the MessageHandler.
             */
            final MessageHandler mHandler =
                    MessagesDownloadActivity.this.messageHandler;

            /**
             * Factory creates a Message that instructs the
             * MessageHandler to begin showing the progress dialog to
             * the user.
             */
            Message msg =
                    mHandler.obtainMessage(MessageHandler.SHOW_DIALOG,
                            mProgressDialog);

            /**
             * Send the Message to initiate the ProgressDialog.
             */
            mHandler.sendMessage(msg);

            /**
             * Download the image.
             */
            final Bitmap image = downloadImage(mUrl);

            /**
             * Factory creates a Message that instructs the
             * MessageHandler to dismiss the progress dialog.
             */
            msg = mHandler.obtainMessage(MessageHandler.DISMISS_DIALOG,
                    mProgressDialog);
            /**
             * Send the Message to dismiss the ProgressDialog.
             */
            mHandler.sendMessage(msg);

            /**
             * Factory creates a Message that instructs the
             * MessageHandler to display the image to the user.
             */
            msg = mHandler.obtainMessage(MessageHandler.DISPLAY_IMAGE,
                    image);
            /**
             * Send the Message to instruct the UI Thread to display
             * the image.
             */
            mHandler.sendMessage(msg);
        }
    }
}
