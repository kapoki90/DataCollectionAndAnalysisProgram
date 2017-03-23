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
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class PieView extends AppCompatActivity {
    ArrayList<String> arrayList = new ArrayList<>();//각 컬럼의 제목을 저장하는 arraylist
    String path = "/sdcard/myproject2/";
    static final String FILE_NAME = "exam.txt";
    static final String FILE_NAME2 = "countInfo.txt";
    String[] listInfoArray;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    File file = new File(path + FILE_NAME);
    File file2 = new File(path + FILE_NAME2);
    String[] array;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    int[] countArray;//각 행의 카운트를 저장할 배열
    int listNum = 0;
    ArrayList<String> newGroupArr;
    ArrayList<String> selectedGroupList;
    int groupCnt;

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
        setContentView(R.layout.pie_view);
        PieSpinner1 = (Spinner) findViewById(R.id.PieSpinner1);


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
        PieSpinner1.setAdapter(adapter);
        // 4. 리스너 등록
        PieSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                for(int k=0;k<selectedGroupList.size();k++){
                    Log.d("selectedGroupList",String.valueOf(selectedGroupList.get(k)));
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
                    for(int k=0;k<countArray.length;k++){
                        Log.d("countArray[]",String.valueOf(countArray[k]));
                    }
                    fillPieChart();
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

            LinearLayout layout = (LinearLayout) findViewById(R.id.chart_pie);

            mChartView = ChartFactory.getPieChartView(PieView.this, mSeries, mRenderer);

            mRenderer.setClickEnabled(true);

            mRenderer.setSelectableBuffer(10);

            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,

                    LinearLayout.LayoutParams.FILL_PARENT));

        } else {

            mChartView.repaint();

        }



    }


    public void fillPieChart() {
        mSeries.clear();
        for (int i = 0; i < selectedGroupList.size(); i++) {

            mSeries.add(selectedGroupList.get(i) + "(count : " + (String.valueOf(countArray[i]))+")", countArray[i]);


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