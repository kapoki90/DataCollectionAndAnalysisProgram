package com.example.administrator.DataCollectionAndAnalysis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class DbActivity extends Activity {
    ArrayList<String> arrayList = new ArrayList<>();
//    Button[] btns = new Button[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_activity);
//        for (int k = 0; k < 8; k++) {
//            btns[k] = (Button) findViewById(R.id.myBtnDb1 + k);//Button 등록.
//        }
    }

    public void viewActivity2(View view){
        //Intent intent = getIntent();
        //int listNum = intent.getIntExtra("listNum",0);
        //arrayList = intent.getStringArrayListExtra("arrayList");

        Button button = (Button)view;
        String title = button.getText().toString();


        int id = view.getId();
        switch (id){
            case R.id.btnDb1:
                Intent intent1 = new Intent(this,MakeListinDb.class);
                startActivity(intent1);
                break;
            case R.id.btnDb2:
                Intent intent2 = new Intent(this,ButtonListDb.class);
                startActivity(intent2);
                break;
            case R.id.btnDb3:
                Intent intent3 = new Intent(this,TableViewDb.class);
                startActivity(intent3);
                break;
            case R.id.btnDb4:
                Intent intent4 = new Intent(this,BarViewDb.class);
                startActivity(intent4);
                break;
            case R.id.btnDb5:
                Intent intent5 = new Intent(this,PieViewDb.class);
                startActivity(intent5);
                break;
            case R.id.btnDb6:
                Intent intent6 = new Intent(this,LineViewDb.class);
                startActivity(intent6);
                break;
            case R.id.btnDb7:
                Intent intent7 = new Intent(this,Synchronization.class);
                startActivity(intent7);
                break;
//            case R.id.btnDb8:
//                Intent intent8 = new Intent(this,MainActivity.class);
//                startActivity(intent8);
//                break;
        }

    }
    public void goBack(View view){
        finish();
    }
}
