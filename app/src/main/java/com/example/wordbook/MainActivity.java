package com.example.wordbook;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20191011000340751";
    private  SQLiteDatabase mDatabase;
    private static final String SECURITY_KEY = "1Be_XwfAsvkrGta1IPGN";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    replaceFragment(new main_fragment(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight()));
                    return true;
                case R.id.navigation_dashboard:
                    replaceFragment(new WordList(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight()));
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
            }
            return false;
        }
    };
    public void  replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment,fragment);
        transaction.commit();
    }
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mm,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()){
            case R.id.help:
                Toast.makeText(this,"你点击了帮助",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Log.i("wid and height",String.valueOf(display.getHeight())+" "+String.valueOf(display.getWidth()));

        mDatabase = new SQLiteDbHelper(this).getWritableDatabase();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        replaceFragment(new main_fragment(display.getWidth(),display.getHeight()));
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String f = sharedPreferences.getString("flag","");
        if(f.equals("")){
            initDB();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("flag","YES");
            editor.commit();
        }


    }
    public void initDB()
    {
        final String init = "Christmas,coat,corn,cow,door,egg,farm";
        final String[] initWord = init.split(",");
        final String[] res = new String[initWord.length];
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<initWord.length;i++) {
                    res[i] = toChinese(initWord[i]);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id",new Date().getTime());
                    contentValues.put("english",initWord[i]);
                    contentValues.put("chinese",res[i]);
                    mDatabase.insert(SQLiteDbHelper.TABLE_STUDENT, null, contentValues);
                }
                for (int i=0;i<initWord.length;i++)
                    Log.i("translateRes",initWord[i]+":"+res[i]);

            }
        }).start();
    }

    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        try{
            final StringBuffer buffer = new StringBuffer();
            while (start > -1) {
                end = dataStr.indexOf("\\u", start + 2);
                String charStr = "";
                if (end == -1) {
                    charStr = dataStr.substring(start + 2, dataStr.length());
                } else {
                    charStr = dataStr.substring(start + 2, end);
                }
                char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
                buffer.append(new Character(letter).toString());
                start = end;
            }
            return buffer.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "无释义";
    }



    public static String toChinese(String e){
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String query = e;
        String r = api.getTransResult(query, "auto", "zh");
//          System.out.println(r);
        String error = r.substring(2,7);
        String[] re = r.split(":");
        String res = re[re.length-1];
        res = res.substring(1,res.length()-4);
        if(!error.equals("error")) return decodeUnicode(res);
        else return "无释义";

    }
    public static String toEnglish(String c){
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String query = c;
        String r = api.getTransResult(query, "auto", "en");
//        System.out.println(r);
        String error = r.substring(2,7);
        String[] re = r.split(":");
        String res = re[re.length-1];
        res = res.substring(1,res.length()-4);
        if(!error.equals("error")) return res;
        else return "无释义";
    }

}
