package com.contacts.mesaynegash.contactsuploader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.contacts.mesaynegash.contactsuploader.activity.LoginActivity;
import com.contacts.mesaynegash.contactsuploader.helper.SQLiteHandler;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.contacts.mesaynegash.contactsuploader.R.layout;

public class AllContacts extends Activity {
    // To get the logged in user id from sqlite databse
    private AdView mAdView;
    private SQLiteHandler db;
    int  i =0;
    InsertData task1;
    Cursor phones, email;
    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.contact_list_item);



        TextView txt =  (TextView) findViewById(R.id.text1);
        //Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

        final String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        phones  =  getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, sortOrder);

        txt.setText("Total: " + phones.getCount() + " contacts");
        //txt.setTextColor(255);

        phones.moveToFirst();
        // Iterate every contact in the phone
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Your code to show add
                mAdView = (AdView) findViewById(R.id.adView);
                mAdView.setAdListener(new ToastAdListener(AllContacts.this));
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdView.loadAd(adRequest);
            }
        }, 1000);


    }



    // submitContacts method is executed when a backup button is pressed
    public void submitContacts(View arg0) {
    /* Start an activity for result with request code 1
    *  LoginActivity is started to check whether the user is logged in or not
    *  if the user is logged in LoginActivity will send a result code to AllContacts activity
    */
        if(isNetworkAvailable()){
            Intent intent=new Intent(AllContacts.this,LoginActivity.class);
            startActivityForResult(intent, 1);
        }
        else{
            makeText(AllContacts.this, "You need internet access to run this application",
                    LENGTH_LONG).show();


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
                if(isNetworkAvailable()){
                    task1 = new InsertData();
                    task1.execute(new String[]{"http://www.mesaynegash.com/cuapp/insert.php"});
                    //Intent in = new Intent(this, AllContacts.class); // if not working use new Intent(this, MainActivity.class)
                    //startActivity(in);
                }

                else{
                    makeText(AllContacts.this, "You need internet access to run this application",
                            LENGTH_LONG).show();



                }




            }
        }
    }
    private class InsertData extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog dialog = new ProgressDialog(AllContacts.this);
        private ProgressBar progressBar;
        private String result;

        @Override
        protected void onPreExecute() {

            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            LinearLayout btnLinear1 = (LinearLayout)findViewById(R.id.image_button_2);
            btnLinear1.setVisibility(View.INVISIBLE);
            TextView txt = (TextView)findViewById(R.id.txt1);
            txt.setText("Press the Back button to cancel...");
            txt.setVisibility(View.VISIBLE);

            //btnLinear2.setOnClickListener();
            progressBar.setVisibility(View.VISIBLE);
            //  dialog.setMessage("Backing up your Data...");
            //dialog.show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {

                // Logout button click event

                db = new SQLiteHandler(getApplicationContext());
                if (phones != null && phones.moveToFirst()) {
                    int total = phones.getCount();



                    for (String url1 : urls) {
                        int myCount = phones.getCount();
                        myCount = myCount;
                        for (; i < phones.getCount(); i++) {
                            try {
                                HashMap<String, String> user = db.getUserDetails();
                                String total_contact = String.valueOf(phones.getCount());
                                String user_id = user.get("uid");
                                pairs.add(new BasicNameValuePair("userId", user_id));
                                pairs.add(new BasicNameValuePair("txtName", phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))));
                                pairs.add(new BasicNameValuePair("txtTel", phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                                pairs.add(new BasicNameValuePair("contId", phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))));
                                pairs.add(new BasicNameValuePair("totalContact", total_contact));


                                HttpClient client = new DefaultHttpClient();
                                HttpPost post = new HttpPost(url1);

                                post.setEntity(new UrlEncodedFormEntity(pairs));

                                HttpResponse response = client.execute(post);
                                HttpEntity resEntity = response.getEntity();

                                if (resEntity != null) {

                                    String responseStr = EntityUtils.toString(resEntity).trim();

                                    pairs.clear();

                                    // you can add an if statement here and do other actions based on the response
                                }


                            } catch (ClientProtocolException e) {
                                makeText(AllContacts.this, e.toString(), LENGTH_LONG).show();
                                return false;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            phones.moveToNext();


                            int progress = ((i + 1) * 100) / total;
                            publishProgress(progress);




                        }
                    }
                }

            return true;

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            TextView txt =  (TextView) findViewById(R.id.text1);
            txt.setText("Backup percentage: " + progress[0]+"%");
            progressBar.setProgress(progress[0]);

        }

        protected void onPostExecute (Boolean result){

            if (result == true) {
                progressBar.setVisibility(View.GONE);
                TextView txt = (TextView)findViewById(R.id.txt1);
                txt.setVisibility(View.INVISIBLE);
                LinearLayout btnLinear2 = (LinearLayout)findViewById(R.id.image_ok);
                btnLinear2.setVisibility(View.VISIBLE);

                makeText(AllContacts.this, "Backup Successful!", LENGTH_LONG).show();

            } else {
                makeText(AllContacts.this, "Error", LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "asynctack cancelled.....", Toast.LENGTH_SHORT).show();
            dialog.hide(); /*hide the progressbar dialog here...*/
            super.onCancelled();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
 //Check connection to server URL.
    public boolean isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            makeText(AllContacts.this, "Unable to access the Server!",
                    LENGTH_LONG).show();
            return false;
        }
    }


// start home activity

    public void home(View arg0){
        Intent intent = new Intent(AllContacts.this,MainActivity.class);
        startActivity(intent);
    }


}
