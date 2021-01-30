package com.example.excalendar2;

import android.content.Context;

import io.realm.Realm;

public class DBHelper {

    private Realm realm;
    private Context context;
    private Memo memo;

    public DBHelper(Context context){
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }
}
