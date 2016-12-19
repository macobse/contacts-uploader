package com.contacts.mesaynegash.contactsuploader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.contacts.mesaynegash.contactsuploader.activity.LoginActivity;
import com.contacts.mesaynegash.contactsuploader.helper.JsonParser;
import com.contacts.mesaynegash.contactsuploader.helper.SQLiteHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;


/**
 * Created by Mesay on 12/27/2015.
 */
public class RestoreContacts extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    JSONParser jParser;
    private static String url;
    static String contacts = "";
    public String user_id;
    private SQLiteHandler db;

    private static final String contacts_list = "contacts";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_contacts);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    // submitContacts method is executed when a backup button is pressed
    public void restoreContacts(View arg0) {
    /* Start an activity for result with request code 1
    *  LoginActivity is started to check whether the user is logged in or not
    *  if the user is logged in LoginActivity will send a result code to RestoreContacts
    */
        if (isNetworkAvailable()) {
            Intent intent = new Intent(RestoreContacts.this, LoginActivity.class);
            startActivityForResult(intent, 1);
        } else {
            makeText(RestoreContacts.this, "You need internet access to run this application",
                    LENGTH_LONG).show();
            Log.v("Home", "############################You are not online!!!!");

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //String result = data.getStringExtra("result");
                /**
                 * Checks if the device has Internet connection.
                 *
                 * @return <code>true</code> if the phone is connected to the Internet.
                 */
               //Logged in user ID
                user_id = data.getStringExtra("user_id");

                //&&isConnectedToServer("http://192.168.0.112:80", 10000) add to check the server status
                if (isNetworkAvailable()) {
                    WriteData task2 = new WriteData();
                    task2.execute();

                }

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RestoreContacts Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.contacts.mesaynegash.contactsuploader/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RestoreContacts Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.contacts.mesaynegash.contactsuploader/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class WriteData extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog dialog = new ProgressDialog(RestoreContacts.this);
        private ProgressBar progressBar;
        private String result;

        @Override
        protected void onPreExecute() {

            progressBar = (ProgressBar) findViewById(R.id.wrtProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            //  dialog.setMessage("Backing up your Data...");
            //dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            try {
                // Create a new HttpClient and Post Header
                JsonParser jParser = new JsonParser();
              //  HashMap<String, String> user = db.getUserDetails();
               //String uid = user.get("uid");//"57e9044e911629.90596579";


                url = "http://mesaynegash.com/cuapp/readjsonbyid.php?user_id="+user_id;



                // Getting JSON from URL

                JSONArray contacts = jParser.getJSONFromUrl(url);
                // Getting JSON Array from URL
                Log.d("JSON Parser", String.valueOf(contacts));

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                   // Storing JSON item in a Variable
                    String DisplayName  = c.getString("name");
                    String MobileNumber = c.getString("phone");

                    Log.v("Name", "############################"+DisplayName);
                        //here u can get all field like this


                        String HomeNumber = c.getString("others");
                        String WorkNumber = c.getString("contact_id");
                        String emailID = c.getString("email");
                        String company = c.getString("website");
                        String jobTitle = c.getString("address");

                        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.RawContacts.CONTENT_URI)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                .build());

                        //------------------------------------------------------ Names
                        if (DisplayName != null) {
                            ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(
                                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                            DisplayName).build());
                        }

                        //------------------------------------------------------ Mobile Number
                        if (MobileNumber != null) {
                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());
                        }

                        //------------------------------------------------------ Home Numbers
                        if (HomeNumber != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                    .build());
                        }

                        //------------------------------------------------------ Work Numbers
                        if (WorkNumber != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                    .build());
                        }

                        //------------------------------------------------------ Email
                        if (emailID != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                    .build());
                        }

                        //------------------------------------------------------ Organization
                        if (!company.equals("") && !jobTitle.equals("")) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                                    .build());
                        }

                        // Asking the Contact provider to create a new contact
                        try {
                            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RestoreContacts.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        int progress = ((i + 1) * 100) / contacts.length();
                        publishProgress(progress);
                        Log.v("Home", "************************Progress " + progress);
                    }
                }catch(Exception e){e.printStackTrace();}






            Toast.makeText(getApplicationContext(),
                    "Eyatina Logged In User ID:"+user_id, Toast.LENGTH_LONG)
                    .show();
        return true;


        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            TextView txt = (TextView) findViewById(R.id.txt1);
            txt.setText("Restoring percentage: " + progress[0] + "%");
            progressBar.setProgress(progress[0]);

        }

        protected void onPostExecute(Boolean result) {

            if (result == true) {
                progressBar.setVisibility(View.GONE);
                makeText(RestoreContacts.this, "All Contacts Successfully Restored!", LENGTH_LONG).show();

            } else {
                makeText(RestoreContacts.this, "Error: 01", LENGTH_LONG).show();
            }
            dialog.dismiss();
        }

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
