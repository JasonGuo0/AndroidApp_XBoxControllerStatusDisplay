package com.example.xboxcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private XBoxTextView xBoxTextView;
    private EditText entryAddr;
    private EditText entryPort;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xBoxTextView = (XBoxTextView)findViewById(R.id.xBoxTextView);
        entryAddr = findViewById(R.id.entryAddr);
        entryPort = findViewById(R.id.entryPort);
        btnConnect = findViewById(R.id.btnConnect);

        xBoxTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xBoxTextView.setText("Is clicked");
            };
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = entryAddr.getText().toString();
                int port = Integer.parseInt(entryPort.getText().toString());
                xBoxTextView.connect2Server(addr, port);
            }
        });
    }

}
