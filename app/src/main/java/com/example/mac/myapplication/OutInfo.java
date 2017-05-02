package com.example.mac.myapplication;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mac.myapplication.PostUtils.localhost;

public class OutInfo extends Activity {
    String ADDTOPRE_URL="http://"+localhost+":8080/valmanage/jsp/androidpreparetosave.jsp";
    String SHOW_URL = "http://"+localhost+":8080/valmanage/jsp/getlocationsavedinfo.jsp";
    //String  SHOW_URL= "http://172.20.10.3:8080/valmanage/jsp/showsaveactioninfo.jsp";
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private String response = null;
    private String result = null;
    private TableLayout tableLayout;
    int Flag = 0;
    String Account=null;

    JSONArray selectcheck = new JSONArray();
    CheckBox checkBox[] = new CheckBox[50];
    Button bt_gettable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_info);
        Bundle bundle = this.getIntent().getExtras();
        String account = bundle.getString("account");
        Account=account;
        Thread t = new Thread(newTread);
        t.start();

        Button bt=(Button)findViewById(R.id.outinfobutton);
        bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d("ButtonJson",selectcheck.toString());
                Log.d("length",selectcheck.length()+"");
                Thread t1 = new Thread(newTread1);
                t1.start();
            }
        });
    }

    Runnable newTread = new Runnable() {
        public void run() {
            response = PostUtils.getDataByGet(SHOW_URL, null, "utf8");
            if (response != null) {
                Flag = 1;
                handler1.sendEmptyMessage(0x123);
            } else {
                result = "网络故障";
                handler.sendEmptyMessage(0x123);
            }
        }
    };
    Runnable newTread1 = new Runnable() {
        public void run() {
            Map<String,String> addmessage=new HashMap<String,String>();
            addmessage.put("option","preoutvalve");
            addmessage.put("checkedinfo",selectcheck.toString());
            addmessage.put("manindex",Account);
            response = PostUtils.getDataByPost(ADDTOPRE_URL,addmessage, "utf8");
            if (response != null) {
                if(response.equals("sucess")){
                    result="预备出库成功";
                    handler.sendEmptyMessage(0x123);
                }else{
                    String[] re = response.split("&");
                    if(re[0].equals("failed")){
                        result=re[1];
                    }
                    handler.sendEmptyMessage(0x123);
                }

            } else {
                result = "网络故障";
                handler.sendEmptyMessage(0x123);
            }
        }
    };
    private Handler handler1=new Handler(){
        public void handleMessage(android.os.Message msg) {
            try {
                tableLayout = (TableLayout) findViewById(R.id.tableshowout);
                tableLayout.removeAllViews();
                tableLayout.setStretchAllColumns(true);
                JSONArray data = new JSONArray(response);
                int colume = 5;
                int row = data.length();
                String colume_s[] = {"index","valnumber", "storagelocationnum", "optime", "valstatus"};
                String colume_head[]={"","编号","位置","时间","状态"};
                {
                    TableRow tableRow = new TableRow(OutInfo.this);
                    TableRow.LayoutParams lp1=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
                    for (int k = 0; k < 5; k++) {
                        TextView tv = new TextView(OutInfo.this);
                        ViewGroup parent = (ViewGroup) tv.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        tv = new TextView(OutInfo.this);
                        tv.setText(colume_head[k]);
                        tv.setBackgroundResource(R.drawable.table_head);
                        tv.setGravity(Gravity.CENTER);
                        lp1.setMargins(5,5,5,5);
                        tv.setLayoutParams(lp1);
                        tableRow.addView(tv);
                    }
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                }
                for (int i = 0; i < data.length()+1; i++) {
                    TableRow tableRow = new TableRow(OutInfo.this);
                    JSONObject mydata = data.getJSONObject(i);
                    TableRow.LayoutParams lp1=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
                    checkBox[i] = new CheckBox(OutInfo.this);
                    final int kk=i;
                    JSONObject ob=new JSONObject();
                    ob.put("cbvalnumber",mydata.getString("valnumber"));
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
                    for (int j = 1; j < colume; j++) {
                        TextView tv = new TextView(OutInfo.this);
                        ViewGroup parent = (ViewGroup) tv.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        tv = new TextView(OutInfo.this);
                        if(colume_s[j].equals("optime")){
                            String date=mydata.getString(colume_s[j]);
                            date=date.substring(0,10);
                            tv.setText(date);
                            tv.setBackgroundResource(R.drawable.table_textview);
                        }else if(colume_s[j].equals("valstatus")){
                            if(mydata.getString(colume_s[j]).equals("Y")){
                                tv.setText("已检");
                                tv.setBackgroundResource(R.drawable.textview_save);
                            }else if(mydata.getString(colume_s[j]).equals("N")){
                                tv.setText("未检");
                                tv.setBackgroundResource(R.drawable.textview_send);
                            }
                        }else{
                            tv.setText(mydata.getString(colume_s[j]));
                            tv.setBackgroundResource(R.drawable.table_textview);
                        }

                        tv.setGravity(Gravity.CENTER);
                        lp1.setMargins(5,5,5,5);
                        tv.setLayoutParams(lp1);
                        tableRow.addView(tv);
                    }
                    ViewGroup parent = (ViewGroup) tableRow.getParent();
                    if (parent != null) {
                        parent.removeAllViews();
                    }
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Looper.loop();
        };
    };
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(OutInfo.this, result, Toast.LENGTH_SHORT).show();
        };
    };
    private Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent it=new Intent().setClass(OutInfo.this,OutInfo.class);
            Bundle bundle=new Bundle();
            bundle.putString("account", Account);
            it.putExtras(bundle);
            startActivity(it);
            Looper.loop();
        };
    };
}
