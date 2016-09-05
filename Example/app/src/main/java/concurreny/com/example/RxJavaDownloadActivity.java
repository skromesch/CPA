package concurreny.com.example;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxJavaDownloadActivity extends DownloadActivity implements Observer<Bitmap> {


    @Override
    void runDownloadImage(String url) {
        // Inform the user that the download is starting.
        showDialog("downloading via RxJava observable");
        createObservableFileDownloader(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }
    Observable<Bitmap> createObservableFileDownloader(final String url){
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> observer) {
                try {
                    if (observer.isUnsubscribed()) {
                        return;
                    }
                    try {
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
                    }catch (OutOfMemoryError ex){
                        final Exception outOfMemory = new Exception("failed download image: Out of memory");
                        observer.onError(outOfMemory);
                    }

                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        });
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
