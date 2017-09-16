package com.example.jonathan.photoconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

public class RegisterActivity extends AppCompatActivity {

    EditText full_name_box = null;
    EditText email_box = null;
    EditText username_box = null;
    EditText password_box = null;

    GoogleApiClient mGoogleApiClient;

    String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        full_name_box = (EditText) findViewById(R.id.first_name_box);
        email_box = (EditText) findViewById(R.id.email_box);
        username_box = (EditText) findViewById(R.id.reg_username_box);
        password_box = (EditText)findViewById(R.id.password_box);

        String fbid = null;

        Button register_button = (Button) findViewById(R.id.register_confirmed);

        if (getIntent() != null){
            String full_name = getIntent().getStringExtra("full_name");
            String email = getIntent().getStringExtra("email");
            pic = getIntent().getStringExtra("pic");
            full_name_box.setText(full_name);
            email_box.setText(email);
            fbid = getIntent().getStringExtra("id");
        }

        final String finalFbid = fbid;
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attemptLogin()) {
                    final String full_name = full_name_box.getText().toString();
                    final String email = email_box.getText().toString();
                    final String username = username_box.getText().toString();
                    String password = password_box.getText().toString();

                    try {
                        password = PasswordEncryption.generateStrongPasswordHash(password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }

                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("RegisterActivity", response);
                                JSONObject jsonResponse = new JSONObject(response);

                                int success = jsonResponse.getInt("success");

                                switch (success) {
                                    case 1:
                                        Intent intent = new Intent(RegisterActivity.this, UserAreaActivity.class);
                                        intent.putExtra("full_name", full_name);
                                        intent.putExtra("username", username);
                                        intent.putExtra("email", email);
                                        intent.putExtra("pic",pic);
                                        startActivity(intent);
                                        break;

                                    case -1:
                                        username_box.setError("Username already taken");
                                        username_box.requestFocus();
                                        Log.d("Hello","hello");
                                        break;

                                    default:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("Network Error.").setNegativeButton("Retry", null).create().show();
                                        break;

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    RegisterRequest registerRequest = new RegisterRequest(full_name, email, username, password, finalFbid, pic, responseListener);
                    RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                    requestQueue.add(registerRequest);
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        super.onBackPressed();
    }

    @Override
    protected void onStop(){
        LoginManager.getInstance().logOut();
        super.onStop();
    }

    public boolean attemptLogin(){
        String full_name = full_name_box.getText().toString();
        String email = email_box.getText().toString();
        String username = username_box.getText().toString();
        String password = password_box.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(full_name)){
            full_name_box.setError("This field is required.");
            focusView = full_name_box;
            cancel = true;
        }
        else if (TextUtils.isEmpty(email)){
            email_box.setError("This field is required");
            focusView = email_box;
            cancel = true;
        }
        else if (TextUtils.isEmpty(username)){
            username_box.setError("This field is required.");
            focusView = username_box;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password)){
            password_box.setError("This field is required");
            focusView = password_box;
            cancel = true;
        }

        else if (!email.contains("@")){
            email_box.setError("Invalid email address.");
            focusView = email_box;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
            return false;
        }
        else return true;
    }
}
