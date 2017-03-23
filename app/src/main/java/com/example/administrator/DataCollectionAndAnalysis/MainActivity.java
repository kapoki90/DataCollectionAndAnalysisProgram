package com.example.administrator.DataCollectionAndAnalysis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> arrayList = new ArrayList<>();
    //Button btn8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //btn8 = (Button)findViewById(R.id.btn8);
    }

    public void viewActivity(View view){
        //Intent intent = getIntent();
        //int listNum = intent.getIntExtra("listNum",0);
        //arrayList = intent.getStringArrayListExtra("arrayList");

        Button button = (Button)view;
        String title = button.getText().toString();


        int id = view.getId();
        switch (id){
            case R.id.btn1:
                Intent intent1 = new Intent(this,MakeList.class);
                startActivity(intent1);
                break;
            case R.id.btn2:
                Intent intent2 = new Intent(this,ButtonList.class);
//                intent2.putExtra("listNum",listNum);
//                intent2.putStringArrayListExtra("arrayList",arrayList);
                startActivity(intent2);
                break;
            case R.id.btn3:
                Intent intent3 = new Intent(this,TableVIew.class);
                startActivity(intent3);
                break;
            case R.id.btn4:
                Intent intent4 = new Intent(this,BarView.class);
                startActivity(intent4);
                break;
            case R.id.btn5:
                Intent intent5 = new Intent(this,PieView.class);
                startActivity(intent5);
                break;
            case R.id.btn6:
                Intent intent6 = new Intent(this,LineView.class);
                startActivity(intent6);
                break;
            case R.id.btn7:
                Intent intent7 = new Intent(this,Synchronization.class);
                startActivity(intent7);
                break;
            case R.id.btn8:
                Intent intent8 = new Intent(this,DbActivity.class);
                startActivity(intent8);
                break;
        }

    }



}
