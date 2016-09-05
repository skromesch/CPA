package concurreny.com.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void runPingPong(View view){
        startDownloadActivity(PingPongActivity.class);
    }
    public void runRunnable(View view){
        startDownloadActivity(RunnableDownloadActivity.class);
    }
    public void runMessages(View view){
        startDownloadActivity(MessagesDownloadActivity.class);
    }
    public void runAsyncTask(View view){
        startDownloadActivity(AsyncTaskDownloadActivity.class);
    }

    public void runService(View view){
        startDownloadActivity(ServiceDownloadActivity.class);
    }
    public void runRxJava(View view){
        startDownloadActivity(RxJavaDownloadActivity.class);
    }
    private void startDownloadActivity(Class className){
        final Intent intent = new Intent(this,className);
        startActivity(intent);
    }
}
