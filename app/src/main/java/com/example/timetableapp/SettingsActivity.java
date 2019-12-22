package com.example.timetableapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class SettingsActivity extends Activity implements View.OnClickListener {

    private final String EDIT_USER_URL = "/user/edit";
    private String preEditName;
    private String preEditGroup;
    private int preEditWeek;
    private ConnectionManager cm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        updateUI(getSharedPreferences("user_info",MODE_PRIVATE));
    }

    private void updateUI(SharedPreferences info){
        EditText name = findViewById(R.id.et_setName);
        EditText group = findViewById(R.id.et_setGroup);
        RadioGroup week = findViewById(R.id.rg_setWeek);
        preEditName = info.getString("name","");
        preEditGroup = info.getString("group","");
        name.setText(preEditName);
        group.setText(preEditGroup);
        int top_week = getSharedPreferences("user_info",MODE_PRIVATE).getInt("top_week",0);
        if ((TimeUtils.getWeek()-top_week)%2 == 0) preEditWeek = R.id.rb_setTopWeek;
        else preEditWeek = R.id.rb_setBottomWeek;
        week.check(preEditWeek);
        findViewById(R.id.btn_settings_apply).setOnClickListener(this);
        findViewById(R.id.btn_settings_cancel).setOnClickListener(this);
    }

    private boolean isChanged(){
        EditText name = findViewById(R.id.et_setName);
        EditText group = findViewById(R.id.et_setGroup);
        RadioGroup week = findViewById(R.id.rg_setWeek);
        if (preEditWeek != week.getCheckedRadioButtonId()) return true;
        if (!preEditGroup.equals(group.getText().toString())) return true;
        if (!preEditName.equalsIgnoreCase(name.getText().toString())) return true;
        return false;
    }

    private void sendChanges(){
        JSONObject params = new JSONObject();
        EditText name = findViewById(R.id.et_setName);
        EditText group = findViewById(R.id.et_setGroup);
        RadioGroup week = findViewById(R.id.rg_setWeek);
        try {
            if (preEditWeek != week.getCheckedRadioButtonId()) {
                int newTopWeek = (week.getCheckedRadioButtonId() == R.id.rb_setTopWeek)?TimeUtils.getWeek():TimeUtils.getWeek()-1;
                params.put("top_week",newTopWeek);
            }
            if (!preEditGroup.equals(group.getText().toString())) params.put("group",group.getText().toString());
            if (!preEditName.equalsIgnoreCase(name.getText().toString()))params.put("name",name.getText().toString());
        } catch (JSONException e) {}
        cm.authPostJSONRequest(EDIT_USER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 200){
                        SharedPreferences.Editor editor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
                        editor.putString("name",response.getString("name"));
                        editor.putString("group",response.getString("group"));
                        editor.putInt("top_week",response.getInt("top_week"));
                        editor.apply();
                        Intent main = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    } else if (status == 400){
                        findViewById(R.id.btn_settings_apply).setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Возникла ошибка на сервере. Попробуйте позже",Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {}
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(main);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_settings_apply:
                if (isChanged()) {
                    findViewById(R.id.btn_settings_apply).setEnabled(false);
                    sendChanges();
                }
                break;
            case R.id.btn_settings_cancel:
                onBackPressed();
                break;
        }
    }
}
