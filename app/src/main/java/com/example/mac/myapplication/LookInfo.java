package com.example.mac.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
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

public class LookInfo extends Activity {
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
        Button bt=(Button)findViewById(R.id.gettable);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    tableLayout = (TableLayout) findViewById(R.id.tableshow);
                    tableLayout.removeAllViews();
                    tableLayout.setStretchAllColumns(true);
                    try {
                        JSONArray data = new JSONArray(response);
                        int colume = 5;
                        int row = data.length();
                        String colume_s[]={"valnumber","storagelocationnum","opaction","optime","valstatus"};

                        for (int i = 0; i < data.length(); i++) {
                            TableRow tableRow = new TableRow(LookInfo.this);

                            JSONObject mydata = data.getJSONObject(i);
                            for (int j = 0; j < colume; j++) {
                                TextView tv = new TextView(LookInfo.this);
                                ViewGroup parent = (ViewGroup)tv.getParent();
                                if (parent != null) {
                                    parent.removeAllViews();
                                }
                                tv = new TextView(LookInfo.this);
                                tv.setText(mydata.getString(colume_s[j]));
                                tableRow.addView(tv);
                            }
                            ViewGroup parent = (ViewGroup)tableRow.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        });
    }

    Runnable newTread = new Runnable(){
        public void run(){
            String  SHOW_URL= "http://172.20.10.3:8080/valmanage/jsp/showsaveactioninfo.jsp";
            response=PostUtils.getDataByGet(SHOW_URL,null,"utf8");
            if(response!=null){
                Flag=1;
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
}
