package com.example.administrator.DataCollectionAndAnalysis;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ButtonList extends Activity {
    ArrayList<String> arrayList = new ArrayList<>();
    String path = "/sdcard/myproject2/",selectedGroupName;
    static final String FILE_NAME = "exam.txt";
    static final String FILE_NAME2 = "countInfo.txt";
    String[] listInfoArray;//txt에서 읽은 문자를 개행문자로 잘라 저장할배열
    Spinner spinner1;

    File file = new File(path + FILE_NAME);
    File file2 = new File(path + FILE_NAME2);
    ArrayList<String> newGroupArr;
    int groupCnt;
    //File file2 = new File(path + FILE_NAME2);
    Button sendDataBtn,goBackBtn;
    Button[] btns = new Button[8];
    EditText[] editTextses = new EditText[8];
    ArrayList<String> selectedGroupList = new ArrayList<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_list);
        sendDataBtn =  (Button) findViewById(R.id.sendDataBtn);
        goBackBtn =  (Button) findViewById(R.id.goBackBtn);

        for (int k = 0; k < 8; k++) {
            btns[k] = (Button) findViewById(R.id.myBtn1 + k);//Button 등록.
            editTextses[k] = (EditText) findViewById(R.id.et1 + k);//EditText 등록.
        }

        spinner1 = (Spinner) findViewById(R.id.spinner1);


        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String listInfo = new String(buffer);
            listInfoArray = listInfo.split("#");
            fis.close();
        } catch (Exception e) {
            Log.e("File", "Error=" + e);
        }

        Log.d("listInfoArray.length", String.valueOf(listInfoArray.length));

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
            //Log.d("groupName",groupName);
        }

        // HashSet 데이터 형태로 생성되면서 중복 제거됨
        LinkedHashSet hs = new LinkedHashSet(groupArr);
        // ArrayList 형태로 다시 생성
        newGroupArr = new ArrayList<String>(hs);
        groupCnt = newGroupArr.size();
        Log.d("newGroupArr.size()", String.valueOf(newGroupArr.size()));


        // 2. 어뎁터 작성
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, newGroupArr);
        // 3. 어뎁터 결합
        spinner1.setAdapter(adapter);
        // 4. 리스너 등록
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroupName = newGroupArr.get(position);
                // 앞부분을 버튼등록
                ArrayList<String> selectedGroupList = new ArrayList<String>();
                for (int i = 0; i < listInfoArray.length; i++) {

                    if (listInfoArray[i].contains(selectedGroupName)) {
                        String arrElement = listInfoArray[i];
                        int idx = arrElement.indexOf("@");
                        String myName = arrElement.substring(0, idx);
                        Log.d("myName", myName);
                        selectedGroupList.add(myName);
                    }
                }

                Log.d("selectedGroupList", String.valueOf(selectedGroupList.size()));

                for (int i = 0; i < selectedGroupList.size(); i++) {
                    btns[i] = (Button) findViewById(R.id.myBtn1 + i);//Button 등록.
                    btns[i].setText(selectedGroupList.get(i));
                    btns[i].setVisibility(View.VISIBLE);//입력한 수만큼 보이도록한다.
                    editTextses[i].setVisibility(View.VISIBLE);//입력한 수만큼 보이도록한다.
                }
                for (int i = selectedGroupList.size(); i < 8; i++) {
                    btns[i].setVisibility(View.INVISIBLE);
                    editTextses[i].setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택하지 않은 경우
            }
        });
    }

    public void increaseCnt(View view){
        Button b = (Button)view;
        String elementInfo = b.getText().toString()+",";
        //Toast.makeText(getApplicationContext(),color,Toast.LENGTH_SHORT).show();

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(file2, true));
            pw.write(elementInfo);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(pw != null) pw.close();
        }
    }

   public void SendData(View view){
        PrintWriter pw = null;
        for (int i = 0; i < btns.length; i++) {

            if(!editTextses[i].getText().toString().isEmpty()) {
                int enteredCount = Integer.parseInt(editTextses[i].getText().toString());

                //Toast.makeText(getApplicationContext(),String.valueOf(enteredCount),Toast.LENGTH_SHORT).show();

                String s = btns[i].getText().toString()+",";

                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                for (int j = 0; j < enteredCount; j++) {
                    try {
                        pw = new PrintWriter(new FileWriter(file2, true));
                        pw.write(s);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (pw != null) pw.close();
                    }
                }

            }
            editTextses[i].setText("");
        }
    }


    public void goBack(View view){
        finish();
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
