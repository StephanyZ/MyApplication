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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddInfo extends Activity {
    String ADDTOPRE_URL="http://192.168.1.100:8080/valmanage/jsp/androidpreparetosave.jsp";
    String GETVALINFO_URL="http://192.168.1.100:8080/valmanage/jsp/show.jsp";
    //String ADDTOPRE_URL="http://172.20.10.3:8080/valmanage/jsp/androidpreparetosave.jsp";
    //String GETVALINFO_URL="http://172.20.10.3:8080/valmanage/jsp/show.jsp";
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    Spinner photo;
    String VALINFO="";
    int index;
    int Flag=0;
    int sleep=0;
    String[] photos;
    private TableLayout tableLayout;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private TextView tvResult;
    String Account=null;
    private String response=null;
    private String result=null;
    JSONArray selectcheck=new JSONArray();
    CheckBox checkBox[]=new CheckBox[50];
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
                Log.d("ButtonJson",selectcheck.toString());
                Log.d("length",selectcheck.length()+"");
                Thread t = new Thread(newTread);
                t.start();

            }

        });


    }
    Runnable newTread = new Runnable() {
        public void run() {
            TextView addvalnumber=(TextView)findViewById(R.id.addvalnumber);
            TextView addstoragenumber=(TextView)findViewById(R.id.addstoragevalnumber);
            TextView addexlocarionnum=(TextView)findViewById(R.id.addexlocationnum);
            String valnumber=addvalnumber.getText().toString().trim();
            String storagenumber=addstoragenumber.getText().toString().trim();
            String exlocationnum=addexlocarionnum.getText().toString().trim();
            String opaction="S";
            Map<String,String> addmessage=new HashMap<String,String>();
            addmessage.put("valorgroupnumber",valnumber);
            addmessage.put("storagelocationnum",storagenumber);
            addmessage.put("opaction",opaction);
            addmessage.put("manindex",Account);
            addmessage.put("checkedinfo",selectcheck.toString());
            if(selectcheck.length()==0) {
                addmessage.put("option", "nochecksave");
            }else if(selectcheck.length()>0){
                addmessage.put("option", "ischeckedsave");
                addmessage.put("exlocationnum",exlocationnum);
            }
            response=PostUtils.getDataByPost(ADDTOPRE_URL,addmessage,"utf8");
            if(response.equals("sucess")) {
                result="添加成功";
                handler.sendEmptyMessage(0x123);
                handler2.sendEmptyMessage(0x123);
            }else{
                String[] re = response.split("&");
                Log.d("re0",re[0]);
                Log.d("re1",re[1]);
                if(re[0].equals("failed")){
                    result="添加失败！"+re[1];
                }
                handler.sendEmptyMessage(0x123);
            }

        }
    };
    Runnable newTread1 = new Runnable() {
        public void run() {
            TextView addvalnumber=(TextView)findViewById(R.id.addvalnumber);
            String valnumber=addvalnumber.getText().toString().trim();
            Map<String,String> addmessage=new HashMap<String,String>();
            addmessage.put("valorgroupnumber",valnumber);
            addmessage.put("option","androidshowvalinfo");
            response=PostUtils.getDataByPost(GETVALINFO_URL,addmessage,"utf8");
            if(response!=null) {
                VALINFO=response;
                //Log.d("result",response);
                handler1.sendEmptyMessage(0x123);
                Flag=1;
            }else{
                result="网络故障";
                handler.sendEmptyMessage(0x123);
            }

        }
    };
    private Handler handler1=new Handler(){
        public void handleMessage(android.os.Message msg) {
            try {
                JSONObject data1 = new JSONObject(response);
                if (data1.getString("status").equals("Cantbesaved")) {
                    Toast.makeText(AddInfo.this, "安全阀编码可能存在错误，请核实", Toast.LENGTH_SHORT).show();
                } else {
                    String str = "";
                    tableLayout = (TableLayout) findViewById(R.id.valinfoshow);
                    tableLayout.removeAllViews();
                    tableLayout.setStretchAllColumns(true);
                    String colume_head[] = {"序号", "编号", "出厂编号", "使用单位", "型号"};
                    String colume_s[] = {"index","valnumber", "valproductno", "manufacture", "valvecate"};
                    {
                        TableRow tableRow = new TableRow(AddInfo.this);
                        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        for (int k = 0; k < colume_head.length; k++) {
                            TextView tv = new TextView(AddInfo.this);
                            ViewGroup parent = (ViewGroup) tv.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tv = new TextView(AddInfo.this);
                            tv.setText(colume_head[k]);
                            tv.setBackgroundResource(R.drawable.table_textview);
                            tv.setGravity(Gravity.CENTER);
                            lp1.setMargins(5, 5, 5, 5);
                            tv.setLayoutParams(lp1);
                            tableRow.addView(tv);
                        }
                        tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                    }

                    JSONArray valarray = new JSONArray(data1.getString("values"));

                    if (data1.getString("valorgroup").equals("val")) {
                        selectcheck=new JSONArray();
                        TableRow tableRow = new TableRow(AddInfo.this);
                        JSONObject val = valarray.getJSONObject(0);
                        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        {
                            TextView tv = new TextView(AddInfo.this);
                            ViewGroup parent = (ViewGroup) tv.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            if(data1.getString("status").equals("checkedwillbesaved")){
                                CheckBox checkBoxval = new CheckBox(AddInfo.this);
                                checkBoxval.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
                                        if(ischecked){
                                            try {
                                                selectcheck.getJSONObject(0).put("ischecked",true);
                                                Log.d("Json",selectcheck.toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            try {
                                                selectcheck.getJSONObject(0).put("ischecked",false);
                                                Log.d("Json",selectcheck.toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                checkBoxval.setGravity(Gravity.CENTER);
                                lp1.setMargins(5, 5, 5, 5);
                                checkBoxval.setLayoutParams(lp1);
                                tableRow.addView(checkBoxval);
                            }else if(data1.getString("status").equals("willbesaved")) {
                                tv = new TextView(AddInfo.this);
                                tv.setText(1 + "");
                                tv.setBackgroundResource(R.drawable.table_textview);
                                tv.setGravity(Gravity.CENTER);
                                lp1.setMargins(5, 5, 5, 5);
                                tv.setLayoutParams(lp1);
                                tableRow.addView(tv);
                            }

                        }
                        for (int j = 0; j < colume_head.length; j++) {
                            TextView tv = new TextView(AddInfo.this);
                            ViewGroup parent = (ViewGroup) tv.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tv = new TextView(AddInfo.this);
                            tv.setText(val.getString(colume_s[j]));
                            tv.setBackgroundResource(R.drawable.table_textview);
                            tv.setGravity(Gravity.CENTER);
                            lp1.setMargins(5, 5, 5, 5);
                            tv.setLayoutParams(lp1);
                            tableRow.addView(tv);
                        }
                        ViewGroup parent = (ViewGroup) tableRow.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                    } else if (data1.getString("valorgroup").equals("group")) {
                        selectcheck=new JSONArray();
                        for (int i = 0; i < valarray.length()+1; i++) {
                            TableRow tableRow = new TableRow(AddInfo.this);
                            JSONObject val = valarray.getJSONObject(i);
                            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                            {
                                TextView tv = new TextView(AddInfo.this);
                                ViewGroup parent = (ViewGroup) tv.getParent();
                                if (parent != null) {
                                    parent.removeAllViews();
                                }
                                if(data1.getString("status").equals("checkedwillbesaved")){
                                    checkBox[i] = new CheckBox(AddInfo.this);
                                    final int kk=i;
                                    JSONObject ob=new JSONObject();
                                    ob.put("cbvalnumber",val.getString("valnumber"));
                                    checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
                                            if(ischecked){
                                                try {
                                                    selectcheck.getJSONObject(kk).put("ischecked",true);
                                                    Log.d("Json",selectcheck.toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                try {
                                                    selectcheck.getJSONObject(kk).put("ischecked",false);
                                                    Log.d("Json",selectcheck.toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                    ob.put("ischecked",false);
                                    selectcheck.put(i,ob);
                                    checkBox[i].setGravity(Gravity.CENTER);
                                    lp1.setMargins(5, 5, 5, 5);
                                    checkBox[i].setLayoutParams(lp1);
                                    tableRow.addView(checkBox[i]);
                                }else if(data1.getString("status").equals("willbesaved")) {

                                    tv = new TextView(AddInfo.this);
                                    int kk=i+1;
                                    tv.setText(kk + "");
                                    tv.setBackgroundResource(R.drawable.table_textview);
                                    tv.setGravity(Gravity.CENTER);
                                    lp1.setMargins(5, 5, 5, 5);
                                    tv.setLayoutParams(lp1);
                                    tableRow.addView(tv);
                                }

                            }

                            for (int j = 1; j < colume_head.length; j++) {
                                TextView tv = new TextView(AddInfo.this);
                                ViewGroup parent = (ViewGroup) tv.getParent();
                                if (parent != null) {
                                    parent.removeAllViews();
                                }
                                tv = new TextView(AddInfo.this);
                                tv.setText(val.getString(colume_s[j]));
                                tv.setBackgroundResource(R.drawable.table_textview);
                                tv.setGravity(Gravity.CENTER);
                                lp1.setMargins(5, 5, 5, 5);
                                tv.setLayoutParams(lp1);
                                tableRow.addView(tv);
                            }

                            ViewGroup parent = (ViewGroup) tableRow.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                        }
                    }
                    Log.d("result", str);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("VALINFO",VALINFO);
            Looper.loop();
        };
    };
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(AddInfo.this,result,Toast.LENGTH_SHORT).show();
            sleep=1;
            Looper.loop();
        };
    };

    private Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent it=new Intent().setClass(AddInfo.this,AddInfo.class);
            Bundle bundle=new Bundle();
            bundle.putString("account", Account);
            it.putExtras(bundle);
            startActivity(it);
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
                            Thread t = new Thread(newTread1);
                            t.start();
                        }else if(photos[index].equals("存储位置编号")&&number.length()<6){
                            TextView tv = (TextView) findViewById(R.id.storagenumber);
                            tv.setText("存储位置编号:");
                            tv.setGravity(Gravity.CENTER);
                            TextView tv1 = (TextView) findViewById(R.id.addstoragevalnumber);
                            tv1.setText(number); //or do sth
                            tv1.setGravity(Gravity.CENTER);
                        }else if(photos[index].equals("附加存储位置编号")&&number.length()<6){
                            TextView tv = (TextView) findViewById(R.id.exlocationnum);
                            tv.setText("附加存储位置编号:");
                            tv.setGravity(Gravity.CENTER);
                            TextView tv1 = (TextView) findViewById(R.id.addexlocationnum);
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
