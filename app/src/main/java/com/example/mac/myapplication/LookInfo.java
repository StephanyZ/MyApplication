package com.example.mac.myapplication;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.mac.myapplication.PostUtils.localhost;

public class LookInfo extends Activity {
    String  SHOW_URL= "http://"+localhost+":8080/valmanage/jsp/showsaveactioninfo.jsp";
    //String  SHOW_URL= "http://172.20.10.3:8080/valmanage/jsp/showsaveactioninfo.jsp";
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private String response=null;
    private String result=null;
    private TableLayout tableLayout;
    int Flag=0;
    Button bt_gettable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_info);
        Thread t = new Thread(newTread);
        t.start();
    }
    Runnable newTread = new Runnable(){
        public void run(){
            response=PostUtils.getDataByGet(SHOW_URL,null,"utf8");
            if(response!=null){
                Flag=1;
                handler1.sendEmptyMessage(0x123);

            }
            else{
                result="网络故障";
                handler.sendEmptyMessage(0x123);
            }
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(LookInfo.this,result,Toast.LENGTH_SHORT).show();
        };
    };
    private Handler handler1=new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                tableLayout = (TableLayout) findViewById(R.id.tableshow);
                tableLayout.removeAllViews();
                tableLayout.setStretchAllColumns(true);
                    JSONArray data = new JSONArray(response);
                    int colume = 5;
                    int row = data.length();
                    String colume_s[] = {"valnumber", "storagelocationnum", "opaction", "optime", "valstatus"};
                    String colume_head[] = {"编号", "位置", "动作", "时间", "状态"};
                    {
                        TableRow tableRow = new TableRow(LookInfo.this);
                        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        for (int k = 0; k < 5; k++) {
                            TextView tv = new TextView(LookInfo.this);
                            ViewGroup parent = (ViewGroup) tv.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tv = new TextView(LookInfo.this);
                            tv.setText(colume_head[k]);
                            tv.setBackgroundResource(R.drawable.table_head);
                            tv.setGravity(Gravity.CENTER);
                            lp1.setMargins(5, 5, 5, 5);
                            tv.setLayoutParams(lp1);
                            tableRow.addView(tv);
                        }
                        tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                    }
                    for (int i = 0; i < data.length() + 1; i++) {
                        TableRow tableRow = new TableRow(LookInfo.this);
                        JSONObject mydata = data.getJSONObject(i);
                        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        for (int j = 0; j < colume; j++) {
                            TextView tv = new TextView(LookInfo.this);
                            ViewGroup parent = (ViewGroup) tv.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tv = new TextView(LookInfo.this);
                            if (colume_s[j].equals("optime")) {
                                String date = mydata.getString(colume_s[j]);
                                date = date.substring(0, 10);
                                tv.setText(date);
                                tv.setBackgroundResource(R.drawable.table_textview);
                            } else if (colume_s[j].equals("opaction")) {
                                if (mydata.getString(colume_s[j]).equals("T")) {
                                    tv.setText("出库");
                                    tv.setBackgroundResource(R.drawable.textview_send);
                                } else if (mydata.getString(colume_s[j]).equals("S")) {
                                    tv.setText("入库");
                                    tv.setBackgroundResource(R.drawable.textview_save);
                                } else if (mydata.getString(colume_s[j]).equals("X")) {
                                    tv.setText("修改");
                                    tv.setBackgroundResource(R.drawable.textview_change);
                                }
                            } else {
                                tv.setText(mydata.getString(colume_s[j]));
                                tv.setBackgroundResource(R.drawable.table_textview);
                            }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Looper.loop();
        };
    };
}
