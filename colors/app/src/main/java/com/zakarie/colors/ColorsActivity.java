package com.zakarie.colors;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ColorsActivity extends Activity {
    private TextView addressPort;
    private TextView status;
    private final String STATUS_CONNECTED = "Connected";
    private final String STATUS_DISCONNECTED = "Disconnected";

    private View colorBox;
    private TextView redLabel;
    private TextView greenLabel;
    private TextView blueLabel;

    private CommandAdapter adapter;
    private ListView commands;

    private ColorTask colorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors);

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        int port = intent.getIntExtra(MainActivity.EXTRA_PORT, 1234);

        // Display host and status
        addressPort = (TextView) findViewById(R.id.address_port);
        addressPort.setText(address + ":" + port);
        status = (TextView) findViewById(R.id.status);

        colorBox = findViewById(R.id.color_box);
        redLabel = (TextView) findViewById(R.id.red_value);
        greenLabel = (TextView) findViewById(R.id.green_value);
        blueLabel = (TextView) findViewById(R.id.blue_value);

        ColorSingleton.createInstance(colorBox, redLabel, greenLabel, blueLabel);

        adapter = new CommandAdapter(this, R.layout.listview_command, new ArrayList<Command>());
        commands = (ListView) findViewById(R.id.commands);
        commands.setOnItemClickListener(commandsOnItemClickListener);
        commands.setAdapter(adapter);

        colorTask = new ColorTask(address, port);
        colorTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("PAUSE", "1");
        colorTask.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_colors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void onConnect() {
        status.setText(STATUS_CONNECTED);
        status.setTextColor(getResources().getColor(android.R.color.holo_green_light));
    }

    private void onDisconnect() {
        status.setText(STATUS_DISCONNECTED);
        status.setTextColor(getResources().getColor(android.R.color.holo_red_light));
    }

    private AdapterView.OnItemClickListener commandsOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.toggleSelection(position);
        }
    };

    public class ColorTask extends AsyncTask<Void, Command, String> {

        private String address;
        private int port;

        ColorTask(String address, int port){
            this.address = address;
            this.port = port;
        }

        @Override
        protected void onPreExecute() {
            onConnect();
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
            onDisconnect();
        }

        @Override
        protected void onPostExecute(String message) {
            onDisconnect();

            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
