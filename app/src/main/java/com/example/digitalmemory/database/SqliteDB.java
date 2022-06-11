package com.example.digitalmemory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.digitalmemory.models.Memories;

import java.util.ArrayList;
import java.util.List;

public class SqliteDB extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "MemoriesDB";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "Memories";

    // below variable is for our id column.
    private static final String ID_COL = "ID";

    // below variable is for our course title column
    private static final String TITLE_COL = "title";

    // below variable id for our course content column.
    private static final String CONTENT_COL = "content";

    // below variable for our course location column.
    private static final String LOCATION_COL = "location";

    private static final String EMOJI_COL = "emoji";

    // below variable is for our course date column.
    private static final String DATE_COL = "date";



    // creating a constructor for our database handler.
    public SqliteDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE_COL + " TEXT,"
                + CONTENT_COL + " TEXT,"
                + LOCATION_COL + " TEXT,"
                + EMOJI_COL + " TEXT,"
                + DATE_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // this method is use to add new memory to our sqlite database.
    public void addNewMemory(String title, String content, String location, String emoji, String date) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(TITLE_COL, title);
        values.put(CONTENT_COL, content);
        values.put(LOCATION_COL, location);
        values.put(EMOJI_COL, emoji);
        values.put(DATE_COL, date);


        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    public void deleteMemory(Memories memory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID_COL + " = ?",
                new String[] { String.valueOf(memory.getID()) });
        db.close();
    }

    public int updateMemory(Memories memory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TITLE_COL, memory.getTitle());
        values.put(CONTENT_COL, memory.getContent());
        values.put(EMOJI_COL, memory.getEmoji());

        // updating row
        return db.update(TABLE_NAME, values, ID_COL + " = ?",
                new String[] { String.valueOf(memory.getID()) });
    }

    public List<Memories> getAllMemories() {
        List<Memories> memoriesList = new ArrayList<Memories>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Memories memory = new Memories();
                memory.setID(Integer.parseInt(cursor.getString(0)));
                memory.setTitle(cursor.getString(1));
                memory.setContent(cursor.getString(2));
                memory.setLocation(cursor.getString(3));
                memory.setEmoji(cursor.getString(4));
                memory.setDate(cursor.getString(5));


                memoriesList.add(memory);
            } while (cursor.moveToNext());
        }

        // return contact list
        return memoriesList;
    }


}