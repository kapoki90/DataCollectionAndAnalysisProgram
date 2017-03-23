package com.example.administrator.DataCollectionAndAnalysis;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MakeList extends Activity {

    String path = "/sdcard/myproject2/";
    static final String FILE_NAME = "exam.txt";

    EditText listNumber,groupName;
    Button okBtn,okBtn2,backBtn;
    EditText[] editTexts;
    File fileDir = new File(path);
    File file = new File(path + FILE_NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_list);

        if(!fileDir.exists()){
            // 디렉토리가 존재하지 않으면 디렉토리 생성
            fileDir.mkdirs();
        }

        okBtn = (Button)findViewById(R.id.okBtn);
        okBtn2 = (Button)findViewById(R.id.okBtn2);
        backBtn = (Button)findViewById(R.id.backBtn);
        listNumber = (EditText)findViewById(R.id.listNumber);
        groupName = (EditText)findViewById(R.id.groupName);
    }

    public void MakeList(View view){
/*
        int listNum = Integer.parseInt(listNumber.getText().toString());

        PrintWriter pw = null;
        //컬럼 개수를 저장
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.flush(); //파일내용 초기화
            fos.close();

            pw = new PrintWriter(new FileWriter(file, true));
            String str = String.valueOf(listNum)+"#";
            pw.flush();
            pw.write(str);
        } catch (Exception e) {
            Log.e("File", "에러=" + e);
        }finally{
            if(pw != null) pw.close();
        }
*/
        //get list count
        int listNum = Integer.parseInt(listNumber.getText().toString());
        //make list Array
        editTexts = new EditText[listNum];

        //1~10범위
        if(listNum>0&&listNum<10) {

            for (int i = 0; i < editTexts.length; i++) {
                editTexts[i] = (EditText) findViewById(R.id.et1 + i);//editTexts읽어온다.
            }

            for (int i = 0; i < editTexts.length; i++) {
                editTexts[i].setVisibility(View.VISIBLE);//입력한 수만큼 보이도록한다.
            }
/*            for (int i = editTexts.length+1 ; i < 4; i++) {
                editTexts[i].setVisibility(View.INVISIBLE);//나머지는 안보이게
            }*/
            okBtn2.setVisibility(View.VISIBLE);

        }else{
/*
            for (int i = 0; i < editTexts.length; i++) {
                editTexts[i].setVisibility(View.INVISIBLE);//모두안보이게
            }
            okBtn2.setVisibility(View.INVISIBLE);*/

            Toast.makeText(this,"please input 1 ~ 9",Toast.LENGTH_SHORT).show();
        }
    }

    public void SendList(View view) {
        ArrayList<String> arrayList = new ArrayList<>();
        int listNum = Integer.parseInt(listNumber.getText().toString());
        String myGroupName = groupName.getText().toString();
        //Intent intent = new Intent(this,MainActivity.class);
        //입력한값을 Arraylist에 넣는다.

        for(int i = 0 ; i < listNum ; i ++) {
            String str = editTexts[i].getText().toString()+"@"+myGroupName+"#";
            PrintWriter pw = null;
            try {
                arrayList.add(i, editTexts[i].getText().toString());
                pw = new PrintWriter(new FileWriter(file, true));
                pw.write(str); //내용을 쓴다.
            }catch (Exception e) {
                Log.e("File", "Error=" + e);
            }finally{
                if(pw != null) pw.close();
            }
            finish();
        }
        //intent.putExtra("listNum",listNum);
        //intent.putStringArrayListExtra("arrayList",arrayList);
        //startActivity(intent);
    }

    public void goBack(View view){
        finish();
    }

}
