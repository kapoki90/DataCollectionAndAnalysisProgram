package com.example.administrator.DataCollectionAndAnalysis;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
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

public class BarViewDb extends AppCompatActivity {
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

    private GraphicalView mChartView;

    Spinner BarSpinner1;
    ArrayList<String> nameList,countList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_view_db);
        BarSpinner1 = (Spinner) findViewById(R.id.BarSpinnerDb1);
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
            allList = jsonObj.getJSONArray(TAG_RESULTS);//Get 'result' Array
            for (int i = 0; i < allList.length(); i++) {
                JSONObject c = allList.getJSONObject(i);
                String groupname = c.getString(TAG_GROUPNAME);
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
            BarSpinner1.setAdapter(adapter);
            // 4. 리스너 등록
            BarSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                loading = ProgressDialog.show(BarViewDb.this, "Please Wait", null, true, true);
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


    private void drawChart() {

        //int[] x = {0, 1, 2, 3, 4, 5, 6, 7, 8};

        //int[] income = {10, 20, 30, 40, 50, 60, 70, 80, 90};

        //int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0};


        // Creating an XYSeries for Income

        XYSeries incomeSeries = new XYSeries("Group A");

        // Creating an XYSeries for Expense

        //XYSeries expenseSeries = new XYSeries("지출");

        // Adding data to Income and Expense Series

        for (int i = 0; i < countList.size(); i++) {

            incomeSeries.add(i, Integer.parseInt(countList.get(i)));

            //expenseSeries.add(i, expense[i]);

        }


        // Creating a dataset to hold each series

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding Income Series to the dataset

        dataset.addSeries(incomeSeries);

        // Adding Expense Series to dataset

        //dataset.addSeries(expenseSeries);


        // Creating XYSeriesRenderer to customize incomeSeries

        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();

        incomeRenderer.setColor(Color.RED); //color of the graph set to cyan

        incomeRenderer.setFillPoints(true);

        incomeRenderer.setLineWidth(2);

        incomeRenderer.setDisplayChartValues(true);

        incomeRenderer.setDisplayChartValuesDistance(10); //setting chart value distance


        // Creating XYSeriesRenderer to customize expenseSeries

//        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
//
//        expenseRenderer.setColor(Color.RED);
//
//        expenseRenderer.setFillPoints(true);
//
//        expenseRenderer.setLineWidth(2);
//
//        expenseRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);

        multiRenderer.setXLabels(0);

        multiRenderer.setChartTitle("Bar Chart");

        multiRenderer.setXTitle(" ");

        multiRenderer.setYTitle("Number of count");


        /***

         * Customizing graphs

         */

        //setting text size of the title

        multiRenderer.setChartTitleTextSize(28);

        multiRenderer.setAxisTitleTextSize(24);

        //setting text size of the graph lable

        multiRenderer.setLabelsTextSize(24);

        //setting zoom buttons visiblity

        multiRenderer.setZoomButtonsVisible(false);

        //setting pan enablity which uses graph to move on both axis

        multiRenderer.setPanEnabled(false, false);

        //setting click false on graph

        multiRenderer.setClickEnabled(false);

        //setting zoom to false on both axis

        multiRenderer.setZoomEnabled(false, false);

        //setting lines to display on y axis

        multiRenderer.setShowGridY(false);

        //setting lines to display on x axis

        multiRenderer.setShowGridX(false);

        //setting legend to fit the screen size

        multiRenderer.setFitLegend(true);

        //setting displaying line on grid

        multiRenderer.setShowGrid(false);

        //setting zoom to false

        multiRenderer.setZoomEnabled(false);

        //setting external zoom functions to false

        multiRenderer.setExternalZoomEnabled(false);

        //setting displaying lines on graph to be formatted(like using graphics)

        multiRenderer.setAntialiasing(true);

        //setting to in scroll to false

        multiRenderer.setInScroll(false);

        //setting to set legend height of the graph

        multiRenderer.setLegendHeight(30);

        //setting x axis label align

        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);

        //setting y axis label to align

        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);

        //setting text style

        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);

        //setting no of values to display in y axis

        multiRenderer.setYLabels(10);

        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.

        // if you use dynamic values then get the max y value and set here

        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(max(countList));

        //setting used to move the graph on xaxiz to .5 to the right

        //multiRenderer.setXAxisMin(-0.5);
        multiRenderer.setXAxisMin(-2.5);
        //setting max values to be display in x axis

        multiRenderer.setXAxisMax(9);

        //setting bar size or space between two bars default=0.5

        multiRenderer.setBarSpacing(0.5);

        //Setting background color of the graph to transparent

        multiRenderer.setBackgroundColor(Color.TRANSPARENT);

        //Setting margin color of the graph to transparent

        multiRenderer.setMarginsColor(Color.BLUE);

        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right

        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        for (int i = 0; i < nameList.size(); i++) {

            multiRenderer.addXTextLabel(i, nameList.get(i));

        }

        // Adding incomeRenderer and expenseRenderer to multipleRenderer

        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer

        // should be same

        multiRenderer.addSeriesRenderer(incomeRenderer);

        //multiRenderer.addSeriesRenderer(expenseRenderer);

        //this part is used to display graph on the xml

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart_barDb);

        //remove any views before u paint the chart

        layout.removeAllViews();

        //drawing bar chart

        mChartView = ChartFactory.getBarChartView(this, dataset, multiRenderer, BarChart.Type.DEFAULT);


        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,

                LinearLayout.LayoutParams.FILL_PARENT));

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
