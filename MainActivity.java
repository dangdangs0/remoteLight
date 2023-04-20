package com.example.myapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    public static Context context_main;
    private TextView mConnectionStatus;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private Button light_button1, light_button2, color_button1,color_button2;
    private static final String TAG = "TcpClient";
    private boolean isConnected = false;
    public Switch light_switch, time_switch;

    private String mServerIP = null;
    private Socket mSocket = null;
    private PrintWriter mOut;
    private BufferedReader mIn;
    private Thread mReceiverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        light_button1 = (Button)findViewById(R.id.button3);
        light_button2 = (Button)findViewById(R.id.button4);
        color_button1 = (Button)findViewById(R.id.button6);
        color_button2 = (Button)findViewById(R.id.button7);
        light_switch = (Switch)findViewById(R.id.switch1);
        time_switch = (Switch)findViewById(R.id.switch4);

        light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    String sendMessage = "1";
                    if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                    else {
                        new Thread(new SenderThread(sendMessage)).start();
                    }

                    light_button1.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            String sendMessage = "3";
                            if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                            else {
                                new Thread(new SenderThread(sendMessage)).start();
                            }
                        }
                    });

                    light_button2.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            String sendMessage = "4";
                            if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                            else {
                                new Thread(new SenderThread(sendMessage)).start();
                            }
                        }
                    });

                    color_button1.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            String sendMessage = "5";
                            if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                            else {
                                new Thread(new SenderThread(sendMessage)).start();
                            }
                        }
                    });

                    color_button2.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            String sendMessage = "6";
                            if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                            else {
                                new Thread(new SenderThread(sendMessage)).start();
                            }
                        }
                    });
                } else {
                    String sendMessage = "2";
                    if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                    else {
                        new Thread(new SenderThread(sendMessage)).start();
                    }
                }
            }
        });
time_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String sendMessage = "g";
                    if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                    else {
                        new Thread(new SenderThread(sendMessage)).start();
                    }
                } else {
                    String sendMessage = "e";
                    if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                    else {
                        new Thread(new SenderThread(sendMessage)).start();
                    }
                }
            }
        });

        mConversationArrayAdapter = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_1 );
        new Thread(new ConnectThread("192.168.78.148", 9000)).start();
        context_main=this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isConnected = false;
    }


    private static long back_pressed;
    @Override
    public void onBackPressed(){

        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();

            Log.d(TAG, "onBackPressed:");
            isConnected = false;

            finish();
        }
        else{
            Toast.makeText(getBaseContext(), "한번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }

    private class ConnectThread implements Runnable {

        private String serverIP;
        private int serverPort;

        ConnectThread(String ip, int port) {
            serverIP = ip;
            serverPort = port;

            mConnectionStatus.setText("connecting to " + serverIP + ".......");
        }

        @Override
        public void run() {

            try {

                mSocket = new Socket(serverIP, serverPort);
              

                mServerIP = mSocket.getRemoteSocketAddress().toString();

            } catch( UnknownHostException e )
            {
                Log.d(TAG,  "ConnectThread: can't find host");
            }
            catch( SocketTimeoutException e )
            {
                Log.d(TAG, "ConnectThread: timeout");
            }
            catch (Exception e) {

                Log.e(TAG, ("ConnectThread:" + e.getMessage()));
            }


            if (mSocket != null) {

                try {

                    mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                    mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));

                    isConnected = true;
                } catch (IOException e) {

                    Log.e(TAG, ("ConnectThread:" + e.getMessage()));
                }
            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (isConnected) {

                        Log.d(TAG, "connected to " + serverIP);
                        mConnectionStatus.setText("connected to " + serverIP);

                    }else{

                        Log.d(TAG, "failed to connect to server " + serverIP);
                        mConnectionStatus.setText("failed to connect to server "  + serverIP);
                    }

                }
            });
        }
    }
    private class TimeThread implements Runnable {

        private String time;

        TimeThread(String time) {
            this.time = time;
        }

        @Override
        public void run() {

            mOut.println(this.time);
            mOut.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send message: " + time);
                    mConversationArrayAdapter.insert(time, 0);
                }
            });
        }
    }
    private class SenderThread implements Runnable {
        private String msg;
        SenderThread(String msg) {
            this.msg = msg;
        }
        @Override
        public void run() {

            mOut.println(this.msg);
            mOut.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send message: " + msg);
                    mConversationArrayAdapter.insert(msg, 0);
                }
            });
        }
    }
    
public void showErrorDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }
    public void button8(View v)
    {
        Intent intent=new Intent(getApplicationContext(), AlarmActivity.class);
        startActivity(intent); //알림 설정할 수 있는 새 창 여는거..
    }

}
