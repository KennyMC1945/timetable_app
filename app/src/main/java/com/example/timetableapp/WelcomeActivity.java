package com.example.timetableapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private final int RC_REGISTRATION = 1532;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Button localSignIn = findViewById(R.id.btn_localSignIn);
        Button register = findViewById(R.id.btn_signUp);
        localSignIn.setOnClickListener(this);
        register.setOnClickListener(this);
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
                break;
            case R.id.btn_continueWithGoogle:
                googleSignIn();
                /* Вход через гугл */
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

    /* --- Пользовательские функции --- */
    public void googleSignIn(){
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
