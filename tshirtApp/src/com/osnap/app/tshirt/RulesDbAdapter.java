/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.osnap.app.tshirt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to retrive and insert new rules with its filters
 */
public class RulesDbAdapter {

    public static final String KEY_ROWID = "_id";

    public static final String KEY_TAG = "tag";
    public static final String KEY_FILTER = "filter";
    public static final String KEY_RULE = "rule";
    public static final String KEY_OUTPUT = "output";

    public static final String KEY_CONNECTION = "connection";

    private static final String TAG = "RulesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_RULES = "create table rules (_id integer primary key autoincrement, rule text not null, output text not null);";
    private static final String DATABASE_CREATE_FILTER = "create table filters (_id integer primary key autoincrement, tag text, filter text, connection integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_RULES = "rules";
    private static final String DATABASE_TABLE_FILTERS = "filters";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_FILTER);
            db.execSQL(DATABASE_CREATE_RULES);

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

//            db.execSQL("DROP TABLE IF EXISTS rules");
//            db.execSQL("DROP TABLE IF EXISTS filters");
//            onCreate(db);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS rules");
            db.execSQL("DROP TABLE IF EXISTS filters");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public RulesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws android.database.SQLException if the database could be neither opened or created
     */
    public RulesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    
    public long createRule(String rule, String output){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RULE, rule);
        initialValues.put(KEY_OUTPUT, output);


        return mDb.insert(DATABASE_TABLE_RULES, null, initialValues);
    }

    public long createRule(String rule, String output, String[] filters){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RULE, rule);
        initialValues.put(KEY_OUTPUT, output);

        long rowId = mDb.insert(DATABASE_TABLE_RULES, null, initialValues);

        for (int i = 0; i < filters.length; i++) {
            initialValues = new ContentValues();
            initialValues.put(KEY_FILTER, filters[i]);
            
            initialValues.put(KEY_CONNECTION, rowId);

            mDb.insert(DATABASE_TABLE_FILTERS, null, initialValues);


        }

        return rowId;
    }
    public Cursor fetchAllRules() {
        return mDb.query(DATABASE_TABLE_RULES, new String[] {KEY_ROWID, KEY_RULE,
                KEY_OUTPUT}, null, null, null, null, null);
    }



    public boolean updateRule(long row, String rule, String output) {
        ContentValues values = new ContentValues();
        values.put(KEY_RULE, rule);
        values.put(KEY_OUTPUT, output);

        return mDb.update(DATABASE_TABLE_RULES, values, KEY_ROWID + "=" + row, null) > 0;
    }
    public boolean updateRule(long row, String rule, String output, String[] filters) {
        ContentValues values = new ContentValues();
        deleteFiltersConnectedTo(row);

        for (int i = 0; i < filters.length; i++) {
            values = new ContentValues();
            values.put(KEY_FILTER, filters[i]);

            values.put(KEY_CONNECTION, row);

            mDb.insert(DATABASE_TABLE_FILTERS, null, values);


        }
        values = new ContentValues();
        values.put(KEY_RULE, rule);
        values.put(KEY_OUTPUT, output);



        return mDb.update(DATABASE_TABLE_RULES, values, KEY_ROWID + "=" + row, null) > 0;
    }

    private boolean deleteFiltersConnectedTo(long row) {
        return mDb.delete(DATABASE_TABLE_FILTERS, KEY_CONNECTION + "=" + row,null) > 0;
    }


    public Cursor fetchAllFiltersFromRule(long ruleID) {
        return mDb.query(DATABASE_TABLE_FILTERS, new String[] {KEY_ROWID, KEY_TAG,
                KEY_FILTER}, KEY_CONNECTION + "=" + ruleID, null, null, null, null);
    }
}
