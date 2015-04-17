package edu.cs.sdsu.jonbeacher.assignment3a;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseDriver extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "professorRating_sqlite11";
    private static final int DATABASE_VERSION = 1;
    private static final String table_professor_list = "TABLE_PROFESSOR_LIST";
    private static final String table_professor_detail = "TABLE_PROFESSOR_DETAIL";
    private static final String table_professor_comments = "TABLE_PROFESSOR_COMMENTS";

    private static final String CREATE_TABLE_PROFESSOR_LIST =
            "CREATE TABLE IF NOT EXISTS " + table_professor_list + " ("
                    + "professorID" + " INTEGER PRIMARY KEY, "
                    + "professorFirstName" + " TEXT, "
                    + "professorLastName" + " TEXT "
                    + ");";

    private static final String CREATE_TABLE_PROFESSOR_DETAIL =
            "CREATE TABLE IF NOT EXISTS " + table_professor_detail + " ("
                    + "professorID" + " INTEGER PRIMARY KEY,"
                    + "professorFirstName" + " TEXT, "
                    + "professorLastName" + " TEXT, "
                    + "professorOffice" + " TEXT, "
                    + "professorPhone" + " TEXT, "
                    + "professorEmail" + " TEXT, "
                    + "professorRating" + " TEXT, "
                    + "professorTotalRatings" + " TEXT "
                    + ");";

    private static final String CREATE_TABLE_PROFESSOR_COMMENTS =
            "CREATE TABLE IF NOT EXISTS " + table_professor_comments + " ("
                    + "professorID" + " INTEGER PRIMARY KEY, "
                    + "professorComment" + " TEXT "
                    + ");";

    //professorLastNam
    public DatabaseDriver(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("drop table if exists TABLE_PROFESSOR_LIST;");
        //db.execSQL("drop table if exists TABLE_PROFESSOR_DETAIL;");
        //db.execSQL("drop table if exists TABLE_PROFESSOR_COMMENTS;");
        //
        db.execSQL(CREATE_TABLE_PROFESSOR_LIST);
        db.execSQL(CREATE_TABLE_PROFESSOR_DETAIL);
        //db.execSQL(CREATE_TABLE_PROFESSOR_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_professor_list);
        db.execSQL("DROP TABLE IF EXISTS " + table_professor_detail);
        //db.execSQL("DROP TABLE IF EXISTS " + table_professor_comments);
        // Create tables again in on create
        onCreate(db);
    }

}
