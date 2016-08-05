package com.example.kousukenezu.mai_kagamilogin;

import android.location.OnNmeaMessageListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    MyNfc myNfc;
    UsbMessage usbMessage = null;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbMessage = new UsbMessage(9999);
        myNfc = new MyNfc(this, usbMessage);
        usbMessage.setMainActivity(this);
        text = (TextView)findViewById(R.id.textView);

        /* 送信ボタンイベント */
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText)findViewById(R.id.editText);
                SpannableStringBuilder sp = (SpannableStringBuilder)edit.getText(); //データ取得
                usbMessage.sendData(sp.toString()); //送信
            }
        });

    }

    //再開時
    @Override
    protected void onResume() {
        super.onResume();
        myNfc.resume();
    }
}