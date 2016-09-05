package concurreny.com.example;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

abstract public class DownloadActivity extends AppCompatActivity {
    /**
     * Debug Tag for logging debug output to LogCat
     */
    private final static String TAG =
            DownloadActivity.class.getSimpleName();

    public static final int DEFAULT_IMAGE_DELAY = 2000;
    /**
     * User's selection of URL to download
     */
    protected EditText mUrlEditText;

    /**
     * Image that's been downloaded
     */
    protected ImageView mImageView;


    /**
     * Display progress of download
     */
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Caches references to the EditText and ImageView objects in
        // data members to optimize subsequent access.
        mUrlEditText = (EditText) findViewById(R.id.mUrlEditText);
        mImageView = (ImageView) findViewById(R.id.mImageView);
    }
    /**
     * Show a toast, notifying a user of an error when retrieving a
     * bitmap.
     */
    protected void showErrorToast(String errorString) {
        Toast.makeText(this,
                errorString,
                Toast.LENGTH_LONG).show();
    }

    protected Bitmap getDefaultImageWithDelay(int defaultImageDelay) {
        try{
            Thread.sleep(defaultImageDelay);
        } catch (InterruptedException e) {
        }
        final Resources resources = getResources();
        return BitmapFactory.decodeResource(resources,R.drawable.download);
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,
     * it reports an error via a Toast.
     *
     * @param image
     *            The bitmap image
     */
    protected void displayImage(Bitmap image)
    {
        if (mImageView == null)
            showErrorToast("Problem with Application,"
                    + " please contact the Developer.");
        else if (image != null)
            mImageView.setImageBitmap(image);
        else
            showErrorToast("image is corrupted,"
                    + " please check the requested URL.");
    }

    /**
     * Called when a user clicks a button to reset an image to
     * default.
     *
     * @param view
     *            The "Reset Image" button
     */
    public void resetImage(View view) {
        mImageView.setImageResource(R.drawable.default_image);
    }
    /**
     * Called when a user clicks the Download Image button to download
     * an image using the DownloadService
     *
     * @param view
     *            The "Download Image" button
     */
    public void downloadImage(View view) {
        // Obtain the requested URL from the user input.
        final String url = getUrlString();

        Log.e(DownloadActivity.class.getSimpleName(),
                "Downloading " + url);

        hideKeyboard();

        runDownloadImage(url);
    }
    abstract void runDownloadImage(String url);

    /**
     * Display the Dialog to the User.
     *
     * @param message
     *          The String to display what download method was used.
     */
    public void showDialog(String message) {
        mProgressDialog =
                ProgressDialog.show(this,
                        "Download",
                        message,
                        true);
    }    /**
     * Dismiss the Dialog
     */
    public void dismissDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /**
     * Hide the keyboard after a user has finished typing the url.
     */
    protected void hideKeyboard() {
        InputMethodManager mgr =
                (InputMethodManager) getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mUrlEditText.getWindowToken(),
                0);
    }


    /**
     * Read the URL EditText and return the String it contains.
     *
     * @return String value in mUrlEditText
     */
    protected String getUrlString() {
        String s = mUrlEditText.getText().toString();
        return s;
    }
    /**
     * Download a bitmap image from the URL provided by the user.
     *
     * @param url
     *            The url where a bitmap image is located
     * @return the image bitmap or null if there was an error
     */
    public Bitmap downloadImage(String url) {
        try {
            /**
             * Use the default Image if the user doesn't supply one URL.
             */
            if (url.equals(""))
                return getDefaultImageWithDelay(DEFAULT_IMAGE_DELAY);
            /**
             * Connect to a remote server, download the contents of
             * the image, and provide access to it via an Input
             * Stream. */
            InputStream is =
                    (InputStream) new URL(url).getContent();

            /**
             * Decode an InputStream into a Bitmap.
             */
            final Bitmap image = BitmapFactory.decodeStream(is);
            return image;
        }catch (OutOfMemoryError e){
            /**
             * Post error reports to the UI Thread.
             */
            this.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Use a Toast to inform user that something
                     * has gone wrong.
                     */
                    showErrorToast("Error downloading image,"
                            + " the image is too big.");
                }
            });
            Log.e(TAG, "Error downloading image: OutOfMemory");
            return null;
        } catch (Exception e) {
            /**
             * Post error reports to the UI Thread.
             */
            this.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Use a Toast to inform user that something
                     * has gone wrong.
                     */
                    showErrorToast("Error downloading image,"
                            + " please check the requested URL.");
                }
            });
            Log.e(TAG, "Error downloading image");
            e.printStackTrace();
            return null;
        }
    }

}
