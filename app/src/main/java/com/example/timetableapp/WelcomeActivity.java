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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Response;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener, AuthListener {

    private final int RC_REGISTRATION = 1152;
    private final int RC_LOGIN        = 1151;
    private final int RC_GOOGLE_AUTH  = 1251;
    private final int RC_GOOGLE_INFO  = 1252;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private ConnectionManager cm;
    private FirebaseAuth mAuth;
    private AuthorizationManager authManager;
    private Intent fbAuthData = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_client_id))
                .requestEmail()
                .requestServerAuthCode(getString(R.string.firebase_client_id),false)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        authManager = new AuthorizationManager(getApplicationContext());
        authManager.subscribe(this);
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        setContentView(R.layout.activity_welcome_screen);
        // Задаем известную инфу, если редактируем для локального пользователя
        updateUI(getIntent().getBooleanExtra("edit",false));
        // Привязываем слушателя для кнопок
        findViewById(R.id.btn_localSignIn).setOnClickListener(this);
        findViewById(R.id.btn_signUp).setOnClickListener(this);
        findViewById(R.id.btn_signIn).setOnClickListener(this);
        findViewById(R.id.btn_continueWithGoogle).setOnClickListener(this);
    }

    private void updateUI(boolean isEdit){
        if (isEdit){
            // Выводим название группы
            EditText groupName = findViewById(R.id.et_startGroup);
            groupName.setText(getSharedPreferences("user_info", MODE_PRIVATE).getString("group", ""));
            // Выбираем какая сейчас неделя
            RadioGroup week_now = findViewById(R.id.rg_week);
            int top_week = getSharedPreferences("user_info",MODE_PRIVATE).getInt("top_week",0);
            if ((TimeUtils.getWeek()-top_week)%2 == 0) week_now.check(R.id.rb_topWeek);
            else week_now.check(R.id.rb_bottomWeek);
        }
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
                RadioGroup radioGroup = findViewById(R.id.rg_week);
                int btn_id = radioGroup.getCheckedRadioButtonId();
                int today_week =  TimeUtils.getWeek();
                int top_week =(btn_id == R.id.rb_topWeek)?today_week:today_week-1;
                Intent intent = new Intent();
                intent.putExtra("no-auth",true);
                intent.putExtra("top_week",top_week);
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
                Intent loginDialog = new Intent(WelcomeActivity.this,LoginDialogActivity.class);
                startActivityForResult(loginDialog,RC_LOGIN);
                break;
            case R.id.btn_continueWithGoogle:
                /* Вход через гугл */
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE_AUTH);
                break;
        }
    }



    @Override
    public void onBackPressed(){
        if (!getIntent().getBooleanExtra("edit",false)) {
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(WelcomeActivity.this);
            quitDialog.setTitle("Выйти из программы");
            quitDialog.setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            quitDialog.setMessage("Вы уверены, что хотите выйти?");
            quitDialog.setNeutralButton("Отмена", null);
            quitDialog.show();
        }
        else super.onBackPressed();
    }

    @Override
    public void onActivityResult(int reqCode, final int resCode, Intent data){
        super.onActivityResult(reqCode,resCode,data);

        if (reqCode >= 1200) { // Если с гуглом
            if (reqCode == RC_GOOGLE_AUTH) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount acc = task.getResult(ApiException.class);
                    fbAuthData.putExtra("gIDToken", acc.getIdToken());
                    fbAuthData.putExtra("login",true);
                    authManager.authWithGoogle(fbAuthData);
                } catch (ApiException e) {

                }
            } else if (reqCode == RC_GOOGLE_INFO && resCode == RESULT_OK) {
                fbAuthData.putExtra("group", data.getStringExtra("group"));
                fbAuthData.putExtra("top_week", data.getIntExtra("top_week", 0));
                fbAuthData.putExtra("name", data.getStringExtra("name"));
                authManager.authWithGoogle(fbAuthData);
            }
        } else { // Если локальная регистрация/логин
            if (reqCode == RC_REGISTRATION && resCode == RESULT_OK) {
                fbAuthData.putExtras(data);
                fbAuthData.putExtra("group",data.getStringExtra("group"));
                fbAuthData.putExtra("top_week",data.getIntExtra("top_week",0));
                fbAuthData.putExtra("name",data.getStringExtra("name"));
                authManager.registerWithEmail(fbAuthData);
            } else if (reqCode == RC_LOGIN && resCode == RESULT_OK){
                authManager.authWithEmail(data);
            }
        }
    }

    @Override public void onDestroy(){
        authManager.unsubscribe(this);
        super.onDestroy();
    }

    @Override
    public void update(int resCode) {
        // TODO: Обработка регистраций
        boolean authenticated = getApplicationContext().getSharedPreferences("user_info",MODE_PRIVATE).getBoolean("authenticated",false);
        if (authenticated && resCode == 0){
                setResult(RESULT_OK,new Intent());
                finish();
        } else if (resCode == 1050) {
            fbAuthData.putExtra("login",false);
            Intent continueRegistration = new Intent(WelcomeActivity.this, AccountInfoActivity.class);
            continueRegistration.putExtra("reqCode", RC_GOOGLE_INFO);
            startActivityForResult(continueRegistration, RC_GOOGLE_INFO);
        }
    }

}
