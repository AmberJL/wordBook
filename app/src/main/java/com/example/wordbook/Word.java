package com.example.wordbook;

public class Word {
    String Chinese;
    String English;
    Word(String c,String e){
        this.Chinese=c;
        this.English=e;
    }

    public String getChinese() {
        return Chinese;
    }

    public String getEnglish() {
        return English;
    }
}
