package com.example.mac.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


public class AddInfo extends Activity {
    Spinner photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        photo=(Spinner)findViewById(R.id.photo);
        Button bt=(Button)findViewById(R.id.spinnerbtn);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] photos=getResources().getStringArray(R.array.photoitem);
                int index=photo.getSelectedItemPosition();
                Toast.makeText(AddInfo.this,photos[index],Toast.LENGTH_SHORT).show();

            }
        });
    }



}
