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
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.shareit_test.activities.Utils.copyFile;

public class Server extends AsyncTask<Void,Double,String> {

    private final int SENDER = 1;
    private final int RECEIVER = 0;
    private Context context;
    private TextView statusText;
    private int recv_or_send,len;
    private String FilePath = null;
    byte buf[]  = new byte[1024];


    // init this way with file path when sending a file
    public Server(Context context, View statusText, int recv_or_send, String FilePath) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.recv_or_send = recv_or_send;
        this.FilePath = FilePath;
    }


    @Override
    protected String doInBackground(Void... params) {
        ServerSocket serverSocket;
        Socket client = null;
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            // Server is always the group owner
            // Set server socket ip (no need) and port
            serverSocket = new ServerSocket(8888);
            //start listening
            client = serverSocket.accept();

            if (recv_or_send == SENDER) {
                //i am the sender
                //start listening for the client to make connection then start sending
                try {
                    /**
                     * Create a byte stream from a JPEG file and pipe it to the output stream
                     * of the socket. This data is retrieved by the server device.
                     */
                    OutputStream outputStream = client.getOutputStream();
                    ContentResolver cr = context.getContentResolver();
                    InputStream inputStream = null;
                    inputStream = cr.openInputStream(Uri.parse(FilePath));
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    Log.e("Server Receiver", e.getMessage());
                } catch (IOException e) {
                    Log.e("Server Receiver", e.getMessage());
                }

                return null;
            } else if (recv_or_send == RECEIVER) {
                //i am the receiver
                //start listening and wait for client to send

                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/ShareIt_test-" + System.currentTimeMillis()
                        + ".mp4");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                InputStream inputstream = client.getInputStream();
                // not handling copyfile stream failure
                copyFile(inputstream, new FileOutputStream(f));
                inputstream.close();
                serverSocket.close();
                return f.getAbsolutePath();
            }
        } catch(IOException e){
            Log.e("Server Receiver", e.getMessage());
            return null;
        }
        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (client != null) {
                if (client.isConnected()) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        Log.e("Server Sender", e.getMessage());
                    }
                }
            }
        }

        return null;
    }

}
