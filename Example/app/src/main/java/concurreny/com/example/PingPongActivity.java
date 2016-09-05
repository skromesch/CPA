package concurreny.com.example;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import concurreny.com.example.PingPong.BinarySemaphore;
import concurreny.com.example.PingPong.PingPongThreadConditionObject;
import concurreny.com.example.PingPong.PingPongThreadMonitorObject;
import concurreny.com.example.PingPong.PingPongThreadSemaphore;
import concurreny.com.example.PingPong.PingPongThreadWrong;
import concurreny.com.example.PingPong.PrintlnInterface;

public class PingPongActivity extends AppCompatActivity implements PrintlnInterface{
    /**
     * Debug Tag for logging debug output to LogCat
     */
    private final static String TAG =
            PingPongActivity.class.getSimpleName();
    private TextView mOutpuTextView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_pong);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mOutpuTextView = (TextView)findViewById(R.id.output);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ping_pong, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_pp_wrong:
                startWrong();
                return true;
            case R.id.menu_pp_semaphore:
                startSemaphore();
                return true;
            case R.id.menu_pp_conditionobject:
                startConditionObject();
                return true;
            case R.id.menu_pp_monitorobject:
                startMonitorObject();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startMonitorObject() {
        final PrintlnInterface printlnInterface = this;
        PingPongThreadMonitorObject.create(printlnInterface).start();
    }

    private void startConditionObject() {
        final PrintlnInterface printlnInterface = this;
        PingPongThreadConditionObject.create(printlnInterface).start();
    }

    private void startSemaphore() {
        final PrintlnInterface printlnInterface = this;
        PingPongThreadSemaphore.create(printlnInterface).start();
    }

    private void startWrong() {
        final PrintlnInterface printlnInterface = this;
        PingPongThreadWrong.create(printlnInterface).start();
    }

    public void println(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutpuTextView.setText(mOutpuTextView.getText()+msg+"\n");
            }
        });
    }
    public void resetOutput(View view){
        if(mOutpuTextView == null){
            return;
        }
        mOutpuTextView.setText("");
    }
}
