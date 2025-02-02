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
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static com.example.shareit_test.activities.Utils.copyFile;

public class Client extends AsyncTask<Void,Void,String> {

    private final int SENDER = 1;
    private final int RECEIVER = 0;
    private Context context;
    private TextView statusText;
    private int recv_or_send,len;
    private Uri FilePath = null;
    byte buf[]  = new byte[4096];
    private String destIP = null;


    // init this way with file path when sending a file
    public Client(Context context, View statusText, int recv_or_send, String destIP, Uri FilePath) {
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
            int numberOfTimesTried = 1;
            // Set server socket ip and port
            while(numberOfTimesTried<10) {
                try {
                    clientToServer = new Socket(destIP, 8888);
                    Log.d("CLIENT","Connection Established with server");
                    break;
                } catch (ConnectException e) {
                    Log.e("CLIENT", "Error while connecting. " + e.getMessage());
                } catch (SocketTimeoutException e) {
                    Log.e("CLIENT", "Connection: " + e.getMessage() + ".");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000); //milliseconds

                } catch (InterruptedException e) {
                    Log.e("CLIENT","Interrupted before connection");
                }
                Log.d("CLIENT","Trying again:"+numberOfTimesTried++);
            }


            if (recv_or_send == SENDER) {
                //i am the sender
                //connected... now start sending
                try {
                    OutputStream outputStream = clientToServer.getOutputStream();
                    ContentResolver cr = context.getContentResolver();
                    InputStream inputStream = null;
                    inputStream = cr.openInputStream(FilePath);
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                        Log.d("Client Sender", "Sent "+len+" bytes");
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    Log.e("Client Sender", e.getMessage());
                } catch (IOException e) {
                    Log.e("Client Sender", e.getMessage());
                }

                return null;
            } else if (recv_or_send == RECEIVER) {
                //i am the receiver
                //connected... now read the input stream to receive

                final File f = new File(Environment.getExternalStorageDirectory() + "/ShareIt_test-" + System.currentTimeMillis()
                        + ".mp4");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                long tic = System.nanoTime();
                InputStream inputstream = clientToServer.getInputStream();
                // not handling copyfile stream failure
                copyFile(inputstream, new FileOutputStream(f));
                long elapsedTime = System.nanoTime() - tic;
                long elapsedTimeSecs = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
                Log.d("Receiver","Received File in "+elapsedTimeSecs+" secs");
                inputstream.close();
                //statusText.setText("File Received");
                return f.getAbsolutePath();
            }
        } catch(IOException e){
            Log.e("Client Sender", e.getMessage());
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
    @Override
    protected void onProgressUpdate (Void... values) {

        // Updating the TextView
        statusText.setText("File Sent");
    }
}
