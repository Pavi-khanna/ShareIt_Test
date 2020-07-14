package com.example.shareit_test.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<Void,Double,String> {

    private final int SENDER = 1;
    private final int RECEIVER = 0;
    private Context context;
    private TextView statusText;
    private int recv_or_send;

    public Server(Context context, View statusText, int recv_or_send) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.recv_or_send = recv_or_send;
    }


    @Override
    protected String doInBackground(Void... params) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);

            Socket client = serverSocket.accept();

            if(recv_or_send == SENDER){
                //i am the sender
                //start listening and wait for client to send

            }
            else if(recv_or_send == RECEIVER){
                //i am the receiver
                //establish connection to the group owner and start sending

            }
            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();
            //copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e("Server Receiver", e.getMessage());
            return null;
        }
    }
}
