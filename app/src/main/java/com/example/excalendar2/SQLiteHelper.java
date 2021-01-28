package com.example.excalendar2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.http.SslCertificate;

import java.util.ArrayList;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String dbname = "Momo.db";
    private static final int version = 2;

    private static SQLiteHelper INSTANCE;
    private static SQLiteDatabase mDb;

    public static SQLiteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SQLiteHelper(context.getApplicationContext());
            mDb = INSTANCE.getWritableDatabase();
        }
        return  INSTANCE;
    }


    public void open() {
        if (mDb.isOpen() == false) {
            INSTANCE.onOpen(mDb);
        }
    }

    @Override
    public void close(){
        if(mDb.isOpen() == true) {
            INSTANCE.close();
        }
    }

    public SQLiteHelper(Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE memo (date TEXT, name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS memo");
    }

    public void insertMemo(String date, String memo){
        String sql = "INSERT INTO memo VALUES('" + date + "','" + memo + "');";
        mDb.execSQL(sql);
    }

    public void deleteMemo(String date){
        mDb.execSQL("DELETE FROM memo WHERE date = '" + date + "';");
    }


    public void modifyMemo(String date, String memo){ // 새 이름/ 기존이름
        mDb.execSQL("UPDATE memo SET memo = '" + memo + "' WHERE date = '" + date + "';");
    }


    //메모 불러오기
    public String loadMemoByDate(String date){
        String sql = "SELECT * FROM memo WHERE date = ? ORDER BY date DESC;";
        Cursor cursor = mDb.rawQuery(sql, new String[] {date});
        String memo = null;
        System.out.println(date + " 카운트: " + cursor.getCount());
        if(cursor.getCount() > 0){
            System.out.println("읽음! " + date);

            cursor.moveToFirst();
            memo = cursor.getString(0);
        }else{
            System.out.println("메모: " + memo);
        }
        cursor.close();
        return memo;
    }

   //한달 메모 불러오기 코드 만들기

}

