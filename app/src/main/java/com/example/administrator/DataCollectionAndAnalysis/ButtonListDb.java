package com.example.administrator.DataCollectionAndAnalysis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;


public class ButtonListDb extends Activity {
    Button[] btns;
    EditText[] editTextses;
    Spinner spinnerDb1;
    String myJSON;
    String[] groupnames;
    ArrayList<String> arrayList = new ArrayList<>();

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUPNAME = "groupname";

    Button sendDataBtnDb,goBackBtnDb;
    JSONArray allList = null;
    JSONArray selectByGroupName = null;
    ListView list;
    int length=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttonlist_db);

        spinnerDb1 = (Spinner) findViewById(R.id.spinnerDb1);
        list = (ListView) findViewById(R.id.listView);
        sendDataBtnDb =  (Button) findViewById(R.id.sendDataBtnDb);
        goBackBtnDb =  (Button) findViewById(R.id.goBackBtnDb);
        //버튼,editText초기화
        btns = new Button[8];
        editTextses = new EditText[8];

        for(int k = 0; k<8; k++){
            btns[k] = (Button) findViewById(R.id.myBtnDb1 + k);//Button 등록.
            editTextses[k] = (EditText) findViewById(R.id.etDb1 + k);//EditText 등록.
        }

        getData("http://192.168.0.3/exam/getdata3.php");
    }

    //php에 접근해 데이터를 가져온다.
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");//테이블의 값 한행씩 읽어옴,개행문자 추가
                    }
                    //Log.d("getData", sb.toString().trim());

                    return sb.toString().trim();//읽어온데이터 공백제거,String으로 변환

                } catch (Exception e) {
                    return null;
                }
            }

            //doInBackground후 실행, doInBackground의 return 값을 파라미터로 받아 myJSON에 넣는다.
            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            allList = jsonObj.getJSONArray(TAG_RESULTS);//'result'배열을 얻어온다.
            //Log.d("allList.length()", String.valueOf(allList.length()));
            for (int i = 0; i < allList.length(); i++) {
                JSONObject c = allList.getJSONObject(i);
                String groupname = c.getString(TAG_GROUPNAME);
                //Log.d("groupname", groupname);
                arrayList.add(groupname);
            }
            // HashSet 데이터 형태로 생성되면서 중복 제거됨 순서 상관없이 섞임..
            LinkedHashSet hs = new LinkedHashSet(arrayList);
            // ArrayList 형태로 다시 생성
            ArrayList<String> newArrList = new ArrayList<String>(hs);

            groupnames = new String[newArrList.size()];
            int size = 0;
            for (String temp : newArrList) {
                groupnames[size++] = temp;
            }
            for (String temp : groupnames) {
                //Log.d("groupnames",String.valueOf(groupnames));
            }

            // 2. 어뎁터 작성
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupnames);
            // 3. 어뎁터 결합
            spinnerDb1.setAdapter(adapter);
            // 4. 리스너 등록
            spinnerDb1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    insertToDatabase(groupnames[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 아무것도 선택하지 않은 경우
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void insertToDatabase(String GroupName) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ButtonListDb.this, "Please Wait", null, true, true);
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String GroupName = params[0];

                    String link = "http://192.168.0.3/exam/select.php";

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    String data = URLEncoder.encode("groupname", "UTF-8") + "=" + URLEncoder.encode(GroupName, "UTF-8");
                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    selectByGroupName = jsonObj.getJSONArray(TAG_RESULTS);
                    length = selectByGroupName.length();

                    for (int i = 0; i < length; i++) {
                        JSONObject c = selectByGroupName.getJSONObject(i);
                        String name = c.getString("name");
                        btns[i] = (Button) findViewById(R.id.myBtnDb1 + i);//Button 등록.
                        btns[i].setText(name);
                        btns[i].setVisibility(View.VISIBLE);//입력한 수만큼 보이도록한다.
                        editTextses[i].setVisibility(View.VISIBLE);//입력한 수만큼 보이도록한다.
                    }
                    for (int i = length; i < 8; i++) {
                        btns[i].setVisibility(View.INVISIBLE);
                        editTextses[i].setVisibility(View.INVISIBLE);
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(GroupName);
    }

    public void increaseCount(View view) {
        Button b = (Button) view;
        String name = b.getText().toString();
        insertToDatabase2(name,"1");
    }

    private void insertToDatabase2(String name,String count){
        class InsertData extends AsyncTask<String, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ButtonListDb.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }
            @Override
            protected String doInBackground(String... params) {
                try{
                    String name = (String)params[0];
                    String count = (String)params[1];

                    String link = "http://192.168.0.3/exam/increaseCount.php";
                    String data  = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("count", "UTF-8") + "=" + URLEncoder.encode(count, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

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
        }
        InsertData task = new InsertData();
        task.execute(name,count);
    }

    public void SendData(View view){
        PrintWriter pw = null;
        for (int i = 0; i < length; i++) {

            if(!editTextses[i].getText().toString().isEmpty()) {
                String enteredCount = editTextses[i].getText().toString();
                String s = btns[i].getText().toString();
                insertToDatabase2(s,enteredCount);
            }
            editTextses[i].setText("");
        }
    }




    public void goBack(View view) {
        // 창을 닫는다.
        finish();
    }
}