package com.example.android.contactsuploader;

/**
 * Created by Mesay on 1/4/2016.
 * registerUser() – Will store the user by passing name, email and password to php,mysql server.
 * db.addUser() – Will insert the user in SQLite database once he is successfully registered.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.contactsuploader.app.AppConfig;
import com.example.android.contactsuploader.app.AppController;
import com.example.android.contactsuploader.helper.SQLiteHandler;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacebookRegister extends AppCompatActivity {
    private static final String TAG = FacebookRegister.class.getSimpleName();
    CallbackManager callbackManager;
    private ProgressDialog progressDialog;
    User user;
    public String name;
    public String email;
    public String uid;
    private String okay;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        getLoginDetails(loginButton);
    }

    /*<
 Initialize the facebook sdk.<br />
 And then callback manager will handle the login responses.<br />
*/
    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }
    /*
      Register a callback function with LoginButton to respond to the login result.<br />
     */
    protected void getLoginDetails(LoginButton login_button){
        // Callback registration<br />
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                try {
                                    uid = object.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Application code
                                try {
                                    //name = object.getString("name").toString();
                                    name = response.getJSONObject().get("name").toString();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    email = object.getString("email");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (!name.isEmpty() && !email.isEmpty() && !uid.isEmpty()) {
                                    registerUser(name, email, uid);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Please Login with Facebook again!", Toast.LENGTH_LONG)
                                            .show();
                                }

                                okay = "ok";
                                Toast.makeText(FacebookRegister.this,"welcome "+name,Toast.LENGTH_LONG).show();
                                Intent returnIntent = new Intent();
                                //returnIntent.putExtra("result",result);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();



                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();




            }

            @Override
            public void onCancel() {
                // code for cancellation<br />
            }
            @Override
            public void onError(FacebookException exception) {
                //  code to handle error<br />
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data",data.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.<br />
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.<br />
        AppEventsLogger.deactivateApp(this);
    }


    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String muid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_date");

                        // Inserting row in users table
                        db.addUser(name, email, muid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
