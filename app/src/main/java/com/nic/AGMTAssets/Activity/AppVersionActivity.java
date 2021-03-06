package com.nic.AGMTAssets.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Support.MyCustomTextView;


public class AppVersionActivity extends AppCompatActivity implements View.OnClickListener {


   private MyCustomTextView btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_update_dialog);
        intializeUI();



    }

    public void intializeUI(){
        btnSave = (MyCustomTextView)findViewById(R.id.btn_ok);
        btnSave.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
               showGooglePlay();
               break;
        }
    }

    public void showGooglePlay() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drdpr.tn.gov.in/")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drdpr.tn.gov.in/")));
        }
    }
}
