package com.example.kousukenezu.mai_kagamilogin;

import android.os.Process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


public class UsbMessage {
    private final int mPort;  //ポート番号
    String data; //送信用データ
    static boolean mode_read = true;  //読み取りモードならtrue
    MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    /* 送信用スレッド */
    private final Runnable mSendTask = new Runnable() {
        public void run() {
            try {
//                mode_read = false;
                ServerSocket serverSocket = new ServerSocket(mPort);  //サーバ側のソケット
                Socket clientSocket = serverSocket.accept();      
                if(clientSocket.isConnected()) {
                    final PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println(data);  //データ送信
                }

                serverSocket.close();
                clientSocket.close();  //ソケットの後処理

//                mainActivity.finish();
            } catch (IOException e) {
            }
            mode_read = true;
        }
    };


    public UsbMessage(final int port) {
        mPort = port;
    }

    //送信用スレッドを起動する
    public void sendData(String d) {
        if(mode_read) {
            data = d;
            Executors.newSingleThreadExecutor().execute(mSendTask);
        }
    }
}