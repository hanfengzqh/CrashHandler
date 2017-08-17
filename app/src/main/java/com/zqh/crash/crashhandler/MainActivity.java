package com.zqh.crash.crashhandler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_crash = (Button)findViewById(R.id.bt_crash);
        bt_crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 5/0;
            }
        });
    }
}
