package com.example.administrator.DataCollectionAndAnalysis;

import android.graphics.Color;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class LineView extends AppCompatActivity {


    // 막대그래프의 가로축

/*    private String[] mMonth = new String[] {

            "Jan", "Feb" , "Mar", "Apr", "May", "Jun",

            "Jul", "Aug" , "Sep", "Oct", "Nov", "Dec"

    };*/
    ArrayList<String> newGroupArr;
    int groupCnt;

    String path = "/sdcard/myproject2/";
    static final String FILE_NAME = "exam.txt";
    static final String FILE_NAME2 = "countInfo.txt";
    String[] listInfoArray;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    ArrayList<String> selectedGroupList;
    File file = new File(path + FILE_NAME);
    File file2 = new File(path + FILE_NAME2);
    String[] array;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    int[] countArray;//각 행의 카운트를 저장할 배열
    private GraphicalView mChartView;
    Spinner LineSpinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_view);
        LineSpinner1 = (Spinner) findViewById(R.id.LineSpinner1);

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

        for (int i = 0; i < listInfoArray.length; i++) {
            // @를 기준으로 문자열을 추출할 것이다.
            String mystr = listInfoArray[i];
            // 먼저 @ 의 인덱스를 찾는다
            int idx = mystr.indexOf("@");
            // 뒷부분을 추출
            // 아래 substring은 @ 바로 뒷부분인 n부터 추출된다.
            String groupName = mystr.substring(idx + 1);
            groupArr.add(groupName);
        }

        // HashSet 데이터 형태로 생성되면서 중복 제거됨
        LinkedHashSet hs = new LinkedHashSet(groupArr);
        // ArrayList 형태로 다시 생성
        newGroupArr = new ArrayList<String>(hs);
        groupCnt = newGroupArr.size();
        //Log.d("newGroupArr.size()", String.valueOf(newGroupArr.size()));

        // 2. 어뎁터 작성
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, newGroupArr);
        // 3. 어뎁터 결합
        LineSpinner1.setAdapter(adapter);
        // 4. 리스너 등록
        LineSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGroupName = newGroupArr.get(position);
                selectedGroupList = new ArrayList<String>();
                for (int i = 0; i < listInfoArray.length; i++) {
                    if (listInfoArray[i].contains(selectedGroupName)) {
                        String arrElement = listInfoArray[i];
                        int idx = arrElement.indexOf("@");
                        String myName = arrElement.substring(0, idx);
                        //Log.d("myName", myName);
                        selectedGroupList.add(myName);
                        //앞부분 추출
                    }
                }
                try {
                    //txt파일로 읽어와 ","로 잘라 저장
                    FileInputStream fis = new FileInputStream(file2);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    String str = new String(buffer);
                    array = str.split(",");

                    countArray = new int[selectedGroupList.size()];// 카운트를 저장하기위해 0으로 초기화

                    for(int i=0 ; i<array.length;i++){
                        for(int j=0 ; j < selectedGroupList.size() ; j++) { //사용자가입력한 string과 비교
                            if (array[i].equals(selectedGroupList.get(j))) {
                                countArray[j] += 1; //카운트 증가
                            }
                        }
                    }
                    drawChart();
                    fis.close();
                } catch (Exception e) {
                    Log.e("File", "에러=" + e);
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택하지 않은 경우
            }
        });

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
        for(int i=0;i<countArray.length;i++){

            incomeSeries.add(x[i], countArray[i]);
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

        multiRenderer.setYAxisMax(max(countArray));

        multiRenderer.setXLabels(0);

        multiRenderer.setChartTitle("Line Chart");

        multiRenderer.setXTitle(" ");

        multiRenderer.setYTitle("Number of count");

        multiRenderer.setZoomButtonsVisible(true);

        for(int i=0;i< selectedGroupList.size();i++){

         //   multiRenderer.addXTextLabel(i+1, mMonth[i]);
            multiRenderer.addXTextLabel(i+1, selectedGroupList.get(i));
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

            LinearLayout layout = (LinearLayout) findViewById(R.id.chart_line);

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

    public static int max(int n[]) {
        int max = n[0];

        for (int i = 1; i < n.length; i++)
            if (n[i] > max) max = n[i];

        return max;
    }

    public void goBack(View view){
        finish();
    }
}