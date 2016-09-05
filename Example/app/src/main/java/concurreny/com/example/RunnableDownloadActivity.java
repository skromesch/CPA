package concurreny.com.example;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RunnableDownloadActivity extends DownloadActivity {

    @Override
    void runDownloadImage(String url) {
        /**
         * Inform the user that the download is starting.
         */

        showDialog("downloading via Runnables and Handlers");

        /**
         * Create and start a new Thread to download an image in the
         * background via a Runnable. The downloaded image is then
         * diplayed in the UI Thread by posting another Runnable via
         * the Activity's runOnUiThread() method, which uses an
         * internal Handler.
         */
        new Thread(new RunnableWithHandlers(url)).start();
    }
    /**
     * @class RunnablesWithHandler
     *
     * @brief This class downloads a bitmap image in the background
     *        using Runnables and Handlers.
     */
    private class RunnableWithHandlers implements Runnable {
        /*
         * The URL to download.
         */
        String mUrl;

        /**
         * Class constructor caches the url of the bitmap image.
         *
         * @param url
         *            The bitmap image url
         */
        RunnableWithHandlers(String url) {
            mUrl = url;
        }

        /**
         * Download a bitmap image in the background. It also sets the
         * image to an image view and dismisses the progress dialog.
         */
        public void run() {
            final Bitmap image = downloadImage(getUrlString());

            RunnableDownloadActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Dismiss the progress dialog.
                     */
                    RunnableDownloadActivity.this.dismissDialog();

                    /**
                     * Display the downloaded image to the user.
                     */
                    displayImage(image);
                }
            });
        }
    }
}
