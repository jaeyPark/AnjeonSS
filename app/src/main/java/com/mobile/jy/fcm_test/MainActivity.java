package com.mobile.jy.fcm_test;

//import android.content.Intent;
import android.app.TabActivity;
//import android.content.Intent; //다시 풀기
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.URL;

//import com.google.firebase.iid.
//import com.google.firebase.messaging.*;

public class MainActivity extends TabActivity implements View.OnClickListener {
    String token;
    Button fastCallBtn;

    Button profileSetting;
    Button login;
    Button register;

    ImageView btn1;
    ImageView btn2;
    ImageView btn3;

    int i=1;
    int j=1;
    int k=1;

    private static final int CALL_REQUESTS = 1;

    private static final String TAG = "DatabaseActivity";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference memoRef = database.getReference("memo");

    TextView fnum1, fnum2, fnum3; //화면에 보일 TextView
    String fnm1, fnm2, fnm3; //단축번호 설정에서 단축번호 1, 2, 3
    String fnn1, fnn2, fnn3; //단축번호 설정에서 이름 1, 2, 3
    String url;
    Bitmap profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token = FirebaseInstanceId.getInstance().getToken();

        btn1=(ImageView)findViewById(R.id.imageButton1);
        btn1.setOnClickListener(this);

        btn2=(ImageView)findViewById(R.id.imageButton2);
        btn2.setOnClickListener(this);

        btn3=(ImageView)findViewById(R.id.imageButton3);
        btn3.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseMessaging.getInstance().subscribeToTopic("notice");

        url = sharedPreferences.getString("url", "http://mm.sookmyung.ac.kr/~m1413286/media/facebook.gif");

        try {
            URL facebookProfileURL = new URL(url);
        //    Bitmap bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        } catch (java.net.MalformedURLException e){
            e.printStackTrace();
        } catch (java.io.IOException e){
            e.printStackTrace();
        }

    //    btn1.setImageBitmap(profilePic);

        final TabHost mTabHost = getTabHost();
        final Switch police = (Switch) findViewById(R.id.policeCallSwitch); //스위치 버튼, 경차 연락 알람
        final Switch alarmSound = (Switch) findViewById(R.id.alarmSwitch); //알람 소리 On/off 할 스위치

        mTabHost.addTab(mTabHost.newTabSpec("ONE").setContent(R.id.home).setIndicator("Home"));
        mTabHost.addTab(mTabHost.newTabSpec("TWO").setContent(R.id.setting).setIndicator("설정"));

        fnum1 = (TextView)findViewById(R.id.fasNum1);
        fnum2 = (TextView)findViewById(R.id.fasNum2);
        fnum3 = (TextView)findViewById(R.id.fasNum3);

        // SharedPreferences 안에 저장한 내용을 불러오려고 했는데 저장이 안되었어!
        // https://developer.android.com/training/basics/data-storage/index.html 페이지 참조!

        fnum1.setText("단축번호 1 : "+sharedPreferences.getString("fnn1", "")+" "+sharedPreferences.getString("fnm1", "0"));
        /*
        fnum2.setText("단축번호 2 : "+sharedPreferences.getString("fnn2", "")+" "+sharedPreferences.getString("fnm2", ""));
        fnum3.setText("단축번호 3 : "+sharedPreferences.getString("fnn3", "")+" "+sharedPreferences.getString("fnm3", ""));
        */

        /*
        tokenButton = (Button)findViewById(R.id.tokenButton);
        tokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
            }
        });
        */

        //메모 표시 및 등록

        final TextView memo1Text = (TextView) findViewById(R.id.memo1);

        memoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                memo1Text.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //    Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        final EditText memoedit = (EditText) findViewById(R.id.memoEditText);
        Button registerButton = (Button) findViewById(R.id.memoBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = memoedit.getText().toString();
                memoRef.setValue(text);
                memo1Text.setText(text);
                memoedit.setText("");
            }
        });

        memo1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                memoedit.setText(memo1Text.getText());
            }
        });

    //    Intent i = getIntent();

        //설정 탭
        //final PushDialog pd = new PushDialog();

        //1. 경찰서 연락 설정
        police.setChecked(sharedPreferences.getBoolean("police_flag", true));
        police.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("police_flag", isChecked);
                editor.commit();
        //        pd.setPoliceFlag(isChecked);
                if(isChecked) {
                    //버튼이 On 상태일때, 일정 시간 후에 문자하기(일단은 다이얼로그와 함꼐 바로 문자가 되는걸로)
                    Toast.makeText(getApplicationContext(), "경찰서 전화", Toast.LENGTH_SHORT).show();
                }
                else {
                    //버튼이 Off 상태일때, 경찰서에 전화->전화하도록 하기
                    Toast.makeText(getApplicationContext(), "경찰서 문자", Toast.LENGTH_SHORT).show();
                }
            }
        });//여기까지 경찰 연락


        //2. 단축 키 설정
        fastCallBtn = (Button)findViewById(R.id.fastCallButtonSetting);
        fastCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다음 액티비티로 넘어가도록
                //Toast.makeText(getApplicationContext(), "다음 액티비티", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SetKeyNumber.class);
                startActivityForResult(intent, CALL_REQUESTS);
                finish();
                //startActivity(new Intent(MainActivity.this, SetKeyNumber.class));
        //        mTabHost.setCurrentTab(2);

            }
        });
        //받아 온 값 화면 및 다이얼로그에 넘겨주기

        Intent fnum = getIntent();
        if (fnum.hasExtra("fnn1")) {
            fnm1 = fnum.getExtras().getString("fnm1").toString();
            fnm2 = fnum.getExtras().getString("fnm2").toString();
            fnm3 = fnum.getExtras().getString("fnm3").toString();
/*
            editor.putString("fnm1", fnm1);
            editor.putString("fnm2", fnm2);
            editor.putString("fnm3", fnm3);
*/
            fnn1 = fnum.getExtras().getString("fnn1").toString();
            fnn2 = fnum.getExtras().getString("fnn2").toString();
            fnn3 = fnum.getExtras().getString("fnn3").toString();
/*
            editor.putString("fnn1", fnn1);
            editor.putString("fnn2", fnn2);
            editor.putString("fnn3", fnn3);
*/
            fnum1.setText("단축번호 1 : "+fnn1+" "+fnm1);
            fnum2.setText("단축번호 2 : "+fnn2+" "+fnm2);
            fnum3.setText("단축번호 3 : "+fnn3+" "+fnm3);

            mTabHost.setCurrentTab(1);
        }

        //3. 알람 소리 설정
        alarmSound.setChecked(sharedPreferences.getBoolean("alert_flag", true));
        alarmSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("alert_flag", isChecked);
                editor.commit();
                if(isChecked) {
                    //버튼이 On 상태일때, 알람 울리도록
                    Toast.makeText(getApplicationContext(), "알람울림", Toast.LENGTH_SHORT).show();
                }
                else {
                    //버튼이 Off 상태일때, 알람 울리지 않도록
                    Toast.makeText(getApplicationContext(), "알람 안울림", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileSetting = (Button)findViewById(R.id.profileSetting);
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, ProfileSettingClass.class));
            }
        });
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, LoginClass.class));
            }
        });
        register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, RegisterClass.class));
            }
        });
    }

    public void onClick(View view) {
        if(view.getId()==R.id.imageButton1)
        {
            if (i % 2 == 1) {
                btn1.setImageResource(R.drawable.off);
                i++;
            } else {
                btn1.setImageResource(R.drawable.on);
                i--;
            }
        }
        else if(view.getId()==R.id.imageButton2)
        {
            if (j % 2 == 1) {
                btn2.setImageResource(R.drawable.off);
                j++;
            } else {
                btn2.setImageResource(R.drawable.on);
                j--;
            }
        }
        else
        {
            if (k % 2 == 1) {
                btn3.setImageResource(R.drawable.off);
                k++;
            } else {
                btn3.setImageResource(R.drawable.on);
                k--;
            }
        }
    }
}
