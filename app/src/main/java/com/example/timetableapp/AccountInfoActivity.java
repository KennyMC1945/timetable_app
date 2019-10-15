package com.example.timetableapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AccountInfoActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Button complete = findViewById(R.id.btn_completeRegistration);
        complete.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_completeRegistration:
                EditText name = findViewById(R.id.et_firstName);
                EditText surname = findViewById(R.id.et_secondName);
                Intent info = new Intent();
                info.putExtra("group",((EditText)findViewById(R.id.et_registrationGroup)).getText().toString());
                info.putExtra("name",name.getText().toString()+" "+ surname.getText().toString());
                setResult(RESULT_OK,info);
                finish();
                break;
        }
    }
}
