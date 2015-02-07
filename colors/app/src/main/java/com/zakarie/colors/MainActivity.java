package com.zakarie.colors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    public final static String EXTRA_ADDRESS = "com.zakarie.colors.ADDRESS";
    public final static String EXTRA_PORT = "com.zakarie.colors.PORT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private boolean isAddressValid(String address) {
        return !address.isEmpty() && !address.contains(" ");
    }

    private boolean isPortValid(String port) {
        return !port.isEmpty();
    }

    public void startColors(View view) {
        EditText addressView = (EditText) findViewById(R.id.address);
        String address = addressView.getText().toString();
        if (!isAddressValid(address)) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText portView = (EditText) findViewById(R.id.port);
        String port = portView.getText().toString();
        if (!isPortValid(port)) {
            Toast.makeText(this, "Please enter a port", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), ColorsActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_PORT, Integer.parseInt(port));
        startActivity(intent);
    }

}
