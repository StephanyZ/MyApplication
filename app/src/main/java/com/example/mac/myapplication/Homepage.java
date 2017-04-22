package com.example.mac.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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
    }


    @Override
    public void onClick(View v) {

    }
}
