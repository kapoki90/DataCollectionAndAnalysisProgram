package com.example.administrator.DataCollectionAndAnalysis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by kapok on 2017-03-17.
 */
public class Synchronization extends Activity {
    List<HashMap<String, String>> list = new ArrayList<>();    // 데이터의 모양
    //ArrayList<String> arrayList = new ArrayList<>();
    String[] AllnameArray;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    int[] countArray;//각 행의 카운트를 저장할 배열
    String path = "/sdcard/myproject2/";

    static final String FILE_NAME = "exam.txt";
    static final String FILE_NAME2 = "countInfo.txt";

    File file = new File(path + FILE_NAME);
    File file2 = new File(path + FILE_NAME2);

    JSONArray allList = null;
    String myJSON;
    String[] listInfoArray;
    ArrayList<String> newGroupArr, listInfoArray2;
    int groupCnt, nameCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);

        //txt->db동기화
        //그룹정보를 읽어온다.
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String listInfo = new String(buffer);
            listInfoArray = listInfo.split("#");
            fis.close();

        } catch (Exception e) {
            Log.e("File", "에러=" + e);
        }

        // @ 기준 뒤를 추출하여 그룹 배열에 저장.
        ArrayList<String> groupArr = new ArrayList<>();
        ArrayList<String> nameArr = new ArrayList<>();
        for (int i = 0; i < listInfoArray.length; i++) {
            // @를 기준으로 문자열을 추출할 것이다.
            String mystr = listInfoArray[i];
            // 먼저 @ 의 인덱스를 찾는다
            int idx = mystr.indexOf("@");
            // 뒷부분을 추출
            // 아래 substring은 @ 바로 뒷부분인 n부터 추출된다.
            String groupName = mystr.substring(idx + 1);
            String myName = mystr.substring(0, idx);
            groupArr.add(groupName);
            nameArr.add(myName);
        }
        // HashSet 데이터 형태로 생성되면서 중복 제거됨
        LinkedHashSet hs = new LinkedHashSet(groupArr);

        // ArrayList 형태로 다시 생성
        newGroupArr = new ArrayList<String>(hs);

        nameCnt = nameArr.size();
        groupCnt = newGroupArr.size();

        //Log.d("newGroupArr.size()", String.valueOf(newGroupArr.size()));

        //그룹정보를 읽어온다.


        try {
            //txt파일로 읽어와 ","로 잘라 저장
            FileInputStream fis = new FileInputStream(file2);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String str = new String(buffer);
            AllnameArray = str.split(",");

            //카운트가 저장될 배열
            countArray = new int[nameCnt];// 카운트를 저장하기위해 0으로 초기화

            //모든 리스트정보로 비교하여 카운트 계산, 배열에 넣는다.
            for (int i = 0; i < AllnameArray.length; i++) {
                for (int j = 0; j < nameCnt; j++) { //사용자가입력한 string과 비교
                    if (AllnameArray[i].equals(nameArr.get(j))) {
                        countArray[j] += 1; //카운트 증가
                    }
                }
            }

            //그룹의 요소개수를 배열에 저장한다.
            int[] GroupSize = new int[groupArr.size()];

            for (int i = 0; i < listInfoArray.length; i++) {

                for (int p = 0; p < newGroupArr.size(); p++) {
                    String selectedGroupName = newGroupArr.get(p);
                    //Log.d("newGroupArr", newGroupArr.get(p));
                    if (listInfoArray[i].contains(selectedGroupName)) {
                        GroupSize[p] += 1;
                    }
                }
            }

//*******************고쳐야할부분**************2번동기화시 문제
            int start = 0, end = 0;
//            Log.d("newNameList",String.valueOf(newNameList.size()));
//            Log.d("countArray",String.valueOf(countArray.length));
            for (int g = 0; g < groupCnt; g++) {
                //Log.d("g",String.valueOf(newGroupArr.get(g)));
                //Log.d("g",String.valueOf(g));
                end += GroupSize[g];
                for (int h = start; h < end; h++) {
                    //Log.d("h",String.valueOf(h));
                    start = end;
                    //Log.d("g",String.valueOf(newNameList.get(h)));
                    insertToDatabase(nameArr.get(h), String.valueOf(countArray[h]), newGroupArr.get(g));
                }
            }


        } catch (Exception e) {
            Log.e("File", "에러=" + e);
        }
        getData("http://192.168.0.3/exam/getdata3.php");//db->txt 동기화
    }

    //GroupArr,GroupSize,newNameList,countArray



    private void insertToDatabase(String name, String count, String groupname) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Synchronization.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String name = (String) params[0];
                    String count = (String) params[1];
                    String groupname = (String) params[2];

                    String link = "http://192.168.0.3/exam/synchronization.php";
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("count", "UTF-8") + "=" + URLEncoder.encode(count, "UTF-8");
                    data += "&" + URLEncoder.encode("groupname", "UTF-8") + "=" + URLEncoder.encode(groupname, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

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
        }
        InsertData task = new InsertData();
        task.execute(name, count, groupname);
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
        String listInfoString = new String();
        String countInfoString = new String();
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            allList = jsonObj.getJSONArray("result");//'result'배열을 얻어온다.
            //Log.d("allList.length()", String.valueOf(allList.length()));
            for (int i = 0; i < allList.length(); i++) {
                JSONObject c = allList.getJSONObject(i);
                String name = c.getString("name");
                int count = Integer.parseInt(c.getString("count"));
                String groupname = c.getString("groupname");
                //Log.d("groupname", groupname);
                listInfoString+=name + "@" + groupname + "#";
                for(int cnt=0;cnt<count;cnt++){
                    countInfoString+=name+",";
                }
            }
            Log.d("listInfoString",listInfoString);
            Log.d("countInfoString",countInfoString);

            PrintWriter pw = null;
            PrintWriter pw2 = null;
            try {
                pw = new PrintWriter(new FileWriter(file));
                pw.write(listInfoString); //내용을 쓴다.

                pw2 = new PrintWriter(new FileWriter(file2));
                pw2.write(countInfoString);
            } catch (Exception e) {
                Log.e("File", "에러=" + e);
            } finally {
                if (pw != null) pw.close();
                if (pw2 != null) pw2.close();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

/*    protected void PrintWriter2() {
        PrintWriter pw2 = null;
        try {
            pw2 = new PrintWriter(new FileWriter(file2));
            pw2.write(countInfoString);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw2 != null) pw2.close();
        }
    }*/

    public void goBack(View view) {
        finish();
    }

}