package com.example.administrator.DataCollectionAndAnalysis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by kapok on 2017-03-16.
 */

public class TableViewDb extends Activity {
    ArrayList<String> arrayList = new ArrayList<>();
    String myJSON;
    Spinner spinner2;
    String[] groupnames;
    JSONArray allList = null;
    JSONArray selectByGroupName = null;
    int length=0;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUPNAME = "groupname";
    //static final String FILE_NAME = "exam.txt";
    //static final String FILE_NAME2 = "listInfo.txt";

    //File file = new File(path + FILE_NAME);
    //File file2 = new File(path + FILE_NAME2);
    String[] listInfoArray;

    //EditText output;
    ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_view_db);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        myListView = (ListView)findViewById(R.id.myListViewDb);
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
            // HashSet 데이터 형태로 생성되면서 중복 제거됨
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
            spinner2.setAdapter(adapter);
            // 4. 리스너 등록
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), groupnames[position], Toast.LENGTH_SHORT).show();
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
                loading = ProgressDialog.show(TableViewDb.this, "Please Wait", null, true, true);
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
                List<HashMap<String, String>> list = new ArrayList<>();	// 데이터의 모양
                loading.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    selectByGroupName = jsonObj.getJSONArray(TAG_RESULTS);
                    length = selectByGroupName.length();

                    for (int i = 0; i < length; i++) {
                        JSONObject c = selectByGroupName.getJSONObject(i);
                        String name = c.getString("name");
                        String count = c.getString("count");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("listName", name);
                        map.put("count", count);
                        list.add(map);		// 리스트에 추가
                    }

                    // 데이터 두 개 짜리를 붙이려면 SimpleAdapter를 이용한다.
                    SimpleAdapter adapter = new SimpleAdapter(TableViewDb.this, list, android.R.layout.simple_list_item_2,
                            new String[] {"listName", "count"}, new int[] {android.R.id.text1, android.R.id.text2});
                    myListView.setAdapter(adapter);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(GroupName);
    }

    public void goBack(View view){
        finish();
    }

}