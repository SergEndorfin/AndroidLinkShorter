package com.first_app.linkshorter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    public static int DB_VERSION = 28;
    private static final String DB_NAME = "link_shorter";
    private static final String DB_TABLE_NAME = "all_links";

    private static final String DB_LONG_LINK_COLUMN = "long_link";
    private static final String DB_SHORT_LINK_COLUMN = "short_link";



    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        System.out.println("*******DB********* == " + DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL);", DB_TABLE_NAME, DB_LONG_LINK_COLUMN, DB_SHORT_LINK_COLUMN);
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlQuery = String.format("DROP TABLE IF EXISTS %s", DB_TABLE_NAME);
        db.execSQL(sqlQuery);
        onCreate(db);
    }

    public void insertData(String longLink, String shortLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_LONG_LINK_COLUMN, longLink);
        values.put(DB_SHORT_LINK_COLUMN, shortLink);
        db.insertWithOnConflict(DB_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

//    public void deleteDataBase() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(DB_TABLE_NAME, null, null);
//        System.out.println(" ---------------- сработало - deleteDataBase()");
//        db.close();
//    }

    public ArrayList<String> getAllShortLinksFromDB() {
        ArrayList<String> allLinks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE_NAME, new String[]{DB_LONG_LINK_COLUMN, DB_SHORT_LINK_COLUMN}, null, null, null, null, null);
        while (cursor.moveToNext()){
//            int longLinkIndex = cursor.getColumnIndex(DB_LONG_LINK_COLUMN);
            int shortLinkIndex = cursor.getColumnIndex(DB_SHORT_LINK_COLUMN);
            allLinks.add(cursor.getString(shortLinkIndex));
        }
        cursor.close();
        db.close();
        return allLinks;
    }

    public boolean getDbShortLink(String shortLink) {
        String linkFromDb = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE_NAME, new String[]{DB_SHORT_LINK_COLUMN}, null, null, null, null, null);
        while (cursor.moveToNext()){
            int shortLinkIndex = cursor.getColumnIndex(DB_SHORT_LINK_COLUMN);
            linkFromDb = cursor.getString(shortLinkIndex);
            if (shortLink.matches(linkFromDb))
                return true;
        }
        return false;
    }

    public String getLongLinkByShort(String shortLink) {
        String linkFromDb = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE_NAME, new String[]{DB_LONG_LINK_COLUMN, DB_SHORT_LINK_COLUMN}, null, null, null, null, null);
        while (cursor.moveToNext()){
            int longLinkIndex = cursor.getColumnIndex(DB_LONG_LINK_COLUMN);
            int shortLinkIndex = cursor.getColumnIndex(DB_SHORT_LINK_COLUMN);
            linkFromDb = cursor.getString(shortLinkIndex);
            if (shortLink.matches(linkFromDb))
                return cursor.getString(longLinkIndex);
        }
        return "Ссылка в базе данных не найдена!";
    }

}
