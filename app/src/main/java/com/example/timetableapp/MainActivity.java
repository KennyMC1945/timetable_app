package com.example.timetableapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* constants */
    private final int RC_SIGN_IN = 3575;
    private final String GOOGLE_AUTH_URL = "/auth/google/verify";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private TextView tv_gDockText;

    private void sendVerifyRequest(final String idToken) throws IOException,JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject params = new JSONObject();
        params.put("token",idToken);
        VerifyRequest verifyRequest = new VerifyRequest(Request.Method.POST, getString(R.string.server_url) + GOOGLE_AUTH_URL,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int status = Integer.parseInt(response.split(":")[0]);
                        String resText = response.split(":")[1];
                        switch (status) {
                            case 200:
                                tv_gDockText.setText(resText);
                                break;
                            case 400:
                                Toast.makeText(getApplicationContext(),resText, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            );
        queue.add(verifyRequest);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        TextView resultLabel = findViewById(R.id.tv_gAuthResult);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount acc = result.getSignInAccount();
                String idToken = acc.getIdToken();
                try {
                    sendVerifyRequest(idToken);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                }
                resultLabel.setText("Success");
            }
            else {
                resultLabel.setText(result.getStatus().toString());
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .requestServerAuthCode(getString(R.string.client_id),false)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        tv_gDockText = findViewById(R.id.tv_dockText);
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("u");
        int weekday = Integer.parseInt(sdf.format(today));
        findViewById(R.id.googleAuth).setOnClickListener(this);
        findViewById(R.id.googleSignOut).setOnClickListener(this);
    }


    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        final TextView result = findViewById(R.id.tv_gAuthResult);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        result.setText("Successful sign out!");
                        tv_gDockText.setText(getString(R.string.default_name));
                    }
                });
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.googleAuth:
                signIn();
                break;
            case R.id.googleSignOut:
                signOut();
                break;
        }
    }
}
