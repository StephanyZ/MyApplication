package com.example.mac.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.acker.simplezxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mac.myapplication.PostUtils.localhost;

public class OutVal extends Activity {
    String VALOUT_URL = "http://"+localhost+":8080/valmanage/jsp/androidpreparetosave.jsp";
    String GETVALOUTINFO_URL = "http://"+localhost+":8080/valmanage/jsp/show.jsp";
    //String VALOUT_URL = "http://192.168.1.100:8080/valmanage/jsp/androidpreparetosave.jsp";
    //String GETVALOUTINFO_URL = "http://192.168.1.100:8080/valmanage/jsp/show.jsp";
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    String Account = null;
    String mark=null;
    String result = null;
    String response = null;
    String QRnumber = null;
    CheckBox checkall;
    private TableLayout tableLayout;
    CheckBox checkBox[] = new CheckBox[50];

    JSONArray selectcheck = new JSONArray();
    private static final int REQ_CODE_PERMISSION = 0x1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_val);
        Bundle bundle = this.getIntent().getExtras();
        String account = bundle.getString("account");
        checkall=new CheckBox(OutVal.this);
        Account = account;
        Button bt = (Button) findViewById(R.id.outvalbtn);
        Button bt1 = (Button) findViewById(R.id.startoutbtn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(OutVal.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(OutVal.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();

                }

            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("ButtonJson", selectcheck.toString());
                Log.d("length", selectcheck.length() + "");
                Thread t = new Thread(newTread);
                t.start();

            }

        });

    }
    Runnable newTread = new Runnable() {
        public void run() {
            Map<String, String> addmessage = new HashMap<String, String>();
            addmessage.put("valoutinfo", selectcheck.toString());
            addmessage.put("option", "preoutvalbyqrcode");
            addmessage.put("manindex",Account);
            addmessage.put("mark",mark);
            response = PostUtils.getDataByPost(VALOUT_URL, addmessage, "utf8");
            Log.d("result", response);
            if (response.equals("sucess")) {
                result="预出库成功";
                Log.d("result", response);
                handler.sendEmptyMessage(0x123);
                handler2.sendEmptyMessage(0x123);//Flag=1;
            } else {
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
            Map<String, String> addmessage = new HashMap<String, String>();
            addmessage.put("valorgroupnumber", QRnumber);
            addmessage.put("option", "androidshowvaloutinfo");
            response = PostUtils.getDataByPost(GETVALOUTINFO_URL, addmessage, "utf8");
            if (response != null) {
                Log.d("result", response);
                handler1.sendEmptyMessage(0x123);      //Flag=1;
            } else {
                result = "网络故障";
                handler.sendEmptyMessage(0x123);
            }
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(OutVal.this, result, Toast.LENGTH_SHORT).show();
        };
    };
    private Handler handler1 = new Handler() {

        public void handleMessage(android.os.Message msg) {
            try {
                tableLayout = (TableLayout) findViewById(R.id.outinfoshow);
                tableLayout.removeAllViews();
                tableLayout.setStretchAllColumns(true);
                final JSONArray valoutinfo = new JSONArray(response);
                String colume_head[] = {"全选", "编号", "委托单号", "存储位置", "状态"};
                String colume_s[] = {"index", "valnumber", "acceptno", "location", "valstatus"};
                {
                    TableRow tableRow = new TableRow(OutVal.this);
                    TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    for (int k = 0; k < colume_head.length; k++) {
                        if (k == 0) {
                            checkall= new CheckBox(OutVal.this);
                            JSONObject ob = new JSONObject();
                            checkall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
                                    if (ischecked) {
                                        for (int jj = 0; jj < valoutinfo.length(); jj++) {
                                            checkBox[jj].setChecked(true);
                                        }
                                    } else {
                                        for (int jj = 0; jj < valoutinfo.length(); jj++) {
                                            checkBox[jj].setChecked(false);
                                        }
                                    }
                                }
                            });
                            checkall.setLayoutParams(lp1);
                            tableRow.addView(checkall);
                        } else {
                            TextView tv = new TextView(OutVal.this);
                            ViewGroup parent = (ViewGroup) tv.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                            }
                            tv = new TextView(OutVal.this);
                            tv.setText(colume_head[k]);
                            tv.setBackgroundResource(R.drawable.table_textview);
                            tv.setGravity(Gravity.CENTER);
                            lp1.setMargins(5, 5, 5, 5);
                            tv.setLayoutParams(lp1);
                            tableRow.addView(tv);
                        }
                    }
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                }
                for (int i = 0; i < valoutinfo.length(); i++) {
                    TableRow tableRow = new TableRow(OutVal.this);
                    TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    JSONObject val = valoutinfo.getJSONObject(i);
                    checkBox[i] = new CheckBox(OutVal.this);
                    final int kk = i;
                    JSONObject ob = new JSONObject();
                    ob.put("cbvalnumber", val.getString("valnumber"));
                    mark=val.getString("mark");
                    checkBox[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
                            if (ischecked) {
                                try {
                                    selectcheck.getJSONObject(kk).put("ischecked", true);
                                    Log.d("Json", selectcheck.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    selectcheck.getJSONObject(kk).put("ischecked", false);
                                    Log.d("Json", selectcheck.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    ob.put("ischecked", false);
                    selectcheck.put(i, ob);
                    checkBox[i].setGravity(Gravity.CENTER);
                    lp1.setMargins(5, 5, 5, 5);
                    checkBox[i].setLayoutParams(lp1);
                    tableRow.addView(checkBox[i]);
                    for (int j = 1; j < colume_s.length; j++) {
                        TextView tv = new TextView(OutVal.this);
                        ViewGroup parent = (ViewGroup) tv.getParent();
                        if (parent != null) {
                            parent.removeAllViews();
                        }
                        tv = new TextView(OutVal.this);
                        tv.setText(val.getString(colume_s[j]));
                        tv.setBackgroundResource(R.drawable.table_textview);
                        tv.setGravity(Gravity.CENTER);
                        lp1.setMargins(5, 5, 5, 5);
                        tv.setLayoutParams(lp1);
                        tableRow.addView(tv);
                    }
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Looper.loop();
        };
    };

    private Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent it=new Intent().setClass(OutVal.this,OutVal.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        String number = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        QRnumber = number;
                        Thread t = new Thread(newTread1);
                        t.start();
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                        }
                        break;
                }
                break;
        }
    }

}
