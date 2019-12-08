package com.example.wordbook;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.wordbook.demo.DemoException;
import com.example.wordbook.demo.TtsMain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class WordList extends Fragment {
    SQLiteDatabase mDataBase;
    ListView listView;
    ArrayList<Word> arrayList;
    SearchView input;
    int h;
    int w;
    WordList(){

    }
    WordList(int w,int h){
        this.h = h;
        this.w = w;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view;
        if(h>w){
            view = inflater.inflate(R.layout.word_fragment,container,false);
        }else{
            view = inflater.inflate(R.layout.heng,container,false);
        }

        listView = view.findViewById(R.id.wordList);

        return view;
    }
    public void init(){
        arrayList = new ArrayList<Word>();
        Cursor cursor = mDataBase.query("word",null,
                "chinese like '%"+"%"+"%'",
                null, null, null, null);
        while(cursor.moveToNext()){

            String c = cursor.getString(1);
            String e = cursor.getString(2);
            Log.i("res",c+"   "+e);
            arrayList.add(new Word(c,e));
        }
        cursor.close();
    }
    public void re(){
        init();
        listAdapter adapter = new listAdapter(getActivity(),R.layout.word_item,arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataBase = new SQLiteDbHelper(getActivity()).getWritableDatabase();
        input = getActivity().findViewById(R.id.query);
        input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")){
                    arrayList = new ArrayList<Word>();
                    Cursor cursor = mDataBase.query("word",null,
                            "english like '%"+newText+"%'",
                            null, null, null, null);
                    while(cursor.moveToNext()){

                        String c = cursor.getString(1);
                        String e = cursor.getString(2);
                        Log.i("res",c+"   "+e);
                        arrayList.add(new Word(c,e));
                    }
                    cursor.close();
                    listAdapter adapter = new listAdapter(getActivity(),R.layout.word_item,arrayList);
                    listView.setAdapter(adapter);
                }else{
                    re();
                }
                return true;
            }
        });
        final ListView listView = getActivity().findViewById(R.id.wordList);

        init();
        listAdapter adapter = new listAdapter(getActivity(),R.layout.word_item,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Word w = arrayList.get(position);
                final String e = w.getEnglish();
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
                new AlertDialog.Builder(getActivity()).setTitle("如何操作"+w.getEnglish())
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDataBase.delete("word","english = '"+w.getEnglish()+"'",null);
                                re();
                            }
                        })
                        .setNegativeButton("修改", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());         // Get the layout inflater
                                final LayoutInflater inflater = getLayoutInflater();
                                final View view1 = inflater.inflate(R.layout.log,null);
                                final EditText english = (EditText) view1.findViewById(R.id.UserId);
                                final EditText chinese = (EditText)view1.findViewById(R.id.UsrPwd);
                                english.setText(w.getEnglish());
                                chinese.setText(w.getChinese());
                                builder.setView(view1).setTitle("修改单词").setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String s = english.getText().toString();
                                        String b = chinese.getText().toString();
                                        mDataBase.delete("word","english = '"+w.getEnglish()+"'",null);
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("id",new Date().getTime());
                                        contentValues.put("english",s);
                                        contentValues.put("chinese",b);
                                        mDataBase.insert("word",null,contentValues);
                                        re();


                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getActivity(),"取消修改",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.show();
                            }
                        }).show();

            }
        });

    }
}
