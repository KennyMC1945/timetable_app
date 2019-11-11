package com.example.timetableapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginDialogActivity extends Activity implements View.OnClickListener {
    private ConnectionManager cm;
    private SharedPreferences appSharedPrefs;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        appSharedPrefs = getSharedPreferences(getString(R.string.conf_file),MODE_PRIVATE);
        /* Задаем размеры окна */
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        int heigth = Math.round(dm.heightPixels / dm.density);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.x = 0;
        layoutParams.width = widthInDP*2;
        this.getWindow().setAttributes(layoutParams);
        findViewById(R.id.dialog_login_btn_signIn).setOnClickListener(this);
        findViewById(R.id.dialog_login_btn_cancel).setOnClickListener(this);

    }

    public void login() {
        EditText email = findViewById(R.id.dialog_login_et_email);
        EditText pass = findViewById(R.id.dialog_login_et_pass);
        Intent loginData = new Intent();
        loginData.putExtra("email",email.getText().toString());
        loginData.putExtra("pass",pass.getText().toString());
        setResult(RESULT_OK,loginData);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_login_btn_signIn:
                login();
                break;
            case R.id.dialog_login_btn_cancel:
                finish();
                break;
        }
    }
}
