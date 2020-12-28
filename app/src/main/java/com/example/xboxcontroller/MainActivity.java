package com.example.xboxcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private XBoxTextView xBoxTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xBoxTextView = (XBoxTextView)findViewById(R.id.xBoxTextView);
        xBoxTextView.setText("This is the initial look");
        Thread object = new Thread(new ConnectionThread());
        object.start();
        xBoxTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xBoxTextView.setText("Is clicked");
            };
        });
    }
    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    private boolean canbePinged(String addr) {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 www.google.com");
            int returnVal = p1.waitFor(); //Cause the thread to wait, if necessary, until the process is terminated. Return immediately if the process is already terminated
            return returnVal == 0;
        }
        catch (IOException e) { xBoxTextView.setText("Interrupted Exception: "+e); }
        catch (InterruptedException e) { xBoxTextView.setText("Interrupted Exception: "+e); }
        return false;
    }
    private class ConnectionThread implements Runnable {
        public void run() {
            //String address = "www.google.com"; //172.27.47.55
            //int port = 8080;
            xBoxTextView.setText("Thread runs." + Thread.currentThread().getId() +"Attempting to connect to server" );
            if (canbePinged("ss")) {
                try {
                    //xBoxTextView.socket = new Socket(address, port);
                    xBoxTextView.setText("Successfully connected to server at ");
                    //xBoxTextView.out = new DataOutputStream(xBoxTextView.socket.getOutputStream());
                }
                catch (Exception e) { xBoxTextView.setText("Exception: "+e); }
            }
            else xBoxTextView.setText("The adress and port cannot be pinged");
        }
    }
}
