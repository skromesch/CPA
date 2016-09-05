package concurreny.com.rxjavadownload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadActivity extends AppCompatActivity implements Observer<Bitmap>{
    private static final int DEFAULT_IMAGE_DELAY = 2000;
    /**
     * User's selection of URL to download
     */
    private EditText mUrlEditText;

    /**
     * Image that's been downloaded
     */
    private ImageView mImageView;

    /**
     * Display progress of download
     */
    private ProgressDialog mProgressDialog;


    /**
     * Method that initializes the Activity when it is first created.
     *
     * @param savedInstanceState
     *            Activity's previously frozen state, if there was one.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the content view specified in the main.xml file.
        setContentView(R.layout.main);

        // Caches references to the EditText and ImageView objects in
        // data members to optimize subsequent access.
        mImageView = (ImageView) findViewById(R.id.mImageView);
        mUrlEditText = (EditText) findViewById(R.id.mUrlEditText);

    }

    /**
     * Show a toast, notifying a user of an error when retrieving a
     * bitmap.
     */
    void showErrorToast(String errorString) {
        Toast.makeText(this,
                errorString,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,
     * it reports an error via a Toast.
     *
     * @param image
     *            The bitmap image
     */
    void displayImage(Bitmap image)
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
        String url = getUrlString();

        Log.e(DownloadActivity.class.getSimpleName(),
                "Downloading " + url);



        hideKeyboard();

        // Inform the user that the download is starting.
        showDialog("downloading via RxJava observable");
        createObservableFileDownloader(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

    }
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
    }

    /**
     * Dismiss the Dialog
     */
    public void dismissDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /**
     * Hide the keyboard after a user has finished typing the url.
     */
    private void hideKeyboard() {
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
    String getUrlString() {
        String s = mUrlEditText.getText().toString();
        return s;
    }
    Observable<Bitmap> createObservableFileDownloader(final String url){
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> observer) {
                try {
                    if (!observer.isUnsubscribed()) {
                        final Bitmap image;
                        if (url.equals("")) {
                            image = getDefaultImageWithDelay(DEFAULT_IMAGE_DELAY);
                        } else {
                            /**
                             * Connect to a remote server, download the contents of
                             * the image, and provide access to it via an Input
                             * Stream. */
                            InputStream is =
                                    (InputStream) new URL(url).getContent();

                            /**
                             * Decode an InputStream into a Bitmap.
                             */
                            image = BitmapFactory.decodeStream(is);
                        }
                        observer.onNext(image);
                        observer.onCompleted();
                    }
                }catch (OutOfMemoryError e){
                    final Exception ex = new Exception("Error download image: Out of memory");
                    observer.onError(ex);
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        });
    }

    private Bitmap getDefaultImageWithDelay(int defaultImageDelay) {
        try{
            Thread.sleep(defaultImageDelay);
        } catch (InterruptedException e) {
        }
        final Resources resources = getResources();
        return BitmapFactory.decodeResource(resources,R.drawable.download);
    }


    @Override
    public void onCompleted() {
        dismissDialog();
    }

    @Override
    public void onError(Throwable e) {
        dismissDialog();
        showErrorToast(e.getMessage());
    }

    @Override
    public void onNext(Bitmap s) {
        displayImage(s);
    }
}