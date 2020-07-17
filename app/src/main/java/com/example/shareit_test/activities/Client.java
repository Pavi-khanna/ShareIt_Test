package com.example.shareit_test.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.example.shareit_test.activities.Utils.copyFile;

public class Client extends AsyncTask<Void,Double,String> {

    private final int SENDER = 1;
    private final int RECEIVER = 0;
    private Context context;
    private TextView statusText;
    private int recv_or_send,len;
    private String FilePath = null;
    byte buf[]  = new byte[1024];
    private String destIP = null;


    // init this way with file path when sending a file
    public Client(Context context, View statusText, int recv_or_send, String destIP, String FilePath) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.recv_or_send = recv_or_send;
        this.FilePath = FilePath;
        this.destIP = destIP;
    }


    @Override
    protected String doInBackground(Void... params) {
        Socket clientToServer = null;
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            // TODO: get the IP of the owner
            // Set server socket ip and port
            clientToServer = new Socket(destIP,8888);

            if (recv_or_send == SENDER) {
                //i am the sender
                //connected... now start sending
                try {
                    OutputStream outputStream = clientToServer.getOutputStream();
                    ContentResolver cr = context.getContentResolver();
                    InputStream inputStream = null;
                    inputStream = cr.openInputStream(Uri.parse(FilePath));
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    Log.e("Client Receiver", e.getMessage());
                } catch (IOException e) {
                    Log.e("Client Receiver", e.getMessage());
                }

                return null;
            } else if (recv_or_send == RECEIVER) {
                //i am the receiver
                //connected... now read the input stream to receive

                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/ShareIt_test-" + System.currentTimeMillis()
                        + ".mp4");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                InputStream inputstream = clientToServer.getInputStream();
                // not handling copyfile stream failure
                copyFile(inputstream, new FileOutputStream(f));
                inputstream.close();
                return f.getAbsolutePath();
            }
        } catch(IOException e){
            Log.e("Client Receiver", e.getMessage());
            return null;
        }
        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (clientToServer != null) {
                if (clientToServer.isConnected()) {
                    try {
                        clientToServer.close();
                    } catch (IOException e) {
                        Log.e("Client Sender", e.getMessage());
                    }
                }
            }
        }

        return null;
    }
}
