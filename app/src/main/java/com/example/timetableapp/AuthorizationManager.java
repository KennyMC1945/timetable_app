package com.example.timetableapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthorizationManager {
    private final String regURL = "/auth/firebase/register";
    private final String loginURL = "/auth/firebase/login";
    private ConnectionManager cm;
    private Context appContext;
    private FirebaseAuth mAuth;
    private ArrayList<AuthListener> subscribers = new ArrayList<>();
    private int loginType; // 100 - Local; 200 - Google
    private Response.Listener<JSONObject> serverListener;

    public AuthorizationManager(Context _appContext){
        this.appContext = _appContext;
        this.mAuth = FirebaseAuth.getInstance();
        this.cm = new ConnectionManager(appContext.getResources().getString(R.string.server_url),appContext);
        this.serverListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO: Обработка ответа на регистрацию
                try {
                    int status = response.getInt("status");
                    SharedPreferences.Editor editor = appContext.getSharedPreferences("user_info", Context.MODE_PRIVATE).edit();
                    if (status == 200) {
                        editor.putString("token", response.getString("token"));
                        editor.putString("name", response.getString("name"));
                        editor.putString("group", response.getString("group"));
                        editor.putInt("top_week", response.getInt("top_week"));
                        editor.putBoolean("authenticated", true);
                        editor.apply();
                        notifySubs(0);
                    } else if (status == 400) {
                        editor.putBoolean("authenticated", false);
                        editor.apply();
                        Toast.makeText(appContext, response.getString("msg"), Toast.LENGTH_SHORT);
                        if (loginType == 200) {
                            notifySubs(1050);
                        } else {
                            notifySubs(0);
                        }
                    }

                } catch (Exception e) { }
            }
        };
    }

    public void registerWithEmail(final Intent data){
        loginType = 0;
        mAuth.createUserWithEmailAndPassword(data.getStringExtra("email"),data.getStringExtra("pass")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    data.putExtra("fb_uid",user.getUid());
                    registerOnServer(data);
                }
                else {
                    Toast.makeText(appContext,"Some kind of error ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void authWithEmail(final Intent data){
        loginType = 100;
        mAuth.signInWithEmailAndPassword(data.getStringExtra("email"),data.getStringExtra("pass")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    data.putExtra("fb_uid",user.getUid());
                    loginOnServer(data);
                }
                else {
                    Toast.makeText(appContext,"Some kind of error ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void authWithGoogle(final Intent data){
        AuthCredential cred = GoogleAuthProvider.getCredential(data.getStringExtra("gIDToken"),null);
        mAuth.signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    data.putExtra("fb_uid",user.getUid());
                    if (data.getBooleanExtra("login",false)){ loginType = 200; loginOnServer(data);}
                    else { loginType =0; registerOnServer(data); }
                }
                else {
                    Toast.makeText(appContext,"Some kind of error ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerOnServer(Intent data){
        JSONObject body = new JSONObject();
        try {
            body.put("fb_uid", data.getStringExtra("fb_uid"));
            body.put("name", data.getStringExtra("name"));
            body.put("group", data.getStringExtra("group"));
            body.put("top_week", data.getIntExtra("top_week", 0));
            cm.postJSONRequest(regURL, body, serverListener);
        } catch (Exception e) {}
    }

    private void loginOnServer(Intent data){
        JSONObject body = new JSONObject();
        try {
            body.put("fb_uid", data.getStringExtra("fb_uid"));
            cm.postJSONRequest(loginURL, body, serverListener);
        } catch (Exception e){}
    }

    private void notifySubs(int resCode){
        for (AuthListener sub: subscribers){
            sub.update(resCode);
        }
    }


    public void subscribe(AuthListener listener){
        subscribers.add(listener);
    }

    public void unsubscribe(AuthListener listener){
        subscribers.remove(listener);
    }
}
