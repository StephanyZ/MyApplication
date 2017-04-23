package com.example.mac.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class Homepage extends Activity implements View.OnClickListener{
    Button lookbtn,addbtn,outbtn,searchbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homepage);
        lookbtn=(Button)findViewById(R.id.lookbutton);
        addbtn=(Button)findViewById(R.id.addbutton);
        outbtn=(Button) findViewById(R.id.outbutton);
        searchbtn=(Button)findViewById(R.id.searchbutton);
        lookbtn.setOnClickListener(this);
        addbtn.setOnClickListener(this);
        outbtn.setOnClickListener(this);
        searchbtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lookbutton){
            startActivity(new Intent().setClass(Homepage.this,LookInfo.class));
        }
        if(v.getId()==R.id.addbutton){
            startActivity(new Intent().setClass(Homepage.this,AddInfo.class));
        }
        if(v.getId()==R.id.outbutton){

        }
        if(v.getId()==R.id.searchbutton){

        }
    }


}
