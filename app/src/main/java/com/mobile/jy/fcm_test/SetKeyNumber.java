package com.mobile.jy.fcm_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

/**
 * Created by Administrator on 2016-08-28.
 */
public class SetKeyNumber extends AppCompatActivity{
    Button deliverBtn, clearBtn;
    Button plusFamily1, plusFamily2, plusFamily3; //연락처 추가하기 위한 버튼
    EditText skname1, skname2, skname3; //단축번호 사람 1, 2, 3
    TextView sknum1, sknum2, sknum3; //단축번호 1, 2, 3

    /*String [] arrProjection = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };

    String [] arrPhoneProjection = {
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setkeynumber);

        //getApplicationContext()

        skname1 = (EditText)findViewById(R.id.keyCallName1);
        skname2 = (EditText)findViewById(R.id.keyCallName2);
        skname3 = (EditText)findViewById(R.id.keyCallName3);

        sknum1 = (TextView) findViewById(R.id.keyCallNum1);
        sknum2 = (TextView) findViewById(R.id.keyCallNum2);
        sknum3 = (TextView) findViewById(R.id.keyCallNum3);

        plusFamily1 = (Button)findViewById(R.id.KeyCNbtn1);
        plusFamily2 = (Button)findViewById(R.id.KeyCNbtn2);
        plusFamily3 = (Button)findViewById(R.id.KeyCNbtn3);

        plusFamily1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              /*  Cursor clsCursor = getApplicationContext().getContentResolver().query(
                      ContactsContract.Contacts.CONTENT_URI, arrPhoneProjection,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                        null, null
                );

                while (clsCursor.moveToNext()) {
                    Cursor clsPhoneCursor = getApplicationContext().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrPhoneProjection,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + clsCursor.getString(0), //이 부분 다시
                            null, null
                    );

                    while (clsPhoneCursor.moveToNext()) {
                        sknum1.setText(clsPhoneCursor.getString(0));
                    }
                    clsPhoneCursor.close();
                }
                clsCursor.close();*/
                Intent mintent = new Intent(Intent.ACTION_PICK);
                mintent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(mintent, 1);
            }
        });

        plusFamily2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mintent = new Intent(Intent.ACTION_PICK);
                mintent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(mintent, 2);
            }
        });

        plusFamily3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent mintent = new Intent(Intent.ACTION_PICK);
                mintent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(mintent, 3);
            }
        });

        deliverBtn = (Button) findViewById(R.id.OK);
        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이전페이지로 단축번호 정보 전달, Bundle 사용하기---->firebase로 전달하기도 필요할 듯
                Intent intent = new Intent(SetKeyNumber.this, MainActivity.class);

                intent.putExtra("fnn1", skname1.getText().toString());
                intent.putExtra("fnn2", skname2.getText().toString());
                intent.putExtra("fnn3", skname3.getText().toString());

                intent.putExtra("fnm1", sknum1.getText().toString());
                intent.putExtra("fnm2", sknum2.getText().toString());
                intent.putExtra("fnm3", sknum3.getText().toString());

                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
                finish(); //현재 액티비티 종료
                //Toast.makeText(getApplicationContext(), "정보전달", Toast.LENGTH_SHORT).show();
            }
        });

        clearBtn = (Button)findViewById(R.id.cleanNum);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editText에 있는 정보 모두 초기화
                skname1.setText("");
                skname2.setText("");
                skname3.setText("");

                sknum1.setText("");
                sknum2.setText("");
                sknum3.setText("");

                Toast.makeText(getApplicationContext(), "초기화", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToNext();
            String sNumber = cursor.getString(1); //sNumber은 받아온 전화번호

            switch (requestCode) {
                case 1:
                    sknum1.setText(sNumber);
                    break;
                case 2:
                    sknum2.setText(sNumber);
                    break;
                case 3:
                    sknum3.setText(sNumber);
                    break;
            }
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
