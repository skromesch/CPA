package concurreny.com.example;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class ServiceDownloadActivity extends DownloadActivity {
    /**
     * Stores an instance of DownloadHandler.
     */
    Handler mDownloadHandler = null;

    /**
     * Method that initializes the Activity when it is first created.
     *
     * @param savedInstanceState
     *            Activity's previously frozen state, if there was one.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the downloadHandler.
        mDownloadHandler = new DownloadHandler(this);
    }

    @Override
    void runDownloadImage(String url) {
        // Inform the user that the download is starting.
        showDialog("downloading via startService()");

        // Create an Intent to download an image in the background via
        // a Service.  The downloaded image is later diplayed in the
        // UI Thread via the downloadHandler() method defined below.
        Intent intent =
                DownloadService.makeIntent(this,
                        Uri.parse(url),
                        mDownloadHandler);

        // Start the DownloadService.
        startService(intent);
    }

    /**
     * @class DownloadHandler
     *
     * @brief An inner class that inherits from Handler and uses its
     *        handleMessage() hook method to process Messages sent to
     *        it from the DownloadService.
     */
    private static class DownloadHandler extends Handler {
        /**
         * Allows Activity to be garbage collected properly.
         */
        private WeakReference<DownloadActivity> mActivity;

        /**
         * Class constructor constructs mActivity as weak reference
         * to the activity
         *
         * @param activity
         *            The corresponding activity
         */
        public DownloadHandler(DownloadActivity activity) {
            mActivity = new WeakReference<DownloadActivity>(activity);
        }

        /**
         * This hook method is dispatched in response to receiving the
         * pathname back from the DownloadService.
         */
        public void handleMessage(Message message) {
            DownloadActivity activity = mActivity.get();
            // Bail out if the DownloadActivity is gone.
            if (activity == null)
                return;

            // Try to extract the pathname from the message.
            String pathname = DownloadService.getPathname(message);

            // See if the download worked or not.
            if (pathname == null)
                activity.showDialog("failed download");

            // Stop displaying the progress dialog.
            activity.dismissDialog();
            try {
                // Display the image in the UI Thread.
                if (pathname.equals("")) {
                    activity.displayImage(BitmapFactory.decodeResource(activity.getResources(), R.drawable.download));
                } else {
                    activity.displayImage(BitmapFactory.decodeFile(pathname));
                }
            }catch (OutOfMemoryError e){
                activity.showDialog("failed download: Out of memory");
            }catch (Exception e){
                activity.showDialog("failed download: " + e.getMessage());

            }
        }
    };
}
