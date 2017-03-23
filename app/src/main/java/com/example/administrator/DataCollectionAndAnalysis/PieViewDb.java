package com.example.administrator.DataCollectionAndAnalysis;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
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

public class PieViewDb extends AppCompatActivity {
    ArrayList<String> arrayList = new ArrayList<>();
    int[] countArray;//각 행의 카운트를 저장할 배열
    String myJSON;
    Spinner spinner2;
    String[] groupnames;
    JSONArray allList = null;
    JSONArray selectByGroupName = null;
    int length=0;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUPNAME = "groupname";


    ArrayList<String> nameList,countList;

    //int[] pieChartValues = {10, 10, 20, 20, 40};  //각 계열(Series)의 값

    public static final String TYPE = "type";

    //각 계열(Series)의 색상

    private static int[] COLORS = new int[]{Color.YELLOW, Color.GREEN, Color.BLUE, Color.WHITE, Color.CYAN,Color.MAGENTA,
            Color.RED,Color.DKGRAY,Color.BLACK};


    //각 계열의 타이틀

    //String[] mSeriesTitle = new String[] {"PIE1", "PIE2", "PIE3", "PIE4", "PIE5" };


    private CategorySeries mSeries = new CategorySeries("계열");

    private DefaultRenderer mRenderer = new DefaultRenderer();

    private GraphicalView mChartView;

    Spinner PieSpinner1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_view_db);
        PieSpinner1 = (Spinner) findViewById(R.id.PieSpinnerDb1);

        getData("http://192.168.0.3/exam/getdata3.php");




        mRenderer.setApplyBackgroundColor(true);

        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));

        mRenderer.setChartTitleTextSize(20);

        mRenderer.setLabelsTextSize(30);

        mRenderer.setLegendTextSize(30);

        mRenderer.setMargins(new int[]{20, 30, 15, 0});

        mRenderer.setZoomButtonsVisible(true);

        mRenderer.setStartAngle(90);

        mRenderer.setLabelsColor(Color.BLACK);

        if (mChartView == null) {

            LinearLayout layout = (LinearLayout) findViewById(R.id.chart_pieDb);

            mChartView = ChartFactory.getPieChartView(PieViewDb.this, mSeries, mRenderer);

            mRenderer.setClickEnabled(true);

            mRenderer.setSelectableBuffer(10);

            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,

                    LinearLayout.LayoutParams.FILL_PARENT));

        } else {

            mChartView.repaint();

        }



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
            PieSpinner1.setAdapter(adapter);
            // 4. 리스너 등록
            PieSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(), groupnames[position], Toast.LENGTH_SHORT).show();
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
                loading = ProgressDialog.show(PieViewDb.this, "Please Wait", null, true, true);
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

                    nameList = new ArrayList<>();
                    countList = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        JSONObject c = selectByGroupName.getJSONObject(i);
                        String name = c.getString("name");
                        String count = c.getString("count");
//                        Log.d("name",name);
//                        Log.d("count",count);
                        nameList.add(name);
                        countList.add(count);
                    }
//                    for(int a =0;a<nameList.size();a++){
//                        Log.d("nameList",String.valueOf(nameList.get(a)));
//                    }
//                    for(int a =0;a<countList.size();a++){
//                        Log.d("nameList",String.valueOf(countList.get(a)));
//                    }
                    fillPieChart();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(GroupName);
    }





    public void fillPieChart() {
        mSeries.clear();
        for (int i = 0; i < nameList.size(); i++) {

            mSeries.add(nameList.get(i) + "(count : " + (String.valueOf(countList.get(i)))+")", Integer.parseInt(countList.get(i)));


            //Chart에서 사용할 값, 색깔, 텍스트등을 DefaultRenderer객체에 설정

            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);


            mRenderer.addSeriesRenderer(renderer);


            if (mChartView != null)

                mChartView.repaint();

        }

    }
    public void goBack(View view){
        finish();
    }

}