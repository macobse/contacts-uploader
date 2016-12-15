package com.example.android.contactsuploader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.contactsuploader.activity.LoginActivity;
import com.example.android.contactsuploader.helper.SQLiteHandler;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class checkStatus extends AppCompatActivity {
    Cursor phones;
    private SQLiteHandler db;
    String error = ""; // string field
    public String user_id;
    ProgressDialog progress;

    private Request request;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);
        TextView txt = (TextView) findViewById(R.id.txxt1);

        final String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, sortOrder);


        txt.setText("Phone Contacts:         " + phones.getCount());


        if(isNetworkAvailable())
        {
            Intent intent=new Intent(checkStatus.this,LoginActivity.class);
            startActivityForResult(intent, 1);
        }


        else

        {
            makeText(checkStatus.this, "You need internet access to run this application",
                    LENGTH_LONG).show();

            Log.v("Home", "############################You are not online!!!!");

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(requestCode==1) {
            if (resultCode == Activity.RESULT_OK) {
                //String result = data.getStringExtra("result");
                /**
                 * Checks if the device has Internet connection.
                 *
                 * @return <code>true</code> if the phone is connected to the Internet.
                 */

                //&&isConnectedToServer("http://192.168.0.112:80", 10000) add to check the server status
                if(isNetworkAvailable())
                {
                    user_id = data.getStringExtra("user_id");
                    url = "http://mesaynegash.com/cuapp/getContacts.php?user_id="+user_id;
                    new CallAPI().execute();
                }


                else

                {
                    makeText(checkStatus.this, "You need internet access to run this application",
                            LENGTH_LONG).show();

                    Log.v("Home", "############################You are not online!!!!");

                }




            }
        }
    }



 // Retrieve total number of contacts stored in server

    public class CallAPI extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(checkStatus.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();


                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    return result;



            } catch (IOException e) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Update the UI
            super.onPostExecute(result);
            progress.dismiss();
            TextView txt2 = (TextView) findViewById(R.id.txt2);
            txt2.setText("Uploaded Contacts:    " + result);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
