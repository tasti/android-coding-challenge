package com.zakarie.colors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ColorsActivity extends Activity {

    private TextView status;

    private CommandAdapter adapter;

    private ColorTask colorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors);

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        int port = intent.getIntExtra(MainActivity.EXTRA_PORT, 1234);

        // Display host and connection status
        TextView addressPort = (TextView) findViewById(R.id.address_port);
        addressPort.setText(address + ":" + port);
        status = (TextView) findViewById(R.id.status);

        View colorBox = findViewById(R.id.color_box);
        TextView redLabel = (TextView) findViewById(R.id.red_value);
        TextView greenLabel = (TextView) findViewById(R.id.green_value);
        TextView blueLabel = (TextView) findViewById(R.id.blue_value);

        ColorSingleton.createInstance(colorBox, redLabel, greenLabel, blueLabel);

        adapter = new CommandAdapter(this, R.layout.listview_command, new ArrayList<Command>());
        ListView commands = (ListView) findViewById(R.id.commands);
        commands.setOnItemClickListener(commandsOnItemClickListener);
        commands.setAdapter(adapter);

        colorTask = new ColorTask(this, address, port, adapter);
        colorTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    public void onConnect() {
        status.setText("Connected");
        status.setTextColor(getResources().getColor(android.R.color.holo_green_light));
    }

    public void onDisconnect() {
        status.setText("Disconnected");
        status.setTextColor(getResources().getColor(android.R.color.holo_red_light));
    }

    private AdapterView.OnItemClickListener commandsOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.toggleSelection(position);
        }
    };

}
