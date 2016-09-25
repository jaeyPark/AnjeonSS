package com.mobile.jy.fcm_test;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by JY on 2016-07-13.
 */
public class PushDialog extends AppCompatActivity {
    private boolean police_flag = true;
    private boolean alert_flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        police_flag = sharedPreferences.getBoolean("police_flag", true);
        alert_flag = sharedPreferences.getBoolean("alert_flag", true);
        AlertDialog dialog = createDialogBox();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.show();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD//잠금 화면 위에 뜨게하기.
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// 켜진 화면 유지 면안꺼지게하
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);// 화면 깨우기.

        /*
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE); //투명하게 만들엑티비티의 타이틀을 없엠
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //사용자가 화면을 끄지않는한 꺼지지않게 유지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, // 생성되는 엑티비티의 초점을 잃게 만듬. 뿌옇게
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        */

        FirebaseMessaging.getInstance().subscribeToTopic("notice");
    }

    public AlertDialog createDialogBox(){ // 원래는 private
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);


        builder.setTitle("경고");
        builder.setMessage("칩입자가 발생하였습니다!");
        builder.setIcon(R.drawable.alert);

        // msg 는 그저 String 타입의 변수, tv 는 onCreate 메서드에 글을 뿌려줄 TextView
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        builder.setNeutralButton("경찰서 연락", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                Toast.makeText(getApplicationContext(),"삐용삐용", Toast.LENGTH_SHORT).show();
                Intent intent;
                if (police_flag) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.daum.net"));
                }

                try {
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e) {
                }

                finish();
            }
        });

        builder.setNegativeButton("단축키", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                //    Toast.makeText(getApplicationContext(),"따르릉", Toast.LENGTH_SHORT).show();
                String items[] = {"1번", "2번", "3번"};  // 저장할 때 설정해서 넘길 수 있도록
                AlertDialog.Builder ab = new AlertDialog.Builder(PushDialog.this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                ab.setTitle("긴급 연락처 단축키");
                ab.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(getApplicationContext(), Integer.toString(whichButton + 1), Toast.LENGTH_SHORT).show();
                            }
                        }).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // OK 버튼 클릭시 Intetn를 수행하도록 함(해당 번호로 전화)
                                Toast.makeText(getApplicationContext(), "따르릉", Toast.LENGTH_SHORT).show();
                                // 전화 걸어욥
                                finish();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Cancel 버튼 클릭 시
                            }
                        });
                ab.show();
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }
}

