package com.example.administrator.DataCollectionAndAnalysis;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
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

public class LineViewDb extends AppCompatActivity {

    ArrayList<String> arrayList = new ArrayList<>();
    String myJSON;
    String[] groupnames;
    JSONArray allList = null;
    JSONArray selectByGroupName = null;
    int length=0;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUPNAME = "groupname";


    private GraphicalView mChartView;

    Spinner LineSpinnerDb1;

    ArrayList<String> nameList,countList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_view_db);
        LineSpinnerDb1 = (Spinner) findViewById(R.id.LineSpinnerDb1);
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
                Log.d("groupnames",temp);
            }

            // 2. 어뎁터 작성
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupnames);
            // 3. 어뎁터 결합
            LineSpinnerDb1.setAdapter(adapter);
            // 4. 리스너 등록
            LineSpinnerDb1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                loading = ProgressDialog.show(LineViewDb.this, "Please Wait", null, true, true);
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
                    drawChart();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(GroupName);
    }

    private void drawChart(){

        int[] x = { 1,2,3,4,5,6,7,8,9,10,11 };

        //int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800};

        //int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400 };


        // Creating an  XYSeries for Income

        XYSeries incomeSeries = new XYSeries("Group A");


        // Creating an  XYSeries for Expense

        //XYSeries expenseSeries = new XYSeries("지출");


        // Adding data to Income and Expense Series

        //incomeSeries.clear();
        for(int i=0;i<countList.size();i++){

            incomeSeries.add(x[i], Integer.parseInt(countList.get(i)));
            //incomeSeries.add(x[i], income[i]);

            //expenseSeries.add(x[i],expense[i]);

        }


        // Creating a dataset to hold each series

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();


        // Adding Income Series to the dataset

        dataset.addSeries(incomeSeries);


        // Adding Expense Series to dataset

        //dataset.addSeries(expenseSeries);


        // Creating XYSeriesRenderer to customize incomeSeries

        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();

        incomeRenderer.setColor(Color.RED);

        incomeRenderer.setPointStyle(PointStyle.CIRCLE);

        incomeRenderer.setFillPoints(true);

        incomeRenderer.setLineWidth(4);

        incomeRenderer.setDisplayChartValues(true);

        incomeRenderer.setChartValuesTextSize(30);

        incomeRenderer.setChartValuesSpacing(30);


        // Creating XYSeriesRenderer to customize expenseSeries

//        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
//
//        expenseRenderer.setColor(Color.RED);
//
//        expenseRenderer.setPointStyle(PointStyle.CIRCLE);
//
//        expenseRenderer.setFillPoints(true);
//
//        expenseRenderer.setLineWidth(2);
//
//        expenseRenderer.setDisplayChartValues(true);


        // Creating a XYMultipleSeriesRenderer to customize the whole chart

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setYAxisMin(0);

        multiRenderer.setYAxisMax(max(countList));

        multiRenderer.setXLabels(0);

        multiRenderer.setChartTitle("Line Chart");

        multiRenderer.setXTitle(" ");

        multiRenderer.setYTitle("Number of count");

        multiRenderer.setZoomButtonsVisible(true);

        for(int i=0;i< nameList.size();i++){

            //   multiRenderer.addXTextLabel(i+1, mMonth[i]);
            multiRenderer.addXTextLabel(i+1, nameList.get(i));
        }
        multiRenderer.setLabelsTextSize(20);

        // Adding incomeRenderer and expenseRenderer to multipleRenderer

        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer

        // should be same

        multiRenderer.addSeriesRenderer(incomeRenderer);

        //multiRenderer.addSeriesRenderer(expenseRenderer);


        // Creating an intent to plot line chart using dataset and multipleRenderer

        // Intent intent = ChartFactory.getLineChartIntent(getBaseContext(), dataset, multiRenderer);



        // Start Activity

        //startActivity(intent);


//        if (mChartView == null) {

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart_lineDb);

        layout.removeAllViews();

        mChartView = ChartFactory.getLineChartView(this, dataset, multiRenderer);

        multiRenderer.setClickEnabled(true);

        multiRenderer.setSelectableBuffer(10);

        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,

                LinearLayout.LayoutParams.FILL_PARENT));

//        } else {

        //mChartView.repaint();
//
//        }

    }

    public static int max(ArrayList<String> n) {
        int max = Integer.parseInt(n.get(0));

        for (int i = 1; i < n.size(); i++)
            if (Integer.parseInt(n.get(i)) > max) max = Integer.parseInt(n.get(i));

        return max;
    }
    public void goBack(View view){
        finish();
    }
}