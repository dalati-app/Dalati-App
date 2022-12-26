package com.dalati;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dalati.ui.models.Category;

import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "db";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "categories";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our course name column
    private static final String NAME_EN_COL = "name_en";
    private static final String NAME_AR_COL = "name_ar";

    // below variable id for our course duration column.
    private static final String TEXT_ID_COL = "text_id";
    SQLiteDatabase db = this.getWritableDatabase();

    // below variable for our course description column.

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
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
                + NAME_EN_COL + " TEXT,"
                + NAME_AR_COL + " TEXT,"
                + TEXT_ID_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
        System.out.println("Moha :running");
    }

    public void table_Creation() {
        try {
            String SQl = "create table categories (id varchar2(30), name_en varchar2(30),name_ar varchar2(30))";
            db.execSQL(SQl);

        } catch (Exception e) {
            System.out.println("Errori:" + e.getMessage());
        }
    }

    // this method is use to add new course to our sqlite database.
    public void addCategories(List<Category> categoryList) {
        table_Creation();

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.


        // on below line we are creating a
        // variable for content values.
        for (int i = 0; i < categoryList.size(); i++) {
            String Query = "Select * from " + TABLE_NAME + " where name_en LIKE" + categoryList.get(i).getId();
            Cursor cursor = db.rawQuery(Query, null);

            if (cursor.getCount() <= 0) {
                ContentValues values = new ContentValues();
                // on below line we are passing all values
                // along with its key and value pair.
                values.put(NAME_AR_COL, categoryList.get(i).getNameAr());
                values.put(NAME_EN_COL, categoryList.get(i).getNameEn());
                values.put("id", categoryList.get(i).getId());

                // after adding all values we are passing
                // content values to our table.
                db.insert(TABLE_NAME, null, values);
            }

        }


        // at last we are closing our
        // database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
