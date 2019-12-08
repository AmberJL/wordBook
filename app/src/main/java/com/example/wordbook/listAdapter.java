package com.example.wordbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class listAdapter extends ArrayAdapter<Word> {

    private int resourseId;
    public listAdapter(Context context, int resource, List<Word> objects) {
        super(context, resource, objects);
        this.resourseId=resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Word list = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourseId,parent,false);
        TextView chinese = view.findViewById(R.id.Chinese);
        TextView english = view.findViewById(R.id.English);
        chinese.setText(list.getEnglish());
        english.setText(list.getChinese());

        return view;

    }
}
