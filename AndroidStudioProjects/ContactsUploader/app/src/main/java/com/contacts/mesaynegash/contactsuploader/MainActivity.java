package com.contacts.mesaynegash.contactsuploader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void  selectContacts(View view){
        Intent myIntent = new Intent(MainActivity.this, AllContacts.class);
       // myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }
    // Starts restore contacts activity
    public void restoreContacts(View view){
        Intent restore = new Intent(MainActivity.this, RestoreContacts.class);
        MainActivity.this.startActivity(restore);
    }

    // Starts Expoty contacts activity
    public void exportContacts(View view){
        Intent restore = new Intent(MainActivity.this, ExportContacts.class);
        MainActivity.this.startActivity(restore);
    }

    // Starts restore contacts activity
    public void checkStatus(View view){
        Intent statu = new Intent(MainActivity.this, checkStatus.class);
        MainActivity.this.startActivity(statu);
    }
}
