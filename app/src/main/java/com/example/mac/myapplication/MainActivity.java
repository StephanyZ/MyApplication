package com.example.mac.myapplication;


import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity{
    String msg = "Android : ";
    public static String  LOGIN_URL= "http://192.168.1.100:8080/valmanage/jsp/androidlogin.jsp";
    //public static String  LOGIN_URL= "http://172.20.10.3:8080/valmanage/jsp/androidlogin.jsp";
    private String response=null;
    private String result=null;
    String flag=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt=(Button)findViewById(R.id.loginbutton);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(newTread);
                t.start();
            }
        });
    }

    Runnable newTread = new Runnable() {
        public void run() {
            EditText user = (EditText)findViewById(R.id.useraccount);
            String userac=user.getText().toString().trim();
            EditText password = (EditText) findViewById(R.id.password);
            String passwd=password.getText().toString().trim();

            Map<String,String> usermessage=new HashMap<String,String>();
            usermessage.put("useraccount",userac);
            usermessage.put("password",passwd);

            response=PostUtils.getDataByPost(LOGIN_URL,usermessage,"utf8");
            if(response.equals("sucess")){
                result="登陆成功";
                handler.sendEmptyMessage(0x123);
                Intent it=new Intent().setClass(MainActivity.this,Homepage.class);
                Bundle bundle=new Bundle();
                bundle.putString("account", userac);
                it.putExtras(bundle);
                startActivity(it);
                MainActivity.this.finish();
            }else if(response.equals("failed")){
                result="密码或账号错误";
                handler.sendEmptyMessage(0x123);
            }
        }

    };


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
        };
    };

}
