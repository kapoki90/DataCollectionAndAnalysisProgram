package com.example.administrator.DataCollectionAndAnalysis;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MakeListinDb extends AppCompatActivity {

    EditText listNumber,groupNameDb;
    Button okBtn,okBtn2,backBtn;
    EditText[] editTexts;
    String ListNumStr;
    int listNum;
    ArrayList<String> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_list_db);

        okBtn = (Button)findViewById(R.id.okBtnDb);
        okBtn2 = (Button)findViewById(R.id.okBtnDb2);
        backBtn = (Button)findViewById(R.id.backBtndb);
        listNumber = (EditText)findViewById(R.id.listNumberDb);
        groupNameDb = (EditText)findViewById(R.id.groupNameDb);
    }

    public void MakeList(View view){
        listNum = Integer.parseInt(listNumber.getText().toString());
        //컬럼 개수를 저장
        ListNumStr = String.valueOf(listNum); //리스트개수

        editTexts = new EditText[listNum];

        //1~10범위
        if(listNum>0&&listNum<10) {

            for (int i = 0; i < editTexts.length; i++) {
                editTexts[i] = (EditText) findViewById(R.id.etdb1 + i);//editTexts읽어온다.
            }

            for (int i = 0; i < editTexts.length; i++) {
                editTexts[i].setVisibility(View.VISIBLE);//입력한 수만큼 보이도록한다.
            }
            okBtn2.setVisibility(View.VISIBLE); //ok버튼 보이게

        }else{
            Toast.makeText(this,"please input 1 ~ 9",Toast.LENGTH_SHORT).show();
        }
    }


    public void SendList(View view) {
        ArrayList<String> arrayList = new ArrayList<>();
        //int listNum = Integer.parseInt(listNumber.getText().toString());
        //Intent intent = new Intent(this,MainActivity.class);
        //입력한값을 Arraylist에 넣는다.
        //Log.d("listNumber", String.valueOf(listNum));
        String myGroupName = groupNameDb.getText().toString();
        for(int i = 0 ; i < listNum ; i ++) {
            arrayList.add(i, editTexts[i].getText().toString());
            Log.d("arrayList ", arrayList.get(i));
            //String str = editTexts[i].getText().toString();//edittext에있는 값을 차례로 저장
            insertToDatabase(arrayList.get(i),myGroupName);
        }
        finish();
    }

    public void insertToDatabase(String listName,String myGroupName){
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            //doInBackground전에 실행
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MakeListinDb.this, "Please Wait", null, true, true);
            }

            @Override
            protected String doInBackground(String... params) {
                try{
                    String listName = params[0];
                    String myGroupName = params[1];

                    //String color = (String)params[0];
                    //String address = (String)params[1];
                    //db서버 접속 ip주소
                    String link="http://192.168.0.3/exam/insertdata.php";
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    //A URL connection use for output.
                    conn.setDoOutput(true);
                    //OutputStreamWriter = bridge from character streams to byte streams
                    Log.d("listNum",String.valueOf(listNum));

                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(listName, "UTF-8");
                    data += "&" + URLEncoder.encode("count", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
                    data += "&" + URLEncoder.encode("groupname", "UTF-8") + "=" + URLEncoder.encode(myGroupName, "UTF-8");
                    wr.write(data);
                    wr.flush();

                    //read data from php
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }

            //doInBackground후 실행
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        }

        InsertData task = new InsertData();
        task.execute(listName,myGroupName);
    }

    public void goBack(View view){
        // 창을 닫는다.
        finish();
    }
}
