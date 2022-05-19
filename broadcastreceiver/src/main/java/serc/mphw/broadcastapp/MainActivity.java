package serc.mphw.broadcastapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter("serc.mphw.BroadcastMessage");
        MyReceiver myReceiver = new MyReceiver();
        registerReceiver(myReceiver,intentFilter);

    }
}