package com.example.mac.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;

import java.util.HashMap;
import java.util.Map;


public class AddInfo extends Activity {
    String ADDTOPRE_URL="http://172.20.10.3:8080/valmanage/jsp/androidpreparetosave.jsp";
    Spinner photo;
    int index;
    int sleep=0;
    String[] photos;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private TextView tvResult;
    String Account=null;
    private String response=null;
    private String result=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        photo=(Spinner)findViewById(R.id.photo);
        Bundle bundle = this.getIntent().getExtras();
        String account = bundle.getString("account");
        Account=account;
        Button bt=(Button)findViewById(R.id.spinnerbtn);
        Button bt1=(Button)findViewById(R.id.startsavebtn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddInfo.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(AddInfo.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }

            }
        });
        bt1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Thread t = new Thread(newTread);
                t.start();

            }

        });
    }
    Runnable newTread = new Runnable() {
        public void run() {
            TextView addvalnumber=(TextView)findViewById(R.id.addvalnumber);
            TextView addstoragenumber=(TextView)findViewById(R.id.addstoragevalnumber);
            String valnumber=addvalnumber.getText().toString().trim();
            String storagenumber=addstoragenumber.getText().toString().trim();
            String opaction="S";
            Map<String,String> addmessage=new HashMap<String,String>();
            addmessage.put("valorgroupnumber",valnumber);
            addmessage.put("storagelocationnum",storagenumber);
            addmessage.put("opaction",opaction);
            addmessage.put("manindex",Account);
            response=PostUtils.getDataByPost(ADDTOPRE_URL,addmessage,"utf8");
            if(response.equals("sucess")) {
                result="添加成功";

            }else{
                String[] re = response.split("&");
                Log.d("re0",re[0]);
                Log.d("re1",re[1]);
                if(re[0].equals("failed")){
                    result="添加失败！"+re[1];
                }
            }
            handler.sendEmptyMessage(0x123);

        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(AddInfo.this,result,Toast.LENGTH_SHORT).show();
            sleep=1;
            Looper.loop();
        };
    };
    private void startCaptureActivityForResult() {
        Intent intent = new Intent(this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "You must agree the camera permission request before you use the code scan function", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        photos=getResources().getStringArray(R.array.photoitem);
                        index=photo.getSelectedItemPosition();
                        String number=data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        if(photos[index].equals("安全阀编号")&&number.length()>6){
                            TextView tv = (TextView) findViewById(R.id.valnumber);
                            tv.setText("安全阀编号:");
                            tv.setGravity(Gravity.CENTER);
                            TextView tv1 = (TextView) findViewById(R.id.addvalnumber);
                            tv1.setText(number); //or do sth
                            tv1.setGravity(Gravity.CENTER);
                        }else if(photos[index].equals("存储位置编号")&&number.length()<6){
                            TextView tv = (TextView) findViewById(R.id.storagenumber);
                            tv.setText("存储位置编号:");
                            tv.setGravity(Gravity.CENTER);
                            TextView tv1 = (TextView) findViewById(R.id.addstoragevalnumber);
                            tv1.setText(number); //or do sth
                            tv1.setGravity(Gravity.CENTER);
                        }
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                           // tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                }
                break;
        }
    }



}
