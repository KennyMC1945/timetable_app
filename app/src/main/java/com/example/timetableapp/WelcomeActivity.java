package com.example.timetableapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Response;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener {

    private final int RC_REGISTRATION = 1152;
    private final int RC_GOOGLE_VERIFY = 1251;
    private final String GOOGLE_AUTH_URL = "/auth/google/verify";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private ConnectionManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .requestServerAuthCode(getString(R.string.client_id),false)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        /* TODO:
        *   Получение нового токена, ибо старый быстро устаревает */
        //GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        setContentView(R.layout.activity_welcome_screen);
        Button localSignIn = findViewById(R.id.btn_localSignIn);
        Button register = findViewById(R.id.btn_signUp);
        localSignIn.setOnClickListener(this);
        register.setOnClickListener(this);
        findViewById(R.id.btn_signIn).setOnClickListener(this);
        findViewById(R.id.btn_continueWithGoogle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_localSignIn:
                /* Локальный вход без регистрации*/
                EditText group_name = findViewById(R.id.et_startGroup);
                if (group_name.getText().toString().matches("") ){
                    group_name.setHintTextColor(Color.RED);
                    break;
                }
                Intent intent = new Intent();
                intent.putExtra("group",group_name.getText().toString());
                setResult(RESULT_OK,intent);
                finish();

                break;
            case R.id.btn_signUp:
                /* Переход к регистрации */
                Intent register = new Intent(WelcomeActivity.this, RegistrationActivity.class);
                startActivityForResult(register,RC_REGISTRATION);
                break;
            case R.id.btn_signIn:
                /* Вход через логин/пароль */
                loginDialog();
                break;
            case R.id.btn_continueWithGoogle:
                /* Вход через гугл */
                googleSignIn();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(WelcomeActivity.this);
        quitDialog.setTitle("Выйти из программы");
        quitDialog.setPositiveButton("Выйти", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
        });
        quitDialog.setMessage("Вы уверены, что хотите выйти?");
        quitDialog.setNeutralButton("Отмена", null);
        quitDialog.show();
    }

    @Override
    public void onActivityResult(int reqCode, final int resCode, Intent data){
        super.onActivityResult(reqCode,resCode,data);
        /* TODO:
        *   Обработка токена
        *   Запись токена и другой инфы в shared prefs*/

        if (reqCode == RC_GOOGLE_VERIFY) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount acc = result.getSignInAccount();
                try{
                    sendToken(acc);
                } catch (Exception e) { Log.w("JSONException",e.getStackTrace().toString()); }
            }
            else {
            }
        }
    }

    /* --- Пользовательские функции --- */
    public void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_VERIFY);
    }

    public void loginDialog(){
        Intent loginDialog = new Intent(WelcomeActivity.this,LoginDialogActivity.class);
        startActivityForResult(loginDialog,214);
//        DialogFragment login = new LoginDialogFragment();
//        login.show(getSupportFragmentManager(),"Login");

    }

    public void sendToken(GoogleSignInAccount acc) throws JSONException{
        String idToken = acc.getIdToken();
        JSONObject reqObj = new JSONObject();
        reqObj.put("token", idToken);
        cm.postJSONRequest(GOOGLE_AUTH_URL, reqObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        Intent data = new Intent();
                        data.putExtra("google",true);
                        data.putExtra("name", response.getString("username"));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                } catch (Exception e) { Log.w("JSONException",e.getStackTrace().toString()); }
            }
        });
    }
}
