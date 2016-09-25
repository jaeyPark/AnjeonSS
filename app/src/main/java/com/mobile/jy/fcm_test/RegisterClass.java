package com.mobile.jy.fcm_test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by JY on 2016-09-09.
 */
public class RegisterClass extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ImageView setImageProfile = (ImageView) findViewById(R.id.firstprofileImgSetting);
        setImageProfile.setImageResource(R.drawable.on);
    }
}
