package com.zh.phototagger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {
    private SQLiteDatabase db;

    public DatabaseHelper(SQLiteDatabase db) {
        this.db = db;
        createTables();
    }

    private void createTables() {
        db.execSQL("DROP TABLE IF EXISTS Photos;");
        db.execSQL("DROP TABLE IF EXISTS Tags;");

        db.execSQL("CREATE TABLE IF NOT EXISTS Photos (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " location TEXT NOT NULL UNIQUE," +
                " size INTEGER DEFAULT 0" +
                ") ;");

        db.execSQL("CREATE TABLE IF NOT EXISTS Tags (" +
                "id INTEGER NOT NULL," +
                "tag TEXT NOT NULL," +
                "FOREIGN KEY (id) REFERENCES Photos(id)" +
                ") ;" );
    }

    public long insertPhoto(String location, long size) {
        ContentValues vals = new ContentValues();
        vals.put("location", location);
        vals.put("size", size);
        return db.insert("Photos", null, vals);
    }

    public void insertTag(int id, String tag) {
        db.execSQL("INSERT INTO Tags VALUES ( " + id + ", '" + tag + "');");
    }

    public void insertTag(long id, String tag) {
        db.execSQL("INSERT INTO Tags VALUES ( " + id + ", '" + tag + "');");
    }

    public String getPhoto(int size) {
        long lower = Math.round(size * .75);
        long higher = Math.round(size * 1.25);
        String[] selectionArgs = new String[2];
        selectionArgs[0] = Long.toString(lower);
        selectionArgs[1] = Long.toString(higher);
        Cursor c = db.rawQuery("SELECT location FROM Photos WHERE size >= ? AND size <= ?;", selectionArgs);
        String location = getLocation(c);
        c.close();
        return location;
    }

    public String getPhoto(String tag) {
        String[] selectionArgs = new String[1];
        selectionArgs[0] = tag;
        Cursor c = db.rawQuery("SELECT location FROM Photos WHERE id IN " +
                "(SELECT id FROM Tags WHERE tag=?);", selectionArgs);
        String location = getLocation(c);
        c.close();
        return location;
    }

    public String getPhoto(int size, String tag) {
        String[] selectionArgs = new String[2];
        selectionArgs[0] = Integer.toString(size);
        selectionArgs[1] = tag;
        Cursor c = db.rawQuery("SELECT location FROM Photos WHERE size= ? AND id IN " +
                "(SELECT id FROM Tags WHERE tag=?);", selectionArgs);

        String location = getLocation(c);
        c.close();
        return location;
    }

    public String getLocation(Cursor c) {
        String s = "";
        while(c.moveToNext()) {
            for (int i = 0; i < c.getColumnCount(); i++) {
                s += c.getString(i);
                if (s.length() > 0) break;
            }
        }
        return s;
    }
}