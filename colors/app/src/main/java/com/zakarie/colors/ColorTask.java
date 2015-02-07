package com.zakarie.colors;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ColorTask extends AsyncTask<Void, Command, String> {

    private Context context;
    private ColorsActivity activity;

    private String address;
    private int port;

    private CommandAdapter adapter;

    ColorTask(Context context, String address, int port, CommandAdapter adapter){
        this.context = context;
        this.activity = (ColorsActivity) context;
        this.address = address;
        this.port = port;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        activity.onConnect();
    }

    @Override
    protected String doInBackground(Void... params) {
        Socket socket;

        try {
            socket = new Socket(address, port);

            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];

            final int RELATIVE_MAX = 6;
            byte[] relative = new byte[RELATIVE_MAX];
            int relativeLength = 0;

            final int ABSOLUTE_MAX = 3;
            byte[] absolute = new byte[ABSOLUTE_MAX];
            int absoluteLength = 0;

            final int STATE_NONE = 0;
            final int STATE_RELATIVE = 1;
            final int STATE_ABSOLUTE = 2;
            int state = STATE_NONE;

            while (inputStream.read(buffer) != -1) {
                for (Byte b : buffer) {
                    if (isCancelled()) {
                        socket.close();
                        return null;
                    }

                    switch (state) {
                        case STATE_NONE:
                            if (b == 1) {
                                state = STATE_RELATIVE;
                            } else if (b == 2) {
                                state = STATE_ABSOLUTE;
                            }

                            break;
                        case STATE_RELATIVE:
                            relative[relativeLength++] = b;

                            if (relativeLength == RELATIVE_MAX) {
                                RelativeCommand rc = new RelativeCommand(relative);
                                publishProgress(rc);

                                relativeLength = 0;
                                state = STATE_NONE;
                            }

                            break;
                        case STATE_ABSOLUTE:
                            absolute[absoluteLength++] = b;

                            if (absoluteLength == ABSOLUTE_MAX) {
                                AbsoluteCommand ac = new AbsoluteCommand(absolute);
                                publishProgress(ac);

                                absoluteLength = 0;
                                state = STATE_NONE;
                            }

                            break;
                    }
                }
            }

            socket.close();
        } catch (UnknownHostException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Command... commands) {
        adapter.add(commands[0]);
    }

    @Override
    protected void onCancelled() {
        activity.onDisconnect();
    }

    @Override
    protected void onPostExecute(String message) {
        activity.onDisconnect();

        if (message != null) {
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}
