package com.example.timetableapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AccountInfoActivity extends Activity implements View.OnClickListener {

    private Intent parent;
    private final int RC_LOCAL_REG_ACC_INFO = 1153;
    private final int RC_GOOGLE_INFO = 1252;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Button complete = findViewById(R.id.btn_completeRegistration);
        parent = getIntent();
        int reqCode = parent.getIntExtra("reqCode",0);
        updateUI(reqCode);
        complete.setOnClickListener(this);
    }

    public void updateUI(int reqCode){
        TextView tv_title = findViewById(R.id.tv_regTitle);
        EditText et_firstName = findViewById(R.id.et_firstName);
        EditText et_lastName = findViewById(R.id.et_secondName);

        switch (reqCode){
            case RC_GOOGLE_INFO:
                tv_title.setText("Уточнение данных");
                String[] name = (parent.hasExtra("name"))?parent.getStringExtra("name").split(" "):"Default Name".split(" ");
                et_firstName.setText(name[0]);
                et_lastName.setText(name[1]);
                break;
            case RC_LOCAL_REG_ACC_INFO:
                tv_title.setText("Регистрация");
                et_firstName.setText("");
                et_lastName.setText("");
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_completeRegistration:
                EditText name = findViewById(R.id.et_firstName);
                EditText surname = findViewById(R.id.et_secondName);
                RadioGroup radioGroup = findViewById(R.id.acc_info_rg_week);
                int btn_id = radioGroup.getCheckedRadioButtonId();
                int today_week =  TimeUtils.getWeek();
                int top_week =(btn_id == R.id.acc_info_rb_topWeek)?today_week:today_week-1;
                Intent info = new Intent();
                info.putExtra("group",((EditText)findViewById(R.id.et_registrationGroup)).getText().toString());
                info.putExtra("name",name.getText().toString()+" "+ surname.getText().toString());
                info.putExtra("top_week",top_week);
                setResult(RESULT_OK,info);
                finish();
                break;
        }
    }
}
