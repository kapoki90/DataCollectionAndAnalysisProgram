package com.example.administrator.DataCollectionAndAnalysis;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * Created by kapok on 2017-03-09.
 */

public class TableVIew extends Activity {
    List<HashMap<String, String>> list;// 데이터의 모양
    ArrayList<String> arrayList = new ArrayList<>();
    String[] array;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    int[] countArray;//각 행의 카운트를 저장할 배열
    String path = "/sdcard/myproject2/",selectedGroupName;
    static final String FILE_NAME = "exam.txt";
    static final String FILE_NAME2 = "countInfo.txt";

    File file = new File(path + FILE_NAME);
    File file2 = new File(path + FILE_NAME2);
    String[] listInfoArray;

    //EditText output;
    ListView myListView;
    Spinner tableSpinner1;

    ArrayList<String> newGroupArr;
    int groupCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_view);
        myListView = (ListView)findViewById(R.id.myListView);
        tableSpinner1 = (Spinner) findViewById(R.id.tableSpinner1);

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
        tableSpinner1.setAdapter(adapter);
        // 4. 리스너 등록
        tableSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list = new ArrayList<>();	// 데이터의 모양
                //Log.d("sel","sel");
                selectedGroupName = newGroupArr.get(position);
                ArrayList<String> selectedGroupList = new ArrayList<String>();
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

                    Log.d("GroupList.size()",String.valueOf(selectedGroupList.size()));
                    // 데이터를 만들자
                    for(int k=0 ; k < selectedGroupList.size() ; k++) {
                        // 맵 객체 생성
                        HashMap<String, String> map = new HashMap<>();
                        map.put("listName", selectedGroupList.get(k));
                        map.put("count", String.valueOf(countArray[k]));
                        list.add(map);		// 리스트에 추가
                        Log.d("listName", selectedGroupList.get(k));
                        Log.d("count", String.valueOf(countArray[k]));
                    }
                    showList();
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

    protected void showList() {
        // 데이터 두 개 짜리를 붙이려면 SimpleAdapter를 이용한다.
        SimpleAdapter adapter2 = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"listName", "count"}, new int[]{android.R.id.text1, android.R.id.text2});
        myListView.setAdapter(adapter2);

    }

    public void goBack(View view){
        finish();
    }

}