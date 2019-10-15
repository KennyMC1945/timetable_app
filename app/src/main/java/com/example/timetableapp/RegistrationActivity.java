package com.example.timetableapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
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
                Intent acc_info = new Intent(RegistrationActivity.this,AccountInfoActivity.class);
                startActivityForResult(acc_info,1552);
                break;
            case R.id.btn_cancelRegistration:
                /* Диалог отмены регистрации */
                cancelDialog();
                break;
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
