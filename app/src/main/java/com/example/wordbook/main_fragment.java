package com.example.wordbook;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.wordbook.demo.DemoException;
import com.example.wordbook.demo.TtsMain;

import java.io.IOException;


public class main_fragment extends Fragment {
    private static final String APP_ID = "20191011000340751";
    private static final String SECURITY_KEY = "1Be_XwfAsvkrGta1IPGN";
    private int Height;
    private int Weight;
    SQLiteDatabase mdatabase;
    main_fragment(){

    }
    main_fragment(int height ,int width){
        this.Height=width;
        this.Weight=height;
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
            View view;
        if (Height>Weight){
                view = inflater.inflate(R.layout.main_fragment,container,false);
            }else{
                view = inflater.inflate(R.layout.main_fragment,container,false);
            }


            return view;
        }
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    final String e = msg.getData().getString("e");
                    final String c = msg.getData().getString("c");
                    new AlertDialog.Builder(getActivity()).setTitle("查询结果\n"+msg.getData().getString("e")+"--中文释义 : "+msg.getData().getString("c"))
                            .setPositiveButton("添加到单词本", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ContentValues contentValues =new ContentValues();
                                    contentValues.put("chinese",c);
                                    contentValues.put("english",e);
                                    mdatabase.insert("word",null,contentValues);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TtsMain s = new TtsMain();
                                            try {
                                                s.start(e);
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            } catch (DemoException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("不添加", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

                    break;
            }
        }
    };

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Button search = getActivity().findViewById(R.id.sou);
            final EditText input = getActivity().findViewById(R.id.input);
            mdatabase = new SQLiteDbHelper(getActivity()).getWritableDatabase();
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!input.getText().toString().equals("")){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String e = input.getText().toString();
                                String c = toChinese(e);
                                Bundle bundle = new Bundle();
                                bundle.putString("e",e);
                                bundle.putString("c",c);
                                Message ms = new Message();
                                ms.what=1;
                                ms.setData(bundle);
                                handler.sendMessage(ms);


                            }
                        }).start();
                    }
                }
            });

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


}
