package com.example.timetableapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScrollTableActivity extends AppCompatActivity {

    private final String GET_TIMETABLE_URL = "/info/getTimetable";
    private ConnectionManager cm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_fragments);
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        if (getSharedPreferences("timetable",MODE_PRIVATE).contains("group")) setViewPager();
        else {
            Intent i = new Intent(ScrollTableActivity.this,WelcomeActivity.class);
            startActivityForResult(i,15);
        }
        ImageView iv = findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(ScrollTableActivity.this,WelcomeActivity.class);
                startActivityForResult(login,532);
            }
        });
    }


    private void setViewPager(){
        TablePagerAdapter pagerAdapter = new TablePagerAdapter(getSupportFragmentManager(), this);
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("u");
        int weekday = Integer.parseInt(sdf.format(today));
        if (weekday > 4) weekday = 0;
        ViewPager viewPager = findViewById(R.id.vp_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(weekday);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 15 && resultCode == RESULT_CANCELED) finish();
        if ((requestCode == 15 || requestCode == 532) && resultCode == RESULT_OK){
            if (data.getBooleanExtra("google",false)) {
                TextView dockText = findViewById(R.id.activity_fragments_tv_dockText);
                dockText.setText(data.getStringExtra("name"));
            } else if (data.getBooleanExtra("no-auth",false)) {
                sendTimetableRequest(data.getStringExtra("group"),data.getIntExtra("top_week",0));
            } else {
                TextView dockText = findViewById(R.id.activity_fragments_tv_dockText);
                dockText.setText(data.getStringExtra("group"));
            }
        }
    }

    private void sendTimetableRequest(final String group, final int top_week){
        JSONObject params = new JSONObject();
        try {
            params.put("group", group);
            cm.postJSONRequest(GET_TIMETABLE_URL, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences.Editor editor = getSharedPreferences("timetable", MODE_PRIVATE).edit();
                    try {
                        int status = response.getInt("status");
                        if (status == 200) {
                            editor.putString("group",group);
                            editor.putInt("top_week",top_week);
                            editor.putString("mon", response.getString("mon"));
                            editor.putString("tue", response.getString("tue"));
                            editor.putString("wed", response.getString("wed"));
                            editor.putString("thu", response.getString("thu"));
                            editor.putString("fri", response.getString("fri"));
                            editor.apply();
                            setViewPager();
                            Toast.makeText(getApplicationContext(),"Success on gathering data",Toast.LENGTH_SHORT).show();
                        } else if (status == 400) {
                            Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) { Log.w("sendTTReq",e.getStackTrace().toString());}
    }
}
