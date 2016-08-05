package com.example.kousukenezu.mai_kagamilogin;


import android.os.Parcelable;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Process;
import android.util.Log;

import java.util.Arrays;

public class MyNfc {

    MainActivity mainActivity = null;
    UsbMessage usbMessage = null;
    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;
    IntentFilter[] mWriteTagFilters;

    String body = null;  //読み込んだ文字列を格納する

    MyNfc(MainActivity mainActivity, UsbMessage usbMessage) {
        this.mainActivity = mainActivity;  //メインアクティビティの登録
        this.usbMessage = usbMessage;      //usbメッセージの登録

        mNfcAdapter = NfcAdapter.getDefaultAdapter(mainActivity);

        mNfcPendingIntent = PendingIntent.getActivity(
                mainActivity, 0, new Intent(mainActivity, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mNdefExchangeFilters = new IntentFilter[]{ndefDetected};

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[]{tagDetected};
    }

    //　getNdefMessages()を呼び出し、nfcを読み込む
    public void resume(){
        //NfcAdapterがタグを読み込んだときif文通過
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(mainActivity.getIntent().getAction())) {
            NdefMessage[] msg = getNdefMessages(mainActivity.getIntent());
            byte[] payload = msg[0].getRecords()[0].getPayload();
            remove_en(new String(payload));
            mainActivity.text.setText(this.body);
            mainActivity.setIntent(new Intent());
            usbMessage.sendData(this.body);
        }
    }

    //　ncf作成時に余分に書き込まれる"en"の削除
    private void remove_en(String body) {
        this.body = body.replaceFirst("en", "");
    }

    //　読み込みはここで行われる
    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            mainActivity.finish();
        }
        return msgs;
    }
//    private NdefMessage getNdefMessages(Intent intent) {
//        NdefMessage[] msgs = null;
//        this.body = null;
//        String action = intent.getAction();
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//                return msgs[0];
//            } else {
//                byte[] empty = new byte[]{};
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
//                NdefMessage msg = new NdefMessage(new NdefRecord[]{
//                        record
//                });
//                msgs = new NdefMessage[]{
//                        msg
//                };
//                return msg;
//            }
//        } else {
//            mainActivity.finish();
//        }
//        return null;
//    }


    public String getBody(){ return body; }
}
