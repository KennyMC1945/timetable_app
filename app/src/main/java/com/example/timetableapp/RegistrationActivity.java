package com.example.timetableapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.List;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    private final int RC_LOCAL_REG_ACC_INFO = 1153;
    private ConnectionManager cm;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        Button next = findViewById(R.id.btn_continueRegistration);
        Button cancel = findViewById(R.id.btn_cancelRegistration);
        next.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continueRegistration:
                /* Проверка правильности в соотв с правилами и переход к следующим полям */
                EditText pass = findViewById(R.id.et_entryPassword);
                EditText repeatPass = findViewById(R.id.et_repeatPassword);
                if (pass.getText().toString().matches(repeatPass.getText().toString())) {
                    Intent acc_info = new Intent(RegistrationActivity.this, AccountInfoActivity.class);
                    startActivityForResult(acc_info, RC_LOCAL_REG_ACC_INFO);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Пароли не совпадают", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_cancelRegistration:
                /* Диалог отмены регистрации */
                cancelDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCAL_REG_ACC_INFO && resultCode == RESULT_OK){
            EditText email = findViewById(R.id.et_mail);
            EditText pass = findViewById(R.id.et_entryPassword);
            try {
                JSONObject params = new JSONObject();
                params.put("mail", email.getText().toString());
                params.put("pass", pass.getText().toString());
                params.put("group", data.getStringExtra("group"));
                params.put("name", data.getStringExtra("name"));
                cm.postJSONRequest(getString(R.string.local_register_url), params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 200) {
                                Toast.makeText(getApplicationContext(), response.getString("token"), Toast.LENGTH_SHORT).show();
                            } else if (status == 400) {
                                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {}
                    }
                });
            } catch (Exception e) {}


        }
    }

    @Override
    public void onBackPressed(){
        cancelDialog();
    }

    private void cancelDialog(){
        AlertDialog.Builder cancel = new AlertDialog.Builder(RegistrationActivity.this);
        cancel.setTitle("Отмена регистрации");
        cancel.setPositiveButton("Да", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        cancel.setMessage("Вы хотите выйти из регистрации?");
        cancel.setNeutralButton("Нет", null);
        cancel.show();
    }
}
