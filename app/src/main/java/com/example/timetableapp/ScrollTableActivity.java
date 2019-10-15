package com.example.timetableapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScrollTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_fragments);
        Intent i = new Intent(ScrollTableActivity.this,WelcomeActivity.class);
        startActivityForResult(i,15);
        TablePagerAdapter pagerAdapter = new TablePagerAdapter(getSupportFragmentManager(), this);
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("u");
        int weekday = Integer.parseInt(sdf.format(today));
        if (weekday > 4) weekday = 0;
        ViewPager viewPager = findViewById(R.id.vp_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(weekday);

        ImageView iv = findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(ScrollTableActivity.this,LoginActivity.class);
                startActivityForResult(login,532);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 15 && resultCode == RESULT_CANCELED) finish();
        if (requestCode == 15 && resultCode == RESULT_OK){
            TextView dockText = findViewById(R.id.activity_fragments_tv_dockText);
            dockText.setText(data.getStringExtra("group"));
        }
    }
}
