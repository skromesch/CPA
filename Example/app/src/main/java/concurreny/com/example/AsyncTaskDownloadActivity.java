package concurreny.com.example;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AsyncTaskDownloadActivity extends DownloadActivity {

    @Override
    void runDownloadImage(String url) {
        // Execute the download using a Thread in the pool of Threads.
        new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }
    /**
     * @class DownloadTask
     *
     * @brief This class downloads a bitmap image in the background
     *        using AsyncTask.
     */
    private class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
        /**
         * Called by the AsyncTask framework in the UI Thread to
         * perform initialization actions.
         */
        protected void onPreExecute() {
            /**
             * Show the progress dialog before starting the download
             * in a Background Thread.
             */
            showDialog("downloading via AsyncTask");
        }

        /**
         * Downloads bitmap in an AsyncTask background thread.
         *
         * @param params
         *            The url of a bitmap image
         */
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        /**
         * Called after an operation executing in the background is
         * completed. It sets the bitmap image to an image view and
         * dismisses the progress dialog.
         *
         * @param image
         *            The bitmap image
         */
        protected void onPostExecute(Bitmap image) {
            /**
             * Dismiss the progress dialog.
             */
            dismissDialog();

            /**
             * Display the downloaded image to the user.
             */
            displayImage(image);
        }
    }
}
