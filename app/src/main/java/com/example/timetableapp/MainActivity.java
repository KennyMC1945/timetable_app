package com.example.timetableapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String GET_TIMETABLE_URL = "/info/getTimetable";
    private FirebaseAuth mAuth;
    private ConnectionManager cm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_fragments);
        mAuth = FirebaseAuth.getInstance();
        cm = new ConnectionManager(getString(R.string.server_url),getApplicationContext());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean authenticated = getSharedPreferences("user_info",MODE_PRIVATE).getBoolean("authenticated",false);
        /* TODO: Auth checking */
        if (getSharedPreferences("user_info",MODE_PRIVATE).contains("group")) {
            updateToolbar(authenticated);
            setViewPager();
        } else {
            Intent i = new Intent(MainActivity.this,WelcomeActivity.class);
            startActivityForResult(i,15);
        }
        findViewById(R.id.iv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("timetable", MODE_PRIVATE).edit();
                SharedPreferences.Editor infoEditor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
                infoEditor.clear();
                infoEditor.commit();
                editor.clear();
                editor.commit();
                Intent thisInt = getIntent();
                finish();
                startActivity(thisInt);

                FirebaseAuth.getInstance().signOut();
            }
        });
        ImageView iv = findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences("user_info",MODE_PRIVATE).getBoolean("authenticated",false)) {
                    Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(settings);
                    finish();
                } else {
                    Intent login = new Intent(MainActivity.this, WelcomeActivity.class);
                    login.putExtra("edit",true);
                    startActivityForResult(login, 532);
                }
            }
        });
    }



    private void setViewPager(){
        TablePagerAdapter pagerAdapter = new TablePagerAdapter(getSupportFragmentManager(), this);
        int weekday = TimeUtils.getWeekDay();
        if (weekday > 5) weekday = 1;
        ViewPager viewPager = findViewById(R.id.vp_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(weekday-1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        SharedPreferences userInfo = getSharedPreferences("user_info",MODE_PRIVATE);
        if (requestCode == 15 && resultCode == RESULT_CANCELED) finish();
        if ((requestCode == 15 || requestCode == 532) && resultCode == RESULT_OK){
            if (data.getBooleanExtra("no-auth",false)) {
                sendTimetableRequest(data.getStringExtra("group"), data.getIntExtra("top_week", 0));
            } else {
                sendTimetableRequest(userInfo.getString("group",""), userInfo.getInt("top_week",0));
            }

        }
    }

    private void updateToolbar(boolean authenticated) {
        TextView groupName = findViewById(R.id.activity_fragments_tv_dockGroup);
        TextView userName = findViewById(R.id.activity_fragments_tv_dockUserName);
        SharedPreferences user_info = getSharedPreferences("user_info",MODE_PRIVATE);
        groupName.setText("Группа: " + user_info.getString("group",""));
        if (authenticated) {
            userName.setText(user_info.getString("name",""));
            userName.setTypeface(null, Typeface.NORMAL);
        }
        else {
            userName.setText(getString(R.string.default_name));
            userName.setTypeface(userName.getTypeface(), Typeface.ITALIC);
        }

    }

    private void sendTimetableRequest(final String group, final int top_week){
        JSONObject params = new JSONObject();
        try {
            params.put("group", group);
            cm.postJSONRequest(GET_TIMETABLE_URL, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences userInfo = getSharedPreferences("user_info",MODE_PRIVATE);
                    SharedPreferences.Editor infoEditor = userInfo.edit();
                    SharedPreferences.Editor ttEditor = getSharedPreferences("timetable", MODE_PRIVATE).edit();
                    try {
                        int status = response.getInt("status");
                        if (status == 200) {
                            infoEditor.putString("group",group);
                            infoEditor.putInt("top_week",top_week);
                            ttEditor.putString("mon", response.getString("mon"));
                            ttEditor.putString("tue", response.getString("tue"));
                            ttEditor.putString("wed", response.getString("wed"));
                            ttEditor.putString("thu", response.getString("thu"));
                            ttEditor.putString("fri", response.getString("fri"));
                            ttEditor.apply();
                            infoEditor.apply();
                            setViewPager();
                            Toast.makeText(getApplicationContext(),"Success on gathering data",Toast.LENGTH_SHORT).show();
                            updateToolbar(userInfo.getBoolean("authenticated",false));
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
