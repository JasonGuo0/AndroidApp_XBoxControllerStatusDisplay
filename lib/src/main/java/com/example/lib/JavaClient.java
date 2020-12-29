package com.example.lib;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class JavaClient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream out;

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

    public JavaClient(String address, int port) {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 www.google.com");
            int returnVal = p1.waitFor(); //Cause the thread to wait, if necessary, until the process is terminated. Return immediately if the process is already terminated
            String s = "Pinging finished. Ping successful? ";
            s += returnVal == 0;
            System.out.println(s);
        }
        catch (IOException e) { System.out.println("Interrupted Exception: "+e); }
        catch (InterruptedException e) { System.out.println("Interrupted Exception: "+e); }

        //System.out.println("Hello world");
        //try {
        //    //The connection is established simply by instantiating the Socket
        //    socket = new Socket(address, port);
        //    System.out.println("Connected");
        //    input = new DataInputStream(System.in);
        //    out = new DataOutputStream(socket.getOutputStream());
        //}
        //catch (UnknownHostException u) {
        //    System.out.println(u);
        //}
        //catch (IOException i) {
        //    System.out.println(i);
        //}
//
        //String line = "Good";
        //byte[] byteStream;
        //int i = 0;
//
        //while (!line.equals("Over")) {
        //    try {
        //        if (i > 4) line = "Over";
        //        else {
        //            i ++;
        //            line = "Good" + i;
        //        }
        //        System.out.println("Sending string " + line);
        //        byteStream = line.getBytes();
        //        //out.writeInt(byteStream.length);
        //        out.write(byteStream);
        //        try {
        //            TimeUnit.SECONDS.sleep(1);
        //        }
        //        catch (InterruptedException e) {
        //            System.out.println(e);
        //        }
        //    }
        //    catch (IOException e) {
        //        System.out.println(e);
        //    }
        //}
        //try {
        //    input.close();
        //    out.close();
        //    socket.close();
        //}
        //catch (IOException e) {
        //    System.out.println(e);
        //}
    }

    public static void main(String args[])
    {
        JavaClient client = new JavaClient("127.0.0.1", 65432);
    }
}

